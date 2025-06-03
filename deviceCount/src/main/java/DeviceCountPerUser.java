import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class DeviceCountPerUser {

    // Mapper class
    public static class DeviceCountMapper extends Mapper<LongWritable, Text, Text, Text> {

        private Text userId = new Text();
        private Text deviceType = new Text();

        @Override
        public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {

            // Skip header line
            String line = value.toString();
            if (line.startsWith("user_id")) {
                return;
            }

            // Split CSV line
            String[] fields = line.split(",");

            // Check if we have enough fields
            if (fields.length >= 6) {
                try {
                    // Extract user_id (field 0) and device_type (field 5)
                    String userIdStr = fields[0].trim();
                    String deviceTypeStr = fields[5].trim();

                    // Remove quotes if present
                    userIdStr = userIdStr.replaceAll("\"", "");
                    deviceTypeStr = deviceTypeStr.replaceAll("\"", "");

                    // Set key-value pairs
                    userId.set(userIdStr);
                    deviceType.set(deviceTypeStr);

                    // Emit user_id as key and device_type as value
                    context.write(userId, deviceType);

                } catch (Exception e) {
                    // Skip malformed lines
                    System.err.println("Error processing line: " + line);
                }
            }
        }
    }

    // Reducer class
    public static class DeviceCountReducer extends Reducer<Text, Text, Text, IntWritable> {

        private IntWritable result = new IntWritable();

        @Override
        public void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {

            // Use a Set to store unique device types for each user
            Set<String> uniqueDevices = new HashSet<>();

            // Add all device types to the set (duplicates are automatically removed)
            for (Text deviceType : values) {
                uniqueDevices.add(deviceType.toString());
            }

            // Set the count of unique device types
            result.set(uniqueDevices.size());

            // Write the result: user_id -> count of unique device types
            context.write(key, result);
        }
    }

    // Driver method
    public static void main(String[] args) throws Exception {

        // Check if correct number of arguments provided
        if (args.length != 2) {
            System.err.println("Usage: DeviceCountPerUser <input path> <output path>");
            System.exit(-1);
        }

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "device count per user");

        // Set jar file
        job.setJarByClass(DeviceCountPerUser.class);

        // Set mapper and reducer classes
        job.setMapperClass(DeviceCountMapper.class);
        job.setCombinerClass(DeviceCountReducer.class);
        job.setReducerClass(DeviceCountReducer.class);

        // Set output key and value types
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        // Set mapper output types (different from final output)
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        // Set input and output paths
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        // Wait for job completion
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}