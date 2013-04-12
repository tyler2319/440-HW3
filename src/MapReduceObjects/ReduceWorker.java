package MapReduceObjects;

import Config.Configuration;

public class ReduceWorker {
	
	private boolean currentlyWorking = false;
	
	//TODO Delete - This is just for testing purposes
	public ReduceWorker() { }
	
	public void startJob(Configuration config) throws IllegalAccessException {
		if (!currentlyWorking) {
			currentlyWorking = true;
			 
			currentlyWorking = false;
		}
		else throw new IllegalAccessException("Worker already working.");
	}
}
