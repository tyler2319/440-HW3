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
	private String masterHost;
	private int masterPort;
	private int workerIndex;
	
	private boolean isInputText;
	
	//TODO Delete - This is for testing only
	public MapWorker() { }
	
	/* needs to take whatever is needed to start listening */
	public MapWorker(String host, int port) {
		try {
			this.masterHost = host;
			this.masterPort = port;
			listener = new WorkerListener(new Socket(masterHost, masterPort), this);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			listener.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void determineType(InputFormat440 input) {
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
	
	public void startJob(Configuration config, InputSplit440 inputSplit) throws IllegalAccessException {
		if (!currentlyWorking) {
			currentlyWorking = true;
			
			InputFormat440 input = getInputFormat(config);
			MapProcessor440 jp = null;
			Class<?> K = config.getOutputKeyClass();
			Class<?> V = config.getOutputValueClass();
			
			if (isInputText) {
				if (K.equals(String.class)) {
					if (V.equals(Integer.class)) {
						jp = new MapProcessor440<Long, String, String, Integer>(config, inputSplit);
					} else if (V.equals(String.class)) {
						jp = new MapProcessor440<Long, String, String, String>(config, inputSplit);
					}
				}
			}
			
			OutputCollecter output = jp.runJob();
			writeOutputToFile(config, output);
			 
			currentlyWorking = false;
		}
		else throw new IllegalAccessException("Worker already working.");
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
		
		Path p = Paths.get(newPath);
		
		RecordWriter440 rw = new RecordWriter440(config, output, p);
		rw.writeOutput();
		
		//sendResultToMaster(p);
	}
	
	private void sendResultToMaster(Path result) {
		Socket connection;
		ObjectOutputStream oos;
		try {
			connection = new Socket(masterHost, masterPort);
			oos = new ObjectOutputStream(connection.getOutputStream());
			oos.writeObject("Result Path");
			oos.writeObject(result);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
