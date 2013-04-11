import java.util.StringTokenizer;

import Interfaces.Mapper;
import MapReduceObjects.OutputCollecter;

public class WordcountMap implements Mapper<Long, String, String, Integer> {
	private final static Integer one = new Integer(1);

	public void map(Long key, String value, OutputCollecter<String, Integer> output) {
		String line = value;
		StringTokenizer tokenizer = new StringTokenizer(line);
		while (tokenizer.hasMoreTokens()) {
			String word = tokenizer.nextToken();
			System.out.print(word + " ");
			output.collect(word, one);
		}
	}
}