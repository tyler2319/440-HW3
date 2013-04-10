package Interfaces;

import Config.Configuration;
import Interfaces.InputSplit440;
import Interfaces.RecordReader440;

public interface InputFormat440<key, value> {
	
	public void configure(Configuration config);
	public RecordReader440<key, value> getRecordReader440(InputSplit440 split);
	public InputSplit440[] getSplits(int numSplits);
	
	public Class<?> getKeyClass();
	public Class<?> getValueClass();
}
