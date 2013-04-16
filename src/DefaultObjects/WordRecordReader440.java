package DefaultObjects;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import Interfaces.RecordReader440;
import MapReduceObjects.Record;

public class WordRecordReader440 implements RecordReader440<Long, String>{
	
	private TextSplit440 split;
	private int pos;
	private BufferedReader br;
	private Character lastCharSeen = null;
	
	public WordRecordReader440(TextSplit440 split) {
		this.split = split;
		this.pos = 0;
		
		try {
			this.br = Files.newBufferedReader(Paths.get(split.getPath()), Charset.defaultCharset());
			long n = 0;
			if ((n = split.getStart()) != 0) {
				br.skip(n - 1);
				int start = br.read();
				/* Keep skipping until we find a space, then keep 
				 * skipping until we find a non-space. This is the proper
				 * starting point for the record reader.
				 */
				//32 is ASCII value for space, 10 for new line
				while (start != 32 && start != 10) {
					pos += 1;
					start = br.read();
				}
				while (start == 32 || start == 10) {
					pos += 1;
					start = br.read();
					lastCharSeen = (char) start;
				}
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
		ArrayList<Character> result = new ArrayList<Character>();
		Character tempLastChar = 'c';
		try {
			int nextChar = br.read();
			//ASCII value of 32 is space, 10 for new line
			while (nextChar != 32 && nextChar != 10 && nextChar != -1) {
				result.add((char)nextChar);
				nextChar = br.read();
				pos +=1 ;
			}
			//Put the reader in the right place for the next time this is called
			while ((nextChar == 32 || nextChar == 10) && nextChar != -1) {
				nextChar = br.read();
				tempLastChar = (char) nextChar;
				pos += 1 ;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (result.size() == 0) return null;
		else {
			char[] resultChars;
			if (lastCharSeen != null) {
				resultChars = new char[result.size() + 1];
				resultChars[0] = lastCharSeen;
				for (int i = 1; i < resultChars.length; i++) {
					resultChars[i] = result.get(i - 1);
				}
			}
			else {
				resultChars = new char[result.size()];
				for (int i = 0; i < resultChars.length; i++) {
					resultChars[i] = result.get(i);
				}
			}
			lastCharSeen = tempLastChar;
			return new String(resultChars);
		}
	}

	public int getPos() {
		return pos;
	}

	public float getProgress() {
		return pos / split.getLength();
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

}
