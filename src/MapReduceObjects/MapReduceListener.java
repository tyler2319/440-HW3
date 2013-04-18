package MapReduceObjects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    
    private String jobName;
    
    //List of connections made to the master ProcessManager
    private ArrayList<SocketContainer> sockets = new ArrayList<SocketContainer>();
    
    //port, backlog for the Socket connection
    private int port;
    private int backlog;
    
    private int heartbeatPort;
    private int heartbeatBacklog;
    
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    
    public MapReduceListener(int port, int backlog, int heartbeatPort, int heartbeatBacklog) {
		this.port = port;
		this.backlog = backlog;
		this.heartbeatPort = heartbeatPort;
		this.heartbeatBacklog = heartbeatBacklog;
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
			@SuppressWarnings("unused")
			@Override
			public void run() {
				running = true;
				while(running) {
					Socket s = null;
					try {
						s = server.accept();
						SocketContainer tempCont = new SocketContainer(s);
						if (sockets.contains(tempCont)) {
							int index = sockets.indexOf(tempCont);
							s = sockets.get(index).getSocket();
							ois = sockets.get(index).getInputStream();
							oos = sockets.get(index).getOutputStream();
						}
						else {
							sockets.add(tempCont);
							ois = tempCont.getInputStream();
							oos = tempCont.getOutputStream();
						}
					} catch (IOException e) { }
					
					try {
						//if (ois == null) ois = new ObjectInputStream(s.getInputStream());
						//if (oos == null) oos = new ObjectOutputStream(s.getOutputStream());
						String command = (String) ois.readObject();
						if (command.equals("master")) {
							System.out.println("master called on port " + port);
							String config = (String) ois.readObject();
							MasterWorker mw = new MasterWorker(config);
							
							try {
								mw.start();
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else if (command.equals("mapworker")) {
							System.out.println("Map worker called on port" + port);
							MapWorker mw = new MapWorker(s, oos, ois, heartbeatPort, heartbeatBacklog);
							oos.writeObject("okay");
						} else if (command.equals("reduceworker")) {
							System.out.println("Reduce worker called on port" + port);
							ReduceWorker rw = new ReduceWorker(s, oos, ois);
						}
						else {
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
    
    public void setJobName(String s) {
    	jobName = s;
    }

}
