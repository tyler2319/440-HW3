package Config;

public interface InputFormat440<key, value> {
	public RecordReader440<key, value> getRecordReader440(InputSplit440 split);
	public InputSplit440[] getSplits(int numSplits);
}
