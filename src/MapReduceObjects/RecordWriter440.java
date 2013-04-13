package MapReduceObjects;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import Config.Configuration;

@SuppressWarnings("rawtypes")
public class RecordWriter440 {
	
	private Configuration config;
	private OutputCollecter output;
	private String path;
	
	public RecordWriter440(Configuration config, OutputCollecter output, String path) {
		this.config = config;
		this.output = output;
		this.path = path;
	}

	public void writeOutput() {
		OutputStream os = null;
		try {
			os = Files.newOutputStream(Paths.get(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Iterator rec = output.getRecords().iterator();
		
		while(rec.hasNext()) {
			Record curRec = (Record) rec.next();
			String toPrint = "<" + curRec.getKey() + ", " + curRec.getValue() + ">";
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
