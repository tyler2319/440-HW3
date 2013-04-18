package MapReduceObjects;

import java.io.Serializable;
import java.util.ArrayList;

import MapReduceAbstractions.WorkInput;

public class ReduceWorkInput implements WorkInput, Serializable {
	private ArrayList<Integer> work;
	private String path;
	
	public ReduceWorkInput(ArrayList<Integer> work, String path) {
		this.work = work;
		this.path = path;
	}
	
	public String getPath() {
		return path;
	}
	
	public ArrayList<Integer> getWork() {
		return work;
	}
}
