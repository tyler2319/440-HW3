package MapReduceObjects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class HeartbeatChecker {
	
	private Socket sock;
	
	private Thread thread;
	
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	
	private boolean isStillAlive = false;
	
	public HeartbeatChecker(Socket sock) {
		this.sock = sock;
	}
	
	public boolean isStillAlive() {
		boolean result = false;
		try {
			result = start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		stop();
		return result;
	}
	
	public synchronized boolean start() throws Exception {
		if (thread != null) {
			throw new Exception("Heartbeat Checker already started.");
		}
		
		thread = new Thread(new Runnable() {
			/** run()
			 * 
			 * First establishes connection with node.
			 * Later sends commands and waits for responses.
			 */
			@Override
			public void run() {
				try {
					if (oos == null) {
						oos = new ObjectOutputStream(sock.getOutputStream());
					}
					if (ois == null) {
						ois = new ObjectInputStream(sock.getInputStream());
					}
					oos.writeObject("Still alive?");
					sock.setSoTimeout(5000);
					String response = "";
					try {
						response = (String)ois.readObject();
					//Worker didn't respond in time.
					} catch (SocketTimeoutException e) {
						isStillAlive = false;
					}
					//Resume the thread if we should send work again
					if (response.equals("Yes")) {
						isStillAlive = true;
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		});
		
		thread.start();
		thread.join();
		return isStillAlive;
	}
	
	/** stop()
     * 
     * Stops the process of listening for requests
     */
    public synchronized void stop() {
		if (thread == null) {
			return;
		}
		thread = null;
	}
}
