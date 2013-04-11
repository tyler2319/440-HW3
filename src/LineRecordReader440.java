import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

import Config.Configuration;
import Interfaces.RecordReader440;
import MapReduceObjects.Record;

public class LineRecordReader440 implements RecordReader440<Long, String> {
	
	private Configuration config;
	private TextLineSplit440 split;
	private int pos;
	private BufferedReader br;
	
	public LineRecordReader440(Configuration config, TextLineSplit440 split) {
		this.config = config;
		this.split = split;
		this.pos = 0;
		
		try {
			this.br = Files.newBufferedReader(split.getPath(), Charset.defaultCharset());
			long n = 0;
			if ((n = split.getStart()) != 0) {
				br.skip(n);
				String scrap = br.readLine();
				pos += scrap.length();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Long createKey() {
		return (long) 0.0;
	}

	public String createValue() {
		String nextLine = null;
		try {
			nextLine = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (nextLine != null) {
			pos += nextLine.length();
			return nextLine;
		}
		
		return null;
	}

	public Record<Long, String> next() {
		Long k;
		String v;
		
		if (pos >= split.getLength()) {
			return null;
		} else {
			k = createKey();
			v = createValue();
			if (v == null) {
				return null;
			}
			return new Record<Long, String>(k, v);
		}
	}
	
	public int getPos() {
		return pos;
	}

	public float getProgress() {
		return pos / split.getLength();
	}
}
