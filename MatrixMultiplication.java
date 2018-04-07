//pacakge matrix;

import java.io.IOException;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
 
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
 
public class MatrixMultiplication {
 
    public static class Map extends Mapper<LongWritable, Text, Text, Text> {
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            Configuration conf = context.getConfiguration();
            /*
             * Row column count
             */
            int aMatrixRows = getIntFromString(conf.get("m"));
            int bMatrixColumns = getIntFromString(conf.get("p"));

            //todo - rename
            int s = getIntFromString(conf.get("s"));
            int t = getIntFromString(conf.get("t"));
            int v = getIntFromString(conf.get("v"));
            
            int mPerS = aMatrixRows/s; // Number of blocks in each column of A.
            int pPerV = bMatrixColumns/v; // Number of blocks in each row of B.
            
            String inputLine = value.toString();
            String[] matrixData = inputLine.split(",");
            Text outputKey = new Text();
            Text outputValue = new Text();
            
            if (matrixData[0].equals("A")) {
                int i = getIntFromString(matrixData[1]);
                int j = getIntFromString(matrixData[2]);
                for (int kPerV = 0; kPerV < pPerV; kPerV++) {
                    outputKey.set(getStringFromInteger(i/s) + "," + getStringFromInteger(j/t) + "," + getStringFromInteger(kPerV));
                    outputValue.set("A," + getStringFromInteger(i%s) + "," + getStringFromInteger(j%t) + "," + matrixData[3]);
                    context.write(outputKey, outputValue);
                }
            } else {
                int j = getIntFromString(matrixData[1]);
                int k = getIntFromString(matrixData[2]);
                for (int iPerS = 0; iPerS < mPerS; iPerS++) {
                    outputKey.set(getStringFromInteger(iPerS) + "," + getStringFromInteger(j/t) + "," + getStringFromInteger(k/v));
                    outputValue.set("B," + getStringFromInteger(j%t) + "," + getStringFromInteger(k%v) + "," + matrixData[3]);
                    context.write(outputKey, outputValue);
                }
            }
        }


    }
 
    public static class Reduce extends Reducer<Text, Text, Text, Text> {
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            String[] value;
            ArrayList<Entry<String, Float>> listA = new ArrayList<Entry<String, Float>>();
            ArrayList<Entry<String, Float>> listB = new ArrayList<Entry<String, Float>>();
            for (Text val : values) {
                value = val.toString().split(",");
                if (value[0].equals("A")) {
                    listA.add(new SimpleEntry<String, Float>(value[1] + "," + value[2], Float.parseFloat(value[3])));
                } else {
                    listB.add(new SimpleEntry<String, Float>(value[1] + "," + value[2], Float.parseFloat(value[3])));
                }
            }
            String[] iModSAndJModT;
            String[] jModTAndKModV;
            float a_ij;
            float b_jk;
            String hashKey;
            HashMap<String, Float> hash = new HashMap<String, Float>();
            for (Entry<String, Float> a : listA) {
                iModSAndJModT = a.getKey().split(",");
                a_ij = a.getValue();
                for (Entry<String, Float> b : listB) {
                    jModTAndKModV = b.getKey().split(",");
                    b_jk = b.getValue();
                    if (iModSAndJModT[1].equals(jModTAndKModV[0])) {
                        hashKey = iModSAndJModT[0] + "," + jModTAndKModV[1];
                        if (hash.containsKey(hashKey)) {
                            hash.put(hashKey, hash.get(hashKey) + a_ij*b_jk);
                        } else {
                            hash.put(hashKey, a_ij*b_jk);
                        }
                    }
                }
            }
            String[] blockIndices = key.toString().split(",");
            String[] indices;
            String i;
            String k;
            Configuration conf = context.getConfiguration();
            int s = getIntFromString(conf.get("s"));
            int v = getIntFromString(conf.get("v"));
            Text outputValue = new Text();
            for (Entry<String, Float> entry : hash.entrySet()) {
                indices = entry.getKey().split(",");
                i = getStringFromInteger(getIntFromString(blockIndices[0])*s + getIntFromString(indices[0]));
                k = getStringFromInteger(getIntFromString(blockIndices[2])*v + getIntFromString(indices[1]));
                outputValue.set(i + "," + k + "," + Float.toString(entry.getValue()));
                context.write(null, outputValue);
            }
        }
    }
 
    public static void main(String[] args) throws Exception {

        //Create a new configuration object to pass the configs to hadoop nodes
        Configuration conf = new Configuration();
        // m and n denotes the dimensions of matrix A.
        // m = number of rows
        // n = number of columns
        conf.set("m", "2");
        conf.set("n", "5");
        // n and p denotes the dimensions of matrix B
        // n = number of rows
        // p = number of columns
        conf.set("p", "3");

        //todo - needs refactoring
        conf.set("s", "2"); // Number of rows in a block in A.
        conf.set("t", "5"); // Number of columns in a block in A = number of rows in a block in B.
        conf.set("v", "3"); // Number of columns in a block in B.
 
        Job matrixJob = new Job(conf, "Matrix-Multiplication");
        matrixJob.setJarByClass(MatrixMultiplication.class);
        matrixJob.setOutputKeyClass(Text.class);
        matrixJob.setOutputValueClass(Text.class);

        //todo - rename the class names
        matrixJob.setMapperClass(Map.class);
        matrixJob.setReducerClass(Reduce.class);
 
        matrixJob.setInputFormatClass(TextInputFormat.class);
        matrixJob.setOutputFormatClass(TextOutputFormat.class);
 
        FileInputFormat.addInputPath(matrixJob, new Path(args[0]));
        FileOutputFormat.setOutputPath(matrixJob, new Path(args[1]));
 
        matrixJob.waitForCompletion(true);
    }

    private static Integer getIntFromString(String input){
        return Integer.parseInt(input);
    }

    private static String getStringFromInteger(Integer input){
        return Integer.toString(input);
    }
}
