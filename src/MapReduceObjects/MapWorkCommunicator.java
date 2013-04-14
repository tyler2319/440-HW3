package MapReduceObjects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

import Interfaces.InputSplit440;


public class MapWorkCommunicator {
	//Thread in which the process will be run
	private Thread thread;

	//ServerSocket that will be accepting connections
	private Socket sock;
    
	//boolean that determines whether the main thread should run
    private volatile boolean running;
    
    private MasterWorker master;
    
    private int id;
    
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
	
	public MapWorkCommunicator(Socket sock, MasterWorker master, int id) {
		this.sock = sock;
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
								e.printStackTrace();
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							}
							
							if (request.equals("ResultPath")) {
								try {
									String path = (String) ois.readObject();
									master.jobFinished(path, id);
									running = false;
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							
							
						}
					}
				}
			});
			
			thread.start();
		}
	
	public void sendWork(String configPath, InputSplit440 input, int workID) {
		try {
			if (oos == null) {
				oos = new ObjectOutputStream(sock.getOutputStream());
			}
			if (ois == null) {
				ois = new ObjectInputStream(sock.getInputStream());
			}
			oos.writeObject("Start job");
			oos.writeObject(workID);
			oos.writeObject(configPath);
			oos.writeObject(input);
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
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public boolean checkIfAlive() {
		try {
			if (oos == null) {
				oos = new ObjectOutputStream(sock.getOutputStream());
			}
			if (ois == null) {
				ois = new ObjectInputStream(sock.getInputStream());
			}
			oos.writeObject("Still alive?");
			sock.setSoTimeout(5000);
			String response;
			try {
				response = (String)ois.readObject();
			//Worker didn't respond in time.
			} catch (SocketTimeoutException e) {
				sock.setSoTimeout(1000000);
				return false;
			}
			//Resume the thread if we should send work again
			if (response.equals("Yes")) {
				System.out.println("Map worker " + id + " alive!");
				try {
					this.start();
					return true;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		//Shouldn't ever get here!
		return false;
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
}
