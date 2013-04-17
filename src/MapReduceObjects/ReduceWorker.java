package MapReduceObjects;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import Config.Configuration;

public class ReduceWorker {
	
	private boolean currentlyWorking = false;
	
	private ReduceListener listener;
	
	private Configuration curConfig;
	private ArrayList<Integer> curSplit;
	private String dataPath;
	
	private Thread thread;

	public ReduceWorker(Socket sock) {
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
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public void run() {
				if (!currentlyWorking) {
					currentlyWorking = true;
					ReduceProcessor440 jp = null;
					OutputCollecter dataCollect = new OutputCollecter();
					Class<?> K = curConfig.getOutputKeyClass();
					Class<?> V = curConfig.getOutputValueClass();
					
					BufferedReader br = null;
					try {
						for (int i = 0; i < curSplit.size(); i++) {
							br = Files.newBufferedReader(Paths.get(dataPath), Charset.defaultCharset());
							br.skip(curSplit.get(i));
							String record = br.readLine();
							br.close();
							String[] recordSplit = record.split(",");
							String keyStr = recordSplit[0].replace("<", "").trim();
							String valueStr = recordSplit[1].replace(">", "").trim();
							
							Object key = null;
							Object value = null;
							
							if (K.equals(String.class)) {
								key = keyStr;
							}
							
							if (V.equals(Integer.class)) {
								value = Integer.valueOf(valueStr);
							} else if (V.equals(String.class)) {
								value = valueStr;
							}
							
							dataCollect.collect(key, value);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}

					if (K.equals(String.class)) {
						if (V.equals(Integer.class)) {
							jp = new ReduceProcessor440<String, Integer>(curConfig, dataCollect);
						} else if (V.equals(String.class)) {
							jp = new ReduceProcessor440<String, String>(curConfig, dataCollect);
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
	
	@SuppressWarnings("rawtypes")
	private void writeOutputToFile(Configuration config, OutputCollecter output, int jobID) {
		System.out.println("About to write output file.");
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
