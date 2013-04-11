package MapReduceObjects;


import java.util.LinkedList;

import Config.Configuration;
import Interfaces.InputSplit440;

public class MasterWorker {
	private Configuration config;
	private MapWorker[] allMapWorkers;
	private LinkedList<MapWorker> avaliableMapWorkers = new LinkedList<MapWorker>();
	private LinkedList<InputSplit440> unperformedMaps = new LinkedList<InputSplit440>();
	
	
	public MasterWorker(Configuration config, InputSplit440[] inputSplit) {
		this.config = config;
		initMapWorkers();
		//TODO init reduce workers
		//initReduceWorker();
		for (int i = 0; i < inputSplit.length; i++) {
			unperformedMaps.add(inputSplit[i]);
		}
		performMapWork();
	}
	
	private void performMapWork() {
		while (unperformedMaps.size() > 0) {
			if (avaliableMapWorkers.size() > 0) {
				MapWorker nextWorker = avaliableMapWorkers.poll();
				InputSplit440 nextMap = unperformedMaps.poll();
				//TODO write the performMap function
				performMap(nextWorker, config, nextMap);
			}
		}
	}
	
	private void initMapWorkers() {
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
