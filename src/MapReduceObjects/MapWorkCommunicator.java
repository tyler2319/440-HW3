package MapReduceObjects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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
    
    private boolean hasBeenInitialized = false;
	
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
							Object request = null;
							ObjectOutputStream oos = null;
							ObjectInputStream ois = null;
							try {
								oos = new ObjectOutputStream(sock.getOutputStream());
								ois = new ObjectInputStream(sock.getInputStream());
								request = (String) ois.readObject();
							} catch (IOException e) {
								e.printStackTrace();
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							}
							
							if (request.equals("ResultPath")) {
								System.out.println("We have a result path!");
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
	
	public void sendWork(String configPath, InputSplit440 input) {
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		try {
			oos = new ObjectOutputStream(sock.getOutputStream());
			ois = new ObjectInputStream(sock.getInputStream());
			oos.writeObject("Start job");
			oos.writeObject(configPath);
			oos.writeObject(input);
			String response = (String)ois.readObject();
			//Resume the thread if we should send work again
			if (response.equals("Ready.")) {
				if (hasBeenInitialized) running = true;
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
