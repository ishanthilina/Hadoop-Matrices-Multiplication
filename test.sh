#!/usr/bin/env bash

HADOOP_HOME=/usr/local/hadoop

##Check if Hadoop path is set
if [ -z "$HADOOP_CLASSPATH" ]; then
    echo -e "\e[42mSetting HADOOP_CLASSPATH\e[0m"
    HADOOP_CLASSPATH=$(${HADOOP_HOME}/bin/hadoop classpath)
fi

echo -e "\e[42mCleanup previous builds...\e[0m"
rm -r ./matrix/
#create dir if not exists
mkdir -p matrix

echo -e "\e[42mCompiling....\e[0m"
if javac -classpath ${HADOOP_CLASSPATH}  -d matrix/ ./src/*.java; then
    echo -e "\e[42mCompilation Successful...!\e[0m"
else
    echo "Compilation Failed"
    exit 1
fi

ls -la ./matrix/

echo -e "\e[42mCreating Jar...\e[0m"
jar -cvf MM.jar -C matrix/ .

INPUT=Input/*
for f in $INPUT
do
    echo -e "\e[42mRun Hadoop Matrix Multiplication on test $(basename $f)...\e[0m"
    rm -r ./Output/
    ${HADOOP_HOME}/bin/hadoop jar MM.jar MatrixMultiplication $(basename $f)
    #Compare files
    cmp --silent "./Expected/$(basename $f)" "./Output/part-r-00000" || echo -e "\033[31;7mOutput is different than expected\e[0m";
done


