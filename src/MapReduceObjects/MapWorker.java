package MapReduceObjects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.net.UnknownHostException;

import Config.Configuration;
import Interfaces.InputFormat440;
import Interfaces.InputSplit440;

public class MapWorker {
	private WorkerListener listener;
	private boolean currentlyWorking = false;
	
	private Configuration curConfig;
	private InputSplit440 curSplit;
	
	private boolean isInputText;
	private boolean isInputImage;
	
	private Thread thread;
	
	/* needs to take whatever is needed to start listening */
	public MapWorker(Socket socket, ObjectOutputStream oos, ObjectInputStream ois, int heartbeatPort, int heartbeatBacklog) {
		listener = new WorkerListener(socket, this, oos, ois, heartbeatPort, heartbeatBacklog);
		
		try {
			listener.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("rawtypes")
	private void determineType(InputFormat440 input) {
		if (input.getClass().equals(DefaultObjects.TextInputFormat440.class)) {
			isInputText = true;
		} else if (input.getClass().equals(DefaultObjects.ImageInputFormat440.class)) {
			isInputImage = true;
		} else if (input.getClass().equals(DefaultObjects.TextWordInputFormat440.class)) {
			isInputText = true;
		}
	}
	
	@SuppressWarnings("rawtypes")
	private InputFormat440 getInputFormat(Configuration config) throws 
		NoSuchMethodException, SecurityException, InstantiationException,
		IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Class<?> inputClass = config.getInputFormat();
		Constructor<?> inputConst = null;
		inputConst = inputClass.getConstructor();
		
		InputFormat440<?, ?> input = null;
		input = (InputFormat440<?, ?>) inputConst.newInstance();
		input.configure(config);
		
		determineType(input);
		return input;
	}
	
	public synchronized void startJob(String configPath, InputSplit440 inputSplit, final int jobID) {
		/*if (jobID == 1 || jobID == 3) {
			sendResultToMaster("Error");
			return;
		}*/
		JobRunner440 jr = new JobRunner440(configPath);
		curConfig = jr.getConfig();
		curSplit = inputSplit;
		thread = new Thread(new Runnable() {
			@SuppressWarnings("rawtypes")
			public void run() {
				if (!currentlyWorking) {
					currentlyWorking = true;
					try {
						@SuppressWarnings("unused")
						InputFormat440 input = getInputFormat(curConfig);
					} catch (NoSuchMethodException | SecurityException
							| InstantiationException | IllegalAccessException
							| IllegalArgumentException
							| InvocationTargetException e1) {
						sendResultToMaster("Error");
						return;
					}
					MapProcessor440 jp = null;
					Class<?> K = curConfig.getOutputKeyClass();
					Class<?> V = curConfig.getOutputValueClass();
					
					if (isInputText) {
						if (K.equals(String.class)) {
							if (V.equals(Integer.class)) {
								jp = new MapProcessor440<Long, String, String, Integer>(curConfig, curSplit);
							} else if (V.equals(String.class)) {
								jp = new MapProcessor440<Long, String, String, String>(curConfig, curSplit);
							} else {
								sendResultToMaster("Error");
								return;
							}
						}
					} else if (isInputImage) {
						if (K.equals(String.class)) {
							if (V.equals(Integer.class)) {
								jp = new MapProcessor440<Long, byte[], String, Integer>(curConfig, curSplit);
							} else if (V.equals(String.class)) {
								jp = new MapProcessor440<Long, byte[], String, String>(curConfig, curSplit);
							} else {
								sendResultToMaster("Error");
								return;
							}
						}
						else {
							sendResultToMaster("Error");
							return;
						}
					}
						
					else {
						sendResultToMaster("Error");
						return;
					}
					
					OutputCollecter output = null;
					try {
						output = jp.runJob();
					} catch (NoSuchMethodException | SecurityException
							| InstantiationException | IllegalAccessException
							| IllegalArgumentException
							| InvocationTargetException e) {
						sendResultToMaster("Error");
						return;
					}
					//Means the output is actually a path.
					writeOutputToFile(curConfig, output, jobID);
					 
					currentlyWorking = false;
				} else {
					sendResultToMaster("busy");
				}
			}
		});
		
		thread.start();
	}
	
	public boolean currentlyWorking() {
		return currentlyWorking;
	}
	
	@SuppressWarnings("rawtypes")
	private void writeOutputToFile(Configuration config, OutputCollecter output, int jobID) {
		String outputPath = config.getOutputFilePath();
		String[] splitOnPeriod = outputPath.split("\\.");
		splitOnPeriod[0] += "_map" + jobID;
		
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
}
