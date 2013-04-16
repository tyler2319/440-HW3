package MapReduceObjects;

import java.util.ArrayList;

import Interfaces.InputSplit440;

public class InputTracker {
	InputSplit440 input;
	private int numAttempts = 0;
	private static final int maxNumberOfAttempts = 2;
	private ArrayList<MapWorkCommunicator> failedWorkers = new ArrayList<MapWorkCommunicator>();
	
	public InputTracker(InputSplit440 input) {
		this.input = input;
	}
	
	public boolean isEligibleWorker(MapWorkCommunicator worker) {
		return !failedWorkers.contains(worker);
	}
	
	public void addFailedWorker(MapWorkCommunicator worker) {
		failedWorkers.add(worker);
	}
	
	public InputSplit440 getInput() {
		return input;
	}
	
	public void workAttempted() {
		numAttempts += 1;
	}
	
	public boolean isEligibleForWork() {
		return numAttempts < maxNumberOfAttempts;
	}
}
