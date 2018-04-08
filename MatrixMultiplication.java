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

    public static final String MATRIX_A_ID = "A";
    public static final String MATRIX_B_ID = "B";

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
        matrixJob.setMapperClass(MatrixMapper.class);
        matrixJob.setReducerClass(MatrixReducer.class);
 
        matrixJob.setInputFormatClass(TextInputFormat.class);
        matrixJob.setOutputFormatClass(TextOutputFormat.class);
 
        FileInputFormat.addInputPath(matrixJob, new Path(args[0]));
        FileOutputFormat.setOutputPath(matrixJob, new Path(args[1]));
 
        matrixJob.waitForCompletion(true);
    }

    public static Integer getIntFromString(String input){
        return Integer.parseInt(input);
    }

    public static String getStringFromInteger(Integer input){
        return Integer.toString(input);
    }

    public static Float getFloatFromString(String input){
        return Float.parseFloat(input);
    }
}
