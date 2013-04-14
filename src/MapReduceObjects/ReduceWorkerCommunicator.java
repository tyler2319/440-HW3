package MapReduceObjects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import Interfaces.InputSplit440;

public class ReduceWorkerCommunicator {
	//Thread in which the process will be run
	private Thread thread;

	//ServerSocket that will be accepting connections
	private Socket sock;
    
	//boolean that determines whether the main thread should run
    private volatile boolean running;
    
    private MasterWorker master;
    
    private int id;
    
    private boolean hasBeenInitialized = false;
    
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
	
	public ReduceWorkerCommunicator(Socket sock, MasterWorker master, int id) {
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
									master.reduceFinished(path, id);
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
	
	public void sendWork(String configPath, String recordPath, ArrayList<Integer> recordLocs, int workID) {
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
			oos.writeObject(recordPath);
			oos.writeObject(recordLocs);
			String response = (String)ois.readObject();
			//Resume the thread if we should send work again
			//TODO GET THIS TO RESUME LISTENING (what running = true is supposed to do)
			if (response.equals("Ready.")) {
				if (hasBeenInitialized) {
					running = true;
				}
				else {
					hasBeenInitialized = true;
					try {
						this.start();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}