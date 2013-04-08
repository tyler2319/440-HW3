package Config;

public interface RecordReader440<key, value> {
	public void close();
	public key createKey();
	public value createValue();
	public long getPos();
	public float getProgress();
	public boolean next(key k, value v);
}
