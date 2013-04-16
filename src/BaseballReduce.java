import java.util.Iterator;

import Interfaces.Reducer;
import MapReduceObjects.OutputCollecter;


public class BaseballReduce implements Reducer<String, Integer, String, Double> {
	public void reduce(String key, Iterator<Integer> values, OutputCollecter<String, Double> output) {
		Integer sum = 0;
		int numAtBats = 0;
		while (values.hasNext()) {
			sum += values.next();
			numAtBats += 1;
		}
		double sluggingPct = ((double) sum) / ((double) numAtBats);
		output.collect(key, sluggingPct);
	}
}
