package MapReduceObjects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketContainer {
	private Socket s;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	
	public SocketContainer(Socket s) {
		this.s = s;
		try {
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ObjectOutputStream getOutputStream() {
		return oos;
	}
	
	public ObjectInputStream getInputStream() {
		return ois;
	}
	
	public Socket getSocket() {
		return s;
	}
	
	public boolean equals(SocketContainer s) {
		return (this.s.getRemoteSocketAddress().equals(s.getSocket().getRemoteSocketAddress()));
	}
}
