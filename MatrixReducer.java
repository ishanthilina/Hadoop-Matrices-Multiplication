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

public class MatrixReducer extends Reducer<Text, Text, Text, Text> {
    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        String[] value;
        ArrayList<Entry<String, Float>> listA = new ArrayList<Entry<String, Float>>();
        ArrayList<Entry<String, Float>> listB = new ArrayList<Entry<String, Float>>();
        for (Text val : values) {
            value = val.toString().split(",");
            if (value[0].equals(MatrixMultiplication.MATRIX_A_ID)) {
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
        int s = MatrixMultiplication.getIntFromString(conf.get("s"));
        int v = MatrixMultiplication.getIntFromString(conf.get("v"));
        Text outputValue = new Text();
        for (Entry<String, Float> entry : hash.entrySet()) {
            indices = entry.getKey().split(",");
            i = MatrixMultiplication.getStringFromInteger(MatrixMultiplication.getIntFromString(blockIndices[0])*s + MatrixMultiplication.getIntFromString(indices[0]));
            k = MatrixMultiplication.getStringFromInteger(MatrixMultiplication.getIntFromString(blockIndices[2])*v + MatrixMultiplication.getIntFromString(indices[1]));
            outputValue.set(i + "," + k + "," + Float.toString(entry.getValue()));
            context.write(null, outputValue);
        }
    }
}