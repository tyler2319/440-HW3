package MapReduceObjects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MapWorkCommunicator {
	//Thread in which the process will be run
	private Thread thread;
	
	private HeartbeatChecker checkIfAlive;
	
	//socket that will be accepting connections
	private Socket sock;
	
	//Socket to call the worker to check if it's alive
	private Socket heartbeatSock;
    
	//boolean that determines whether the main thread should run
    private volatile boolean running;
    
    private InputTracker curWork;
    
    private MasterWorker master;
    
    private int id;
    
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
	
	public MapWorkCommunicator(Socket sock, ObjectOutputStream oos, ObjectInputStream ois, Socket heartbeatSock, MasterWorker master, int id) {
		this.sock = sock;
		this.oos = oos;
		this.ois = ois;
		this.heartbeatSock = heartbeatSock;
		checkIfAlive = new HeartbeatChecker(this.heartbeatSock, this);
		this.master = master;
		this.id = id;
	}
	
	public synchronized void start() throws Exception {
			if (thread != null) {
				throw new Exception("Listener already started.");
			}
			
			thread = new Thread(new Runnable() {
				/** run()
				 * 
				 * First establishes connection with node.
				 * Later sends commands and waits for responses.
				 */
				@Override
				public void run() {
					running = true;
					while(running) {
						if (!sock.isClosed()) {
							String request = null;
							try {
								request = (String) ois.readObject();
							} catch (IOException e) {
								shutDownSelf();
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							} 
							if (request == null) {}
							else if (request.equals("ResultPath")) {
								try {
									String path = (String) ois.readObject();
									master.jobFinished(path, id);
									running = false;
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									shutDownSelf();
								}
							}
							
							
						}
					}
				}
			});
			
			thread.start();
		}
	
	public boolean canWork() {
		return running;
	}
	
	public void sendWork(String configPath, InputTracker input, int workID) {
		try {
			curWork = input;
			if (oos == null) {
				oos = new ObjectOutputStream(sock.getOutputStream());
			}
			if (ois == null) {
				ois = new ObjectInputStream(sock.getInputStream());
			}
			oos.writeObject("Start job");
			oos.writeObject(workID);
			oos.writeObject(configPath);
			oos.writeObject(input.getInput());
			String response = (String)ois.readObject();
			//Resume the thread if we should send work again
			if (response.equals("Ready.")) {
				try {
					this.start();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if (response.equals("Worker busy.")) {
			}
		} catch (IOException e) {
			shutDownSelf();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public boolean checkIfAlive() {
		return checkIfAlive.isStillAlive();
	}
	
    /** stop()
     * 
     * Stops the process of listening for requests
     */
    public synchronized void stop() {
		if (thread == null) {
			return;
		}
		
		running = false;
		thread = null;
	}
	
	public void closeSocket() {
		try {
			sock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int getID() {
		return id;
	}
	
	public InputTracker getCurrentWork() {
		return curWork;
	}
	
	public void shutDownSockets() {
		try {
			checkIfAlive.getSocket().close();
			sock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void shutDownSelf() {
		master.shutDownMapWorker(id);
	}
}
