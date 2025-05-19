import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * ProductViewCounter - MapReduce program to count the number of views per product
 * Input: CSV file with fields: user_id,timestamp,product_id,category,duration
 * Output: product_id, view_count
 */
public class ProductViewCounter {

    /**
     * Mapper class that processes each line of the input file
     * and emits (product_id, 1) for each record
     */
    public static class ViewMapper
            extends Mapper<Object, Text, Text, IntWritable> {

        private final static IntWritable one = new IntWritable(1);
        private Text productId = new Text();

        public void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {

            // Skip the header line if present
            if (value.toString().startsWith("user_id,timestamp")) {
                return;
            }

            // Parse the CSV line
            String[] fields = value.toString().split(",");

            // Ensure we have enough fields (at least 3 to get product_id)
            if (fields.length >= 3) {
                // Extract the product_id (3rd field, index 2)
                String product = fields[2];
                productId.set(product);

                // Emit (product_id, 1) for counting
                context.write(productId, one);
            }
        }
    }

    /**
     * Reducer class that sums up the counts for each product_id
     */
    public static class ViewCountReducer
            extends Reducer<Text, IntWritable, Text, IntWritable> {

        private IntWritable result = new IntWritable();

        public void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {

            // Sum up all counts for this product_id
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }

            result.set(sum);

            // Emit (product_id, total_count)
            context.write(key, result);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();

        // Create a new job
        Job job = Job.getInstance(conf, "product view count");
        job.setJarByClass(ProductViewCounter.class);

        // Set mapper and reducer classes
        job.setMapperClass(ViewMapper.class);
        job.setReducerClass(ViewCountReducer.class);

        // Specify output types
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        // Set input and output paths
        FileInputFormat.addInputPath(job, new Path(args[0]));  // Input path from command line
        FileOutputFormat.setOutputPath(job, new Path(args[1])); // Output path from command line

        // Exit when job completes, return status
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}