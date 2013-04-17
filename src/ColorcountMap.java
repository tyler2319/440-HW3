import Interfaces.Mapper;
import MapReduceObjects.OutputCollecter;

public class ColorcountMap implements Mapper<Long, byte[], String, Integer> {
	private final static Integer one = new Integer(1);

	public void map(Long key, byte[] value, OutputCollecter<String, Integer> output) {
		byte red = value[0];
		byte green = value[1];
		byte blue = value[2];
		
		String color;
		
		if (red >= 0) {
			if (green >= 0) {
				if (blue >= 0) {
					color = "black";
				} else {
					color = "blue";
				}
			} else {
				if (blue >= 0) {
					color = "green";
				} else {
					color = "aqua";
				}
			}
		} else {
			if (green >= 0) {
				if (blue >= 0) {
					color = "red";
				} else {
					color = "purple";
				}
			} else {
				if (blue >= 0) {
					color = "yellow";
				} else {
					color = "white";
				}
			}
		}
		
		output.collect(color, one);
	}
}
