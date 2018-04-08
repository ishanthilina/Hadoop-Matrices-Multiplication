#!/usr/bin/env bash

HADOOP_HOME=/usr/local/hadoop

##Check if Hadoop path is set
if [ -z "$HADOOP_CLASSPATH" ]; then
    echo -e "\e[42mSetting HADOOP_CLASSPATH\e[0m"
    HADOOP_CLASSPATH=$(${HADOOP_HOME}/bin/hadoop classpath)
fi

echo -e "\e[42mCleanup previous builds...\e[0m"
rm -r ./matrix/
rm -r ./Output/
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

echo -e "\e[42mRun Hadoop...\e[0m"
${HADOOP_HOME}/bin/hadoop jar MM.jar MatrixMultiplication 1

#Compare files
cmp --silent "./Expected/1.txt" "./Output/part-r-00000" || echo -e "\033[31;7mOutput is different than expected\e[0m";
