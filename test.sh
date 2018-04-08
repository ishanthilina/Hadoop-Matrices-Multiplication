#!/usr/bin/env bash

##Check if Hadoop path is set
if [ -z "$HADOOP_CLASSPATH" ]; then
    echo "Setting HADOOP_CLASSPATH"
    HADOOP_CLASSPATH=$(/usr/local/hadoop/bin/hadoop classpath)
fi

echo "Cleanup previous builds..."
rm -r ./matrix/
rm -r ./Output/
#create dir if not exists
mkdir -p matrix

echo "Compiling...."
if javac -classpath ${HADOOP_CLASSPATH}  -d matrix/ ./src/*.java; then
    echo "Compilation Successful...!"
else
    echo "Compilation Failed"
    exit 1
fi

ls -la ./matrix/
#if test `find "./matrix/MatrixMultiplication.class" -mmin +120`
#then
#    exit 1
#fi

echo "Creating Jar..."
jar -cvf MM.jar -C matrix/ .


echo "Run Hadoop..."
/usr/local/hadoop/bin/hadoop jar MM.jar MatrixMultiplication data.txt Output

#Compare files
cmp --silent "./Expected/1.txt" "./Output/part-r-00000" || echo -e "\033[31;7mOutput is different than expected\e[0m";
