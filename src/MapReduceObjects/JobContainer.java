package MapReduceObjects;

public class JobContainer {
	String jobName;
	MasterWorker mw;
	int id;
	
	public JobContainer(int id) {
		this.id = id;
	}
	
	public void setJobName(String s) {
		this.jobName = s;
	}
	
	public void setMasterWorker(MasterWorker mw) {
		this.mw = mw;
	}
	
	public void finishJob() {
		mw.stop();
	}
	
	public int getJobID() {
		return this.id;
	}
	
	public String getJobName() {
		return this.jobName;
	}
}
