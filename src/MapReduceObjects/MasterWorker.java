package MapReduceObjects;

import java.util.LinkedList;

import Config.Configuration;
import Interfaces.InputSplit440;

public class MasterWorker {
	
	private String configPath;
	private MapWorker[] allMapWorkers;
	private LinkedList<MapWorker> avaliableMapWorkers = new LinkedList<MapWorker>();
	private LinkedList<InputSplit440> unperformedMaps = new LinkedList<InputSplit440>();
	
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
				
				initMapWorkers(config);
				//TODO init reduce workers
				//initReduceWorker();
				for (int i = 0; i < splits.length; i++) {
					unperformedMaps.add(splits[i]);
				}
				performMapWork(config); 
			}
		});

		thread.start();
	}
	
	private void performMapWork(Configuration config) {
		while (unperformedMaps.size() > 0) {
			if (avaliableMapWorkers.size() > 0) {
				MapWorker nextWorker = avaliableMapWorkers.poll();
				InputSplit440 nextMap = unperformedMaps.poll();
				//TODO write the performMap function
				performMap(nextWorker, config, nextMap);
			}
		}
	}
	
	private void initMapWorkers(Configuration config) {
		allMapWorkers = new MapWorker[config.getNumOfMappers()];
		for (int i = 0; i < config.getNumOfMappers(); i++) {
			//TODO The server (this) needs to open a connection with each of the
			//workers on initialization.
			/* Maybe make a new server socket for each worker? Then in the initialization,
			 * just wait to accept a connection from the client. At that point, the client
			 * is ready to do work. 
			 */
			avaliableMapWorkers.add(e);
		}
	}
}
