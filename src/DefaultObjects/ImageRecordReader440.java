package DefaultObjects;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import Interfaces.RecordReader440;
import MapReduceObjects.Record;

public class ImageRecordReader440 implements RecordReader440<Long, byte[]> {
	
	private ImageSplit440 split;
	private int pos;
	private InputStream is;
	
	public ImageRecordReader440(ImageSplit440 split) {
		this.split = split;
		this.pos = 0;
		
		try {
			this.is = Files.newInputStream(Paths.get(split.getPath()));
			long n = split.getStart();
			is.skip(3*n);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Long createKey() {
		return (long) 0.0;
	}

	public byte[] createValue() {
		byte[] pixel = new byte[3];
		int isRead = -1;
		try {
			isRead = is.read(pixel, 0, 3);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (isRead != -1) {
			pos++;
			return pixel;
		}
		
		return null;
	}

	public Record<Long, byte[]> next() {
		Long k;
		byte[] v;
		
		if (pos >= split.getLength()) {
			return null;
		} else {
			k = createKey();
			v = createValue();
			if (v == null) {
				return null;
			}
			return new Record<Long, byte[]>(k, v);
		}
	}
	
	public int getPos() {
		return pos;
	}

	public float getProgress() {
		return pos / split.getLength();
	}
}
