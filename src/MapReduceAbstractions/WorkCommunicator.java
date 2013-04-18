package MapReduceAbstractions;

import MapReduceObjects.InputTracker;

public interface WorkCommunicator {
	public void start() throws Exception;
	public void stop();
	public int getID();
	public boolean checkIfAlive();
	public void shutDownSockets();
	public void shutDownSelf();
	public InputTracker getCurrentWork();
}
