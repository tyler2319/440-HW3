import java.util.StringTokenizer;

import Interfaces.Mapper;
import MapReduceObjects.OutputCollecter;


public class BaseballMap implements Mapper<Long, String, String, Integer>{
	public void map(Long key, String value, OutputCollecter<String, Integer> output) {
		String word = value;
		String[] vals = word.split("-");
		String name = vals[0];
		String atBatOutcome = vals[1];
		output.collect(name, atBatToInt(atBatOutcome));
	}
		
	private Integer atBatToInt(String atBatName) {
		int result;
		switch (atBatName) {
			case "single": 
				result = 1;
				break;
			case "double":
				result = 2;
				break;
			case "triple":
				result = 3;
				break;
			case "homer":
				result = 4;
				break;
			//String is "out"
			default:
				result = 0;
				break;
		}
		return new Integer(result);
	}
}
