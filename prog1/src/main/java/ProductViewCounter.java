import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

<<<<<<< HEAD
import java.io.IOException;

//class dkulchi
public class ProductViewCounter {

   //mapper class hh
    public static class ViewMapper
            extends Mapper<Object, Text, Text, IntWritable> {

=======
public class ProductViewCounter {

    // Mapper class
    public static class VuesMapper extends Mapper<Object, Text, Text, IntWritable> {
>>>>>>> 766a9dd83e4d6c2778f4706dedec1050e76b4f0d
        private final static IntWritable one = new IntWritable(1);
        private Text productId = new Text();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();

            // Skip header
            if (line.startsWith("user_id,timestamp")) return;

            // Split CSV line
            String[] parts = line.split(",");

            if (parts.length >= 3) {
                String produit = parts[2];
                productId.set(produit);
                context.write(productId, one);
            }
        }
    }

<<<<<<< HEAD

    public static class ViewCountReducer
            extends Reducer<Text, IntWritable, Text, IntWritable> {

=======
    // Reducer class
    public static class VuesReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
>>>>>>> 766a9dd83e4d6c2778f4706dedec1050e76b4f0d
        private IntWritable result = new IntWritable();

        public void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {

            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }

            result.set(sum);
<<<<<<< HEAD

=======
>>>>>>> 766a9dd83e4d6c2778f4706dedec1050e76b4f0d
            context.write(key, result);
        }
    }

    // Main method
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "nombre vues par produit");

<<<<<<< HEAD
        Job job = Job.getInstance(conf, "product view count");
        job.setJarByClass(ProductViewCounter.class);

        job.setMapperClass(ViewMapper.class);
        job.setReducerClass(ViewCountReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));  // Input path from command line
        FileOutputFormat.setOutputPath(job, new Path(args[1])); // Output path from command line
=======
        job.setJarByClass(NombreVuesParProduit.class);
        job.setMapperClass(VuesMapper.class);
        job.setReducerClass(VuesReducer.class);
        job.setCombinerClass(VuesReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        if (args.length != 2) {
            System.err.println("Usage: NombreVuesParProduit <input path> <output path>");
            System.exit(2);
        }

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
>>>>>>> 766a9dd83e4d6c2778f4706dedec1050e76b4f0d

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
