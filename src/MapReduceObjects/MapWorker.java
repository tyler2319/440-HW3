package MapReduceObjects;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Path;

import Config.Configuration;
import Interfaces.InputFormat440;
import Interfaces.InputSplit440;

public class MapWorker {
	private WorkerListener listener;
	private boolean currentlyWorking = false;
	private String host;
	private int port;
	private int workerIndex;
	
	private boolean isInputText;

	public static void main(String[] args) {
		String host = "hey there";
		int port = 45;
		new MapWorker(host, port);
	}
	
	public MapWorker() { }
	
	/* needs to take whatever is needed to start listening */
	public MapWorker(String host, int port) {
		try {
			this.host = host;
			this.port = port;
			listener = new WorkerListener(new Socket(host, port), this);
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
			JobProcessor440 jp = null;
			Class<?> K = config.getOutputKeyClass();
			Class<?> V = config.getOutputValueClass();
			
			if (isInputText) {
				if (K.equals(String.class)) {
					if (V.equals(Integer.class)) {
						jp = new JobProcessor440<Long, String, String, Integer>(config, inputSplit);
					}
				}
			}
			
			OutputCollecter output = jp.runJob();
			writeOutputToFile(output);
			 
			currentlyWorking = false;
		}
		else throw new IllegalAccessException("Worker already working.");
	}
	
	public boolean currentlyWorking() {
		return currentlyWorking;
	}
	
	private void writeOutputToFile(OutputCollecter output) {
		//TODO: Write the output to file
		sendResultToMaster(null);
	}
	
	private void sendResultToMaster(Path result) {
		Socket connection;
		ObjectOutputStream oos;
		try {
			connection = new Socket(host, port);
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
