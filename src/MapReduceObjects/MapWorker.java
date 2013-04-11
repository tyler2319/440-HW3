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
import Interfaces.Mapper;
import Interfaces.RecordReader440;

public class MapWorker {
	private WorkerListener listener;
	private boolean currentlyWorking = false;
	private String host;
	private int port;
	private int workerIndex;

	public static void main(String[] args) {
		String host = "hey there";
		int port = 45;
		new MapWorker(host, port);
	}
	
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
	
	//TODO Need to get this to work with generics */
	public void startJob(Configuration config, InputSplit440 inputSplit) {
		if (!currentlyWorking) {
			currentlyWorking = true;
			/* Set up the records to be read */
			RecordReader440 recordReader = config.getInputFormat().getRecordReader440(inputSplit);
			Record curRecord = recordReader.next();
			
			/* Set up the object that will hold the output key/ value pairs */
			OutputCollecter output = new OutputCollecter();
			
			/* Get a map class going that we can instantiate */
			Class<?> mapClass = config.getMapperClass();
			Constructor<?> mapConst = null;
			try {
				mapConst = mapClass.getConstructor();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
			
			/* Get a mapper up and running so we can run it */
			Mapper<?,?,?,?> map = null;
			try {
				map = (Mapper<?,?,?,?>) mapConst.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			
			while (curRecord != null) {
				map.map(curRecord.getKey(), curRecord.getValue(), output);
				curRecord = recordReader.next();
			}
			writeOutputToFile(output);
			currentlyWorking = false;
		}
		else throw new IllegalAccessException("Worker already working.");
	}
	
	private void writeOutputToFile(OutputCollecter output) {
		//TODO: Write the output to file
		sendResultToMaster(null);
	}
	
	public boolean currentlyWorking() {
		return currentlyWorking;
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
