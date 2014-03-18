package wiki.job;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.TextInputFormat;

public class NonSplitableTextInputFormat extends TextInputFormat {

	protected boolean isSplitable(FileSystem fs, Path file) {
		return false;
	}
}
