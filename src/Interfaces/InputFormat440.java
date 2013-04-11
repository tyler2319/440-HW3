package Interfaces;

import Config.Configuration;
import Interfaces.InputSplit440;
import Interfaces.RecordReader440;

public interface InputFormat440<K, V> {
	
	public void configure(Configuration config);
	public RecordReader440<K, V> getRecordReader440(InputSplit440 split);
	public InputSplit440[] getSplits(int numSplits);
	
	public Class<K> getKeyClass();
	public Class<V> getValueClass();
}
