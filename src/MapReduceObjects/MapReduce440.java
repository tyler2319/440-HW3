package MapReduceObjects;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import ClassLoader.ClassLoader440;
import Config.Configuration;
import Interfaces.InputSplit440;

public class MapReduce440 {
	
	private boolean isRunning = true;

	/** receiveCommands()
	 * 
	 * Runs the command prompt
	 * @throws Exception
	 */
	public void receiveCommands() throws Exception {
		String result = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		while(isRunning) {
			System.out.print("==> ");
			result = br.readLine();
			parseCommand(result);
		}
	}
	
	public void parseCommand(String command) {
		//Split command based on space
		String[] words = command.split(" ");

		//First word is the process/command
		String com = words[0];

		//Remaining words are process arguments
		String[] args = new String[words.length - 1];

		for (int i = 1; i < words.length; i++) {
			args[i-1] = words[i];
		}

		if (com.equals("start") && args.length == 1) {
			Configuration config = getConfig(args[0]);
			JobRunner440 jr = new JobRunner440(config);
			InputSplit440[] splits =  jr.computeSplits();
			MapWorker mw = new MapWorker();
			try {
				mw.startJob(config, splits[0]);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} else if (com.equals("monitor") && words.length == 1) {
			//MONITOR CODE
		} else if (com.equals("stop") && words.length == 1) {
			//STOP CODE
		} else {
			System.out.println("Command not " + com + " recognized.");
		}
	}
	
	public Configuration getConfig(String path) {
		String[] parts = path.split("/");
		String[] fileParts = parts[parts.length - 1].split("\\.");
		
		if (fileParts.length != 2 || !fileParts[1].equals("class")) {
			System.out.println("Configuration file must be a valid Java class");
			return null;
		}
		
		ClassLoader440 cl = new ClassLoader440();
		Class<?> configClass = cl.getClass(path, fileParts[0]);
		Constructor<?> configConst = null;
		try {
			configConst = configClass.getConstructor();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		
		Configuration config = null;
		try {
			config = (Configuration)configConst.newInstance();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return config;
	}
}
