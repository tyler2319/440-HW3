package MapReduceObjects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;

import Config.Configuration;
import Interfaces.InputSplit440;

public class MasterWorker {
	
	private String configPath;
	private MapWorkCommunicator[] allMapWorkers;
	private LinkedList<MapWorkCommunicator> avaliableMapWorkers = new LinkedList<MapWorkCommunicator>();
	private LinkedList<InputSplit440> unperformedMaps = new LinkedList<InputSplit440>();
	
	private String[] completedMapPaths;
	private int nextOpenMapIndex = 0;
	
	private Thread thread;
	
	public MasterWorker(String configPath) {
		this.configPath = configPath;
	}
	
	public synchronized void start() throws Exception {
		if (thread != null) {
			throw new Exception("Master already working.");
		}

		thread = new Thread(new Runnable() {
			/** run()
			 * 
			 * Accepts connections and adds it to the managed list
			 */
			@Override
			public void run() {
				JobRunner440 jr = new JobRunner440(configPath);
				Configuration config = jr.getConfig();
				InputSplit440[] splits = jr.computeSplits();
				for (int i = 0; i < splits.length; i++) {
					unperformedMaps.add(splits[i]);
				}
				completedMapPaths = new String[splits.length];
				initMapWorkers(config);
				performMapWork();
			}
		});

		thread.start();
	}
	
	private void performMapWork() {
		while (unperformedMaps.size() > 0) {
			if (avaliableMapWorkers.size() > 0) {
				MapWorkCommunicator nextWorker = avaliableMapWorkers.poll();
				InputSplit440 nextMap = unperformedMaps.poll();
				nextWorker.sendWork(configPath, nextMap);
			}
		}
		System.out.println("All done with mapping!");
	}
	
	private void initMapWorkers(Configuration config) {
		allMapWorkers = new MapWorkCommunicator[config.getNumOfMappers()];
		String[] workerLocations = config.getWorkerLocations();
		for (int i = 0; i < config.getNumOfMappers(); i++) {
			String[] curWorkerLoc = workerLocations[i].split(":");
			String curWorkerHost = curWorkerLoc[0];
			int curWorkerPort = Integer.parseInt(curWorkerLoc[1]);
			Socket connection;
			ObjectOutputStream oos = null;
			//ObjectInputStream ois = null;
			
			try {
				connection = new Socket(curWorkerHost, curWorkerPort);
				oos = new ObjectOutputStream(connection.getOutputStream());
				//ois = new ObjectInputStream(connection.getInputStream());
				oos.writeObject("mapworker");
				MapWorkCommunicator mwp = new MapWorkCommunicator(connection, this, i);
				allMapWorkers[i] = mwp;
				avaliableMapWorkers.push(mwp);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void jobFinished(String path, int indexOfFinishedWorker) {
		completedMapPaths[nextOpenMapIndex] = path;
		nextOpenMapIndex += 1;
		avaliableMapWorkers.push(allMapWorkers[indexOfFinishedWorker]);
	}
}
