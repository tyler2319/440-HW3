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
		MapReduceListener mrl = new MapReduceListener(8888, 10);
		mrl.start();
		
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
			JobRunner440 jr = new JobRunner440(args[0]);
			InputSplit440[] splits =  jr.computeSplits();
		} else if (com.equals("monitor") && words.length == 1) {
			//MONITOR CODE
		} else if (com.equals("stop") && words.length == 1) {
			//STOP CODE
		} else {
			System.out.println("Command not " + com + " recognized.");
		}
	}
}
