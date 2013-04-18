package MapReduceObjects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class HeartbeatResponder {
	//Thread in which the process will be run
	private Thread thread;

	//ServerSocket that will be accepting connections
    private ServerSocket server;
    
    //boolean that determines whether the main thread should run
    private volatile boolean running;
    
    //The server socket will only make 1 connection.
    Socket sock = null;
    
    //port, backlog for the Socket connection
    private int port;
    private int backlog;
    
    ObjectInputStream ois;
    ObjectOutputStream oos;
    
    public HeartbeatResponder(int port, int backlog) {
    	this.port = port;
    	this.backlog = backlog;
    }
    
	public synchronized void start() throws Exception {
		if (thread != null) {
			throw new Exception("Heartbeat Responder already started.");
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
					//Socket s = null;
					try {
						if (sock == null) {
							sock = server.accept();
						}
						if (ois == null) ois = new ObjectInputStream(sock.getInputStream());
						if (oos == null) oos = new ObjectOutputStream(sock.getOutputStream());
						String command = (String) ois.readObject();
						if (command.equals("Still alive?")) {
							oos.writeObject("Yes");
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
}
