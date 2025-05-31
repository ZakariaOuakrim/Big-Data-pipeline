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

//class dkulchi
public class ProductViewCounter {

   //mapper class hh
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


    public static class ViewCountReducer
            extends Reducer<Text, IntWritable, Text, IntWritable> {

        private IntWritable result = new IntWritable();

        public void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {

            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }

            result.set(sum);

            context.write(key, result);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();

        Job job = Job.getInstance(conf, "product view count");
        job.setJarByClass(ProductViewCounter.class);

        job.setMapperClass(ViewMapper.class);
        job.setReducerClass(ViewCountReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));  // Input path from command line
        FileOutputFormat.setOutputPath(job, new Path(args[1])); // Output path from command line

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}