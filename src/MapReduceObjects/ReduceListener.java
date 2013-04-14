package MapReduceObjects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import Interfaces.InputSplit440;

public class ReduceListener {
	//Thread in which the process will be run
		private Thread thread;
			
		//boolean that determines whether the main thread should run
		private volatile boolean running;
		
		ReduceWorker worker;
		
		private Socket sock;
		
		private ObjectOutputStream oos;
		private ObjectInputStream ois;
		   
		public ReduceListener(Socket sock, ReduceWorker worker) {
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
								e.printStackTrace();
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							}
							
							if (request.equals("Still alive?")) {
								try {
									oos.writeObject("Yes");
								} catch (IOException e) {
									e.printStackTrace();
								}
							} else if (request.equals("Start job")) {
								try {
									int jobID;
									String configPath, recordPath;
									ArrayList<Integer> recordLocs;
									try {
										jobID = (Integer) ois.readObject();
										configPath = (String) ois.readObject();
										recordPath = (String) ois.readObject();
										recordLocs = (ArrayList<Integer>) ois.readObject();
										if (!worker.currentlyWorking()) {
											oos.writeObject("Ready.");
											try {
												worker.startJob(configPath, recordPath, recordLocs, jobID);
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
