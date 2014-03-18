package uniandes.mapRed;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class WCReducerWikipedia extends Reducer<Text, Text, Text, Text> {
	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {

		for (Text iw : values) {
			context.write(key, new Text(iw.toString()));
		}

	}

}
