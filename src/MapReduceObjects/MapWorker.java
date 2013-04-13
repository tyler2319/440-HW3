package MapReduceObjects;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;

import Config.Configuration;
import Interfaces.InputFormat440;
import Interfaces.InputSplit440;

public class MapWorker {
	private WorkerListener listener;
	private boolean currentlyWorking = false;
	private Socket socket;
	private int workerIndex;
	
	private Configuration curConfig;
	private InputSplit440 curSplit;
	
	private boolean isInputText;
	
	private Thread thread;
	
	//TODO Delete - This is for testing only
	public MapWorker() { }
	
	/* needs to take whatever is needed to start listening */
	public MapWorker(Socket socket) {
		this.socket = socket;
		listener = new WorkerListener(socket, this);
		
		try {
			listener.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void determineType(InputFormat440 input) {
		if (input.getClass().equals(DefaultObjects.TextInputFormat440.class)) {
			isInputText = true;
		}
	}
	
	private InputFormat440 getInputFormat(Configuration config) {
		Class<?> inputClass = config.getInputFormat();
		Constructor<?> inputConst = null;
		try {
			inputConst = inputClass.getConstructor();
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		} catch (SecurityException e1) {
			e1.printStackTrace();
		}
		
		InputFormat440<?, ?> input = null;
		try {
			input = (InputFormat440<?, ?>) inputConst.newInstance();
			input.configure(config);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		determineType(input);
		return input;
	}
	
	/*public synchronized void startJob(String configPath, InputSplit440 inputSplit) throws Exception {
		if (thread != null) {
			throw new Exception("Worker busy");
		}
		
		JobRunner440 jr = new JobRunner440(configPath);
		curConfig = jr.getConfig();
		curSplit = inputSplit;
		thread = new Thread(new Runnable() {
			public void run() {
				if (!currentlyWorking) {
					currentlyWorking = true;
					System.out.println("Working on job");
					InputFormat440 input = getInputFormat(curConfig);
					MapProcessor440 jp = null;
					Class<?> K = curConfig.getOutputKeyClass();
					Class<?> V = curConfig.getOutputValueClass();
					
					if (isInputText) {
						if (K.equals(String.class)) {
							if (V.equals(Integer.class)) {
								jp = new MapProcessor440<Long, String, String, Integer>(curConfig, curSplit);
							} else if (V.equals(String.class)) {
								jp = new MapProcessor440<Long, String, String, String>(curConfig, curSplit);
							}
						}
					}
					
					OutputCollecter output = jp.runJob();
					writeOutputToFile(curConfig, output);
					 
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
	}*/
	
	public void startJob(String configPath, InputSplit440 inputSplit) {
		JobRunner440 jr = new JobRunner440(configPath);
		curConfig = jr.getConfig();
		curSplit = inputSplit;
		if (!currentlyWorking) {
			currentlyWorking = true;
			InputFormat440 input = getInputFormat(curConfig);
			MapProcessor440 jp = null;
			Class<?> K = curConfig.getOutputKeyClass();
			Class<?> V = curConfig.getOutputValueClass();
			
			if (isInputText) {
				if (K.equals(String.class)) {
					if (V.equals(Integer.class)) {
						jp = new MapProcessor440<Long, String, String, Integer>(curConfig, curSplit);
					} else if (V.equals(String.class)) {
						jp = new MapProcessor440<Long, String, String, String>(curConfig, curSplit);
					}
				}
			}
			
			OutputCollecter output = jp.runJob();
			writeOutputToFile(curConfig, output);
			 
			currentlyWorking = false;
		} else {
			try {
				throw new IllegalAccessException("Worker already working.");
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean currentlyWorking() {
		return currentlyWorking;
	}
	
	private void writeOutputToFile(Configuration config, OutputCollecter output) {
		String outputPath = config.getOutputFilePath();
		String[] splitOnPeriod = outputPath.split("\\.");
		splitOnPeriod[0] += "_map" + workerIndex;
		
		if (splitOnPeriod.length == 2) {
			splitOnPeriod[1] = "." + splitOnPeriod[1];
		}
		
		String newPath = "";
		
		for (int i = 0; i < splitOnPeriod.length; i++) {
			newPath += splitOnPeriod[i];
		}
		
		//Path p = Paths.get(newPath);
		
		RecordWriter440 rw = new RecordWriter440(config, output, newPath);
		rw.writeOutput();
		
		sendResultToMaster(newPath);
	}
	
	private void sendResultToMaster(String result) {
		System.out.println("Want to send result to master.");
		Socket connection;
		ObjectOutputStream oos;
		listener.stop();
		try {
			connection = socket;
			oos = new ObjectOutputStream(connection.getOutputStream());
			oos.writeObject("ResultPath");
			oos.writeObject(result);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			//listener.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
