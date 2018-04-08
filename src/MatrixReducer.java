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
        String[] matrixData;
        ArrayList<Entry<String, Float>> listA = new ArrayList<Entry<String, Float>>();
        ArrayList<Entry<String, Float>> listB = new ArrayList<Entry<String, Float>>();
        for (Text matrixLine : values) {
            matrixData = matrixLine.toString().split(",");
            if (matrixData[0].equals(MatrixMultiplication.MATRIX_A_ID)) {
                listA.add(new SimpleEntry<String, Float>(matrixData[1] + "," + matrixData[2], MatrixMultiplication.getFloatFromString(matrixData[3])));
            } else {
                listB.add(new SimpleEntry<String, Float>(matrixData[1] + "," + matrixData[2], MatrixMultiplication.getFloatFromString(matrixData[3])));
            }
        }

        String[] iModSWithJModT;
        String[] jModTWithKModV;

        float aValue;
        float bValue;
        String hashKey;
        HashMap<String, Float> locationToValueMap = new HashMap<String, Float>();
        for (Entry<String, Float> aKeyVal : listA) {
            iModSWithJModT = aKeyVal.getKey().split(",");
            aValue = aKeyVal.getValue();
            for (Entry<String, Float> bKeyVal : listB) {
                jModTWithKModV = bKeyVal.getKey().split(",");
                bValue = bKeyVal.getValue();
                if (iModSWithJModT[1].equals(jModTWithKModV[0])) {
                    hashKey = iModSWithJModT[0] + "," + jModTWithKModV[1];
                    if (locationToValueMap.containsKey(hashKey)) {
                        locationToValueMap.put(hashKey, locationToValueMap.get(hashKey) + aValue*bValue);
                    } else {
                        locationToValueMap.put(hashKey, aValue*bValue);
                    }
                }
            }
        }
        String[] myBlockIndices = key.toString().split(",");
        String[] myIndices;
        String i;
        String k;
        Configuration config = context.getConfiguration();
        //todo - rename
        int s = MatrixMultiplication.getIntFromString(config.get("s"));
        int v = MatrixMultiplication.getIntFromString(config.get("v"));

        Text outputValue = new Text();
        for (Entry<String, Float> entry : locationToValueMap.entrySet()) {
            myIndices = entry.getKey().split(",");
            i = MatrixMultiplication.getStringFromInteger(MatrixMultiplication.getIntFromString(myBlockIndices[0])*s + MatrixMultiplication.getIntFromString(myIndices[0]));
            k = MatrixMultiplication.getStringFromInteger(MatrixMultiplication.getIntFromString(myBlockIndices[2])*v + MatrixMultiplication.getIntFromString(myIndices[1]));
            outputValue.set(i + "," + k + "," + Float.toString(entry.getValue()));
            context.write(null, outputValue);
        }
    }
}