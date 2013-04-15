package MapReduceObjects;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import Config.Configuration;
import Interfaces.InputFormat440;
import Interfaces.InputSplit440;

public class ReduceWorker {
	
	private boolean currentlyWorking = false;
	
	private Socket sock;
	
	private ReduceListener listener;
	
	private Configuration curConfig;
	private ArrayList<Integer> curSplit;
	private String dataPath;
	
	private Thread thread;
	
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
	
	public synchronized void startJob(String configPath, ArrayList<Integer> inputSplit, String dp, final int jobID) {
		JobRunner440 jr = new JobRunner440(configPath);
		curConfig = jr.getConfig();
		curSplit = inputSplit;
		dataPath = dp;
		thread = new Thread(new Runnable() {
			public void run() {
				if (!currentlyWorking) {
					currentlyWorking = true;
					ReduceProcessor440 jp = null;
					Class<?> K = curConfig.getOutputKeyClass();
					Class<?> V = curConfig.getOutputValueClass();

					if (K.equals(String.class)) {
						if (V.equals(Integer.class)) {
							jp = new ReduceProcessor440<String, Integer>(curConfig, curSplit, dataPath);
						} else if (V.equals(String.class)) {
							jp = new ReduceProcessor440<String, String>(curConfig, curSplit, dataPath);
						}
					}
					
					OutputCollecter output = jp.runJob();
					writeOutputToFile(curConfig, output, jobID);
					 
					currentlyWorking = false;
				} else {
					try {
						throw new IllegalAccessException("Worker already working.");
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		thread.start();
	}
	
	private void writeOutputToFile(Configuration config, OutputCollecter output, int jobID) {
		String outputPath = config.getOutputFilePath();
		String[] splitOnPeriod = outputPath.split("\\.");
		splitOnPeriod[0] += "" + jobID;
		
		if (splitOnPeriod.length == 2) {
			splitOnPeriod[1] = "." + splitOnPeriod[1];
		}
		
		String newPath = "";
		
		for (int i = 0; i < splitOnPeriod.length; i++) {
			newPath += splitOnPeriod[i];
		}
		
		RecordWriter440 rw = new RecordWriter440(config, output, newPath);
		rw.writeOutput();
		
		sendResultToMaster(newPath);
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
