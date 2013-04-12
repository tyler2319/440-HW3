package MapReduceObjects;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import Config.Configuration;

@SuppressWarnings("rawtypes")
public class RecordWriter440 {
	
	private Configuration config;
	private OutputCollecter output;
	private Path path;
	
	public RecordWriter440(Configuration config, OutputCollecter output, Path path) {
		this.config = config;
		this.output = output;
		this.path = path;
	}

	public void writeOutput() {
		OutputStream os = null;
		try {
			os = Files.newOutputStream(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		HashMap rec = output.getRecords();
		Set keys = rec.keySet();
		TreeSet sortedKeys = new TreeSet(keys);
		Iterator keysIter = sortedKeys.iterator();
		
		while(keysIter.hasNext()) {
			Object curKey = keysIter.next();
			String toPrint = "<" + curKey + ", " + rec.get(curKey) + ">";
			byte[] bytes = new byte[config.getRecordLength()];
			
			for(int i = 0; i < toPrint.length(); i++) {
				bytes[i] = (byte) toPrint.charAt(i);
			}
			bytes[bytes.length - 1] = (byte) '\n';
			
			try {
				os.write(bytes);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
