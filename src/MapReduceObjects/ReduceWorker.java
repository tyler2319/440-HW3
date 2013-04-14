package MapReduceObjects;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import Config.Configuration;

public class ReduceWorker {
	
	private boolean currentlyWorking = false;
	
	private Socket sock;
	
	private ReduceListener listener;
	
	private Configuration config;
	private String configPath;
	private String recordPath;
	private int id;

	public ReduceWorker(Socket sock) {
		this.sock = sock;
		listener = new ReduceListener(sock, this);
		
		try {
			listener.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void startJob(String configPath, String recordPath, ArrayList<Integer> recordLocs, int jobID){
		if (!currentlyWorking) {
			currentlyWorking = true;
			
			this.configPath = configPath;
			JobRunner440 jr = new JobRunner440(configPath);
			config = jr.getConfig();
			this.recordPath = recordPath;
			this.id = jobID;
			System.out.println("Ready to start reducing!");
			sendResultToMaster("Hey there you crazy!");
			currentlyWorking = false;
		}
		else {
			try {
				throw new IllegalAccessException("Worker already working.");
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void sendResultToMaster(String result) {
		//Socket connection;
		ObjectOutputStream oos;
		listener.pause();
		try {
			//connection = socket;
			//oos = new ObjectOutputStream(connection.getOutputStream());
			oos = listener.getObjectOutputStream();
			oos.writeObject("ResultPath");
			oos.writeObject(result);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			//listener.start();
			listener.resume();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean currentlyWorking() {
		return currentlyWorking;
	}
}
