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

public class MatrixMapper extends Mapper<LongWritable, Text, Text, Text> {
    
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        Configuration conf = context.getConfiguration();
        /*
         * Row column count
         */
        int aMatrixRows = MatrixMultiplication.getIntFromString(conf.get("m"));
        int bMatrixColumns = MatrixMultiplication.getIntFromString(conf.get("p"));

        //todo - rename
        int s = MatrixMultiplication.getIntFromString(conf.get("s"));
        int t = MatrixMultiplication.getIntFromString(conf.get("t"));
        int v = MatrixMultiplication.getIntFromString(conf.get("v"));

        int mPerS = aMatrixRows/s; // Number of blocks in each column of A.
        int pPerV = bMatrixColumns/v; // Number of blocks in each row of B.

        String inputLine = value.toString();
        String[] matrixData = inputLine.split(",");
        Text outputKey = new Text();
        Text outputValue = new Text();

        if (matrixData[0].equals(MatrixMultiplication.MATRIX_A_ID)) {
            int i = MatrixMultiplication.getIntFromString(matrixData[1]);
            int j = MatrixMultiplication.getIntFromString(matrixData[2]);
            for (int index = 0; index < pPerV; index++) {
                outputKey.set(MatrixMultiplication.getStringFromInteger(i/s) + "," + MatrixMultiplication.getStringFromInteger(j/t) + "," + MatrixMultiplication.getStringFromInteger(index));
                outputValue.set(MatrixMultiplication.MATRIX_A_ID+"," + MatrixMultiplication.getStringFromInteger(i%s) + "," + MatrixMultiplication.getStringFromInteger(j%t) + "," + matrixData[3]);
                context.write(outputKey, outputValue);
            }
        } else {
            int j = MatrixMultiplication.getIntFromString(matrixData[1]);
            int k = MatrixMultiplication.getIntFromString(matrixData[2]);
            for (int index = 0; index < mPerS; index++) {
                outputKey.set(MatrixMultiplication.getStringFromInteger(index) + "," + MatrixMultiplication.getStringFromInteger(j/t) + "," + MatrixMultiplication.getStringFromInteger(k/v));
                outputValue.set(MatrixMultiplication.MATRIX_B_ID+"," + MatrixMultiplication.getStringFromInteger(j%t) + "," + MatrixMultiplication.getStringFromInteger(k%v) + "," + matrixData[3]);
                context.write(outputKey, outputValue);
            }
        }
    }

}