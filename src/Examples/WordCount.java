package Examples;

import java.io.File;
import java.util.Iterator;
import java.util.StringTokenizer;

import Config.Config;
import Interfaces.Mapper;
import Interfaces.Reducer;
import MapReduceObjects.OutputCollecter;

public class WordCount {

	public static class Map implements Mapper<Long, String, String, Integer> {
		private final static Integer one = new Integer(1);

		public void map(Long key, String value, OutputCollecter<String, Integer> output) {
			String line = value;
			StringTokenizer tokenizer = new StringTokenizer(line);
			while (tokenizer.hasMoreTokens()) {
				String word = tokenizer.nextToken();
				output.collect(word, one);
			}
		}
	}
	
	public static class Reduce implements Reducer<String, Integer, String, Integer> {

		public void reduce(String key, Iterator<Integer> values, OutputCollecter<String, Integer> output) {
			Integer sum = 0;
			while (values.hasNext()) {
				sum += values.next();	
			}
			output.collect(key, sum);
		}
		
	}

	public static void main(String[] args) {
		Config cfg = new Config();

		cfg.setJobName("wordcount");
		
		cfg.setMapperClass(Map.class);
		cfg.setCombinerClass(Reduce.class);
		cfg.setReducerClass(Reduce.class);
		
		cfg.setInputFile(new File(args[0]));
		cfg.setOutputFile(new File(args[1]));
	}
}
