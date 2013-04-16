package MapReduceObjects;

import Config.Configuration;
import Interfaces.InputSplit440;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class WorkerListener {

	//Thread in which the process will be run
	private Thread thread;
		
	private HeartbeatResponder hbr;
	
	//boolean that determines whether the main thread should run
	private volatile boolean running;
	
	MapWorker worker;
	
	private Socket sock;
	
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	   
	public WorkerListener(Socket sock, MapWorker worker, ObjectOutputStream oos, ObjectInputStream ois, int heartbeatPort, int heartbeatBacklog) {
		hbr = new HeartbeatResponder(heartbeatPort, heartbeatBacklog);
		try {
			hbr.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.oos = oos;
		this.ois = ois;
		this.sock = sock;
		this.worker = worker;
	}
	
    public synchronized void start() throws Exception {
		if (thread != null) {
			throw new Exception("Listener already started.");
		}
		
		thread = new Thread(new Runnable() {
			/** run()
			 * 
			 * Listens for requests from the master.
			 * Interprets the requests and responds to them
			 */
			@Override
			public void run() {
				running = true;
				while(running) {
					if (!sock.isClosed()) {
						Object request = null;
						try {
							if (oos == null) oos = new ObjectOutputStream(sock.getOutputStream());
							if (ois == null) ois = new ObjectInputStream(sock.getInputStream());
							request = (String) ois.readObject();
						} catch (IOException e) {
							//e.printStackTrace();
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
						if (request == null) {}
						else if (request.equals("Start job")) {
							try {
								int jobID;
								String configPath;
								InputSplit440 split;
								try {
									jobID = (Integer) ois.readObject();
									configPath = (String) ois.readObject();
									split = (InputSplit440) ois.readObject();
									if (!worker.currentlyWorking()) {
										oos.writeObject("Ready.");
										try {
											worker.startJob(configPath, split, jobID);
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
									else oos.writeObject("Worker busy.");
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						} 
						
						/*else if (request.equals("Thanks for your work.")) {
							System.out.println("Worker shutting down.");
							try {
								ois.close();
								oos.close();
								sock.close();
								running = false;
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}*/
					}
				}
			}
		});
		
		thread.start();
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
    
    public synchronized void pause() {
    	running = false;
    }
    
    public synchronized void resume() {
    	running = true;
    }
    
    public ObjectOutputStream getObjectOutputStream() {
    	return oos;
    }
}
