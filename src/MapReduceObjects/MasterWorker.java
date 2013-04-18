package MapReduceObjects;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import Config.Configuration;
import Interfaces.InputSplit440;

public class MasterWorker {
	
	private String configPath;
	private String intermediateFilepath;
	private Configuration config;
	private int numMapWorkers = 0;
	private MapWorkCommunicator[] allMapWorkers;
	private ReduceWorkerCommunicator[] allReduceWorkers;
	private LinkedList<MapWorkCommunicator> avaliableMapWorkers = new LinkedList<MapWorkCommunicator>();
	private LinkedList<ReduceWorkerCommunicator> avaliableReduceWorkers = new LinkedList<ReduceWorkerCommunicator>();
	private LinkedList<InputTracker> unperformedMaps = new LinkedList<InputTracker>();
	private InputTracker[] sentOutMapWork;
	private LinkedList<ArrayList<Integer>> unperformedReduces = new LinkedList<ArrayList<Integer>>();
	private int mapWorkIndex = 0;
	private int reduceWorkIndex = 0;
	private String[] completedMapPaths;
	private String[] completedReducePaths;
	private int nextOpenMapIndex = 0;
	private int nextOpenReduceIndex = 0;
	
	private MapReduceListener mrl;
	private JobContainer job;
	
	ScheduledExecutorService workerCheck = Executors.newSingleThreadScheduledExecutor();
	
	private ArrayList<Integer>[] recordsSplitToReduceWorkers;
	
	private Thread thread;
	
	public MasterWorker(String configPath, MapReduceListener mrl, JobContainer job) {
		this.configPath = configPath;
		this.mrl = mrl;
		this.job = job;
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
				config = jr.getConfig();
				job.setJobName(config.getJobName());
				InputSplit440[] splits = jr.computeSplits();
				for (int i = 0; i < splits.length; i++) {
					unperformedMaps.add(new InputTracker(splits[i]));
				}
				completedMapPaths = new String[splits.length];
				initMapWorkers();
				workerCheck.scheduleAtFixedRate(new Runnable() {
					  @Override
					  public void run() {
					    checkIfWorkersAlive();
					  }
					}, 0, 15, TimeUnit.SECONDS);
				performMapWork();
				workerCheck.shutdown();
				initReduce();
				performReduceWork();
				mrl.jobFinished(job.getJobID());
			}
		});

		thread.start();
	}
	
    /** stop()
     * 
     * Stops the process of listening for requests
     */
    public synchronized void stop() {
		if (thread == null) {
			return;
		}
		thread = null;
	}
    
	
	@SuppressWarnings("unchecked")
	private void initReduce() {
		//Initialize reduce variables
		recordsSplitToReduceWorkers = new ArrayList[config.getNumOfReducers()];
		completedReducePaths = new String[recordsSplitToReduceWorkers.length];
		for (int i = 0; i < recordsSplitToReduceWorkers.length; i++) {
			recordsSplitToReduceWorkers[i] = new ArrayList<Integer>();
		}
		/* First get the path of the intermediate file obtained
		 * by concatenating all the map files.
		 */
		String outputPath = config.getOutputFilePath();
		String[] splitOnPeriod = outputPath.split("\\.");
		splitOnPeriod[0] += "_intermediate";
		
		if (splitOnPeriod.length == 2) {
			splitOnPeriod[1] = "." + splitOnPeriod[1];
		}
		
		String newPath = "";
		
		for (int i = 0; i < splitOnPeriod.length; i++) {
			newPath += splitOnPeriod[i];
		}
		intermediateFilepath = newPath;
		
		/* Now, actually write that file. */
		BufferedWriter outWriter = null;
		int numCharsSeen = 0;
		try {
			outWriter = Files.newBufferedWriter(Paths.get(intermediateFilepath), Charset.defaultCharset());
		    for (String path : completedMapPaths) {
		    	BufferedReader br = null;
				br = Files.newBufferedReader(Paths.get(path), Charset.defaultCharset());
				String curLine;
		        try {
					while ( (curLine = br.readLine()) != null) {
						//First map the current record to a reduce worker
						//TODO Hash the key, not the entire line
						String[] commaSplit = curLine.split(",");
						//Skip the first character which is <
						String key = commaSplit[0].substring(1);
						int index = Math.abs(key.hashCode()) % config.getNumOfReducers();
						recordsSplitToReduceWorkers[index].add(numCharsSeen);
						outWriter.write(curLine);
						outWriter.newLine();
						numCharsSeen += config.getRecordLength() + 1;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
		    }
			outWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < recordsSplitToReduceWorkers.length; i++) {
			unperformedReduces.add(recordsSplitToReduceWorkers[i]);
		}
		
		initReduceWorkers();
		
		/*ArrayList<Integer> first = recordsSplitToReduceWorkers[0];
		BufferedReader br = null;
		try {
			for (Integer i: first) {
				br = Files.newBufferedReader(Paths.get(newPath), Charset.defaultCharset());
				br.skip(i);
				System.out.println(br.readLine());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}
	
	private void performReduceWork() {
		while (unperformedReduces.size() > 0 || (avaliableReduceWorkers.size() < allReduceWorkers.length)) {
			if (avaliableReduceWorkers.size() > 0 && unperformedReduces.size() > 0) {
				ReduceWorkerCommunicator nextWorker = avaliableReduceWorkers.poll();
				ArrayList<Integer> nextReduce = unperformedReduces.poll();
				nextWorker.sendWork(configPath, intermediateFilepath, nextReduce, reduceWorkIndex);
				reduceWorkIndex += 1;
			}
		}
	}
	
	private void performMapWork() {
		while (unperformedMaps.size() > 0 || (avaliableMapWorkers.size() < numMapWorkers)) {
			if (avaliableMapWorkers.size() > 0 && unperformedMaps.size() > 0) {
				MapWorkCommunicator nextWorker = avaliableMapWorkers.poll();
				InputTracker nextMap = unperformedMaps.poll();
				if (nextMap.isEligibleWorker(nextWorker)) {
					nextMap.workAttempted();
					//Make sure we have this work on record so we can re-commission it if need be.
					sentOutMapWork[nextWorker.getID()] = nextMap;
					nextWorker.sendWork(configPath, nextMap, mapWorkIndex);
					mapWorkIndex += 1;
				}
				else {
					//Ineligible worker.
					unperformedMaps.add(0, nextMap);
					avaliableMapWorkers.add(nextWorker);
				}
			}
		}
		for (MapWorkCommunicator mwc: allMapWorkers) {
			if (mwc != null) mwc.closeSocket();
		}
	}
	
	/*private void shutDownMapWorkers() {
		for (int i = 0; i < allMapWorkers.length; i++) {
			allMapWorkers[i].shutDown();
		}
	}*/
	
	private void initReduceWorkers() {
		allReduceWorkers = new ReduceWorkerCommunicator[config.getWorkerLocations().length];
		String[] workerLocations = config.getWorkerLocations();
		for (int i = 0; i < config.getWorkerLocations().length; i++) {
			String worker = workerLocations[i];
			String[] curWorkerLoc = worker.split(":");
			String curWorkerHost = curWorkerLoc[0];
			int curWorkerPort = Integer.parseInt(curWorkerLoc[1]);
			Socket connection;
			ObjectOutputStream oos = null;
			ObjectInputStream ois = null;
			try {
				connection = new Socket(curWorkerHost, curWorkerPort);
				oos = new ObjectOutputStream(connection.getOutputStream());
				ois = new ObjectInputStream(connection.getInputStream());
				oos.writeObject("reduceworker");
				ReduceWorkerCommunicator rwp = new ReduceWorkerCommunicator(connection, oos, ois, this, i);
				allReduceWorkers[i] = rwp;
				avaliableReduceWorkers.push(rwp);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//TODO Have two sockets per worker: 1 to send work back and forth, and another
	//to keep a heart beat on the worker (i.e. check if it's alive)
	private void initMapWorkers() {
		sentOutMapWork = new InputTracker[config.getWorkerLocations().length];
		allMapWorkers = new MapWorkCommunicator[config.getWorkerLocations().length];
		String[] workerLocations = config.getWorkerLocations();
		for (int i = 0; i < config.getWorkerLocations().length; i++) {
			String[] curWorkerLoc = workerLocations[i].split(":");
			String curWorkerHost = curWorkerLoc[0];
			int curWorkerPort = Integer.parseInt(curWorkerLoc[1]);
			int curHeartbeatPort = Integer.parseInt(curWorkerLoc[2]);
			Socket connection;
			Socket heartbeatSock;
			ObjectOutputStream oos = null;
			ObjectInputStream ois = null;
			
			try {
				connection = new Socket(curWorkerHost, curWorkerPort);
				oos = new ObjectOutputStream(connection.getOutputStream());
				ois = new ObjectInputStream(connection.getInputStream());
				oos.writeObject("mapworker");
				String response = (String) ois.readObject();
				if (response.equals("okay")) {
					heartbeatSock = new Socket(curWorkerHost, curHeartbeatPort);
					MapWorkCommunicator mwp = new MapWorkCommunicator(connection, oos, ois, heartbeatSock, this, i);
					allMapWorkers[i] = mwp;
					numMapWorkers += 1;
					avaliableMapWorkers.push(mwp);
				} 
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void jobFinished(String path, int indexOfFinishedWorker) {
		allMapWorkers[indexOfFinishedWorker].stop();
		if (path.equals("Error")) {
			MapWorkCommunicator failedWorker = allMapWorkers[indexOfFinishedWorker];
			InputTracker input = failedWorker.getCurrentWork();
			input.addFailedWorker(failedWorker);
			if (input.isEligibleForWork()) {
				unperformedMaps.add(input);
			}
			avaliableMapWorkers.add(failedWorker);
		}
		else {
			sentOutMapWork[indexOfFinishedWorker] = null;
			completedMapPaths[nextOpenMapIndex] = path;
			nextOpenMapIndex += 1;
			avaliableMapWorkers.add(allMapWorkers[indexOfFinishedWorker]);
		}
	}
	
	public void reduceFinished(String path, int indexOfFinishedWorker) {
		allReduceWorkers[indexOfFinishedWorker].stop();
		completedReducePaths[nextOpenReduceIndex] = path;
		nextOpenReduceIndex += 1;
		avaliableReduceWorkers.push(allReduceWorkers[indexOfFinishedWorker]);
	}
	
	private void checkIfWorkersAlive() {
		for (int i = 0; i < sentOutMapWork.length; i++) {
			InputTracker curSplit = sentOutMapWork[i];
			if (curSplit != null) {
				boolean stillAlive = allMapWorkers[i].checkIfAlive();
				if (!stillAlive) {
					shutDownMapWorker(i);
				}
			}
		}
	}
	
	public void shutDownMapWorker(int index) {
		if (allMapWorkers[index] != null) {
			numMapWorkers -= 1;
			InputTracker failedWork = allMapWorkers[index].getCurrentWork();
			allMapWorkers[index].shutDownSockets();
			if (failedWork.isEligibleForWork()) unperformedMaps.add(failedWork);
			allMapWorkers[index] = null;
			sentOutMapWork[index] = null;
		}
	}
}
