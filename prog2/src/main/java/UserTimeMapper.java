import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class UserTimeMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    private Text userId = new Text();
    private IntWritable duration = new IntWritable();

    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

        // Skip header line
        if (key.get() == 0) {
            return;
        }

        String line = value.toString();
        String[] fields = line.split(",");

        // Check if we have all required fields
        // Format: user_id,timestamp,product_id,category,duration
        if (fields.length >= 5) {
            try {
                String userIdStr = fields[0].trim();
                String durationStr = fields[4].trim();

                userId.set(userIdStr);
                duration.set(Integer.parseInt(durationStr));

                context.write(userId, duration);
            } catch (NumberFormatException e) {
                // Skip invalid records
                System.err.println("Invalid duration format: " + line);
            }
        }
    }
}