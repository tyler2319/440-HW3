import java.util.Iterator;

import Interfaces.Reducer;
import MapReduceObjects.OutputCollecter;

public class ColorcountReduce implements Reducer<String, Integer, String, Integer> {

	public void reduce(String key, Iterator<Integer> values, OutputCollecter<String, Integer> output) {
		Integer sum = 0;
		while (values.hasNext()) {
			sum += values.next();	
		}
		output.collect(key, sum);
	}	
}
