package MapReduceObjects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MapReduceListener {
	
	//Thread in which the process will be run
	private Thread thread;

	//ServerSocket that will be accepting connections
    private ServerSocket server;
    
    //boolean that determines whether the main thread should run
    private volatile boolean running;
    
    //List of connections made to the master ProcessManager
    private ArrayList<Socket> sockets = new ArrayList<Socket>();
    
    //port, backlog for the Socket connection
    private int port;
    private int backlog;
    
    public MapReduceListener(int port, int backlog) {
		this.port = port;
		this.backlog = backlog;
	}
    
    public synchronized void start() throws Exception {
		if (thread != null) {
			throw new Exception("MapReduceListener already started.");
		}

		server = new ServerSocket(port, backlog);
		thread = new Thread(new Runnable() {
			/** run()
			 * 
			 * Accepts connections and adds it to the managed list
			 */
			@Override
			public void run() {
				running = true;
				while(running) {
					Socket s = null;
					try {
						s = server.accept();
						sockets.add(s);
					} catch (IOException e) { }
					
					ObjectInputStream input = null;
					try {
						input = new ObjectInputStream(s.getInputStream());
						String command = (String) input.readObject();
						if (command.equals("master")) {
							System.out.println("master called on port " + port);
							String config = (String) input.readObject();
							MasterWorker mw = new MasterWorker(config);
							
							try {
								mw.start();
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else if (command.equals("mapworker")) {
							System.out.println("Map worker called on port" + port);
							MapWorker mw = new MapWorker(s);
						} else {
							throw new Exception("Invalid command.");
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

		thread.start();
	}
    
    public synchronized void stop() {
		if (thread == null) {
			return;
		}

		running = false;
		if (server != null) {
			try {
				server.close();
			} catch (Exception e) { }
		}
		thread = null;
	}

	public synchronized ArrayList<Socket> getScokets() {
		return sockets;
	}

}
