import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class UserTimeReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

    private IntWritable result = new IntWritable();

    @Override
    public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {

        int totalTime = 0;

        // Sum all durations for this user
        for (IntWritable duration : values) {
            totalTime += duration.get();
        }

        result.set(totalTime);
        context.write(key, result);
    }
}
