Matrix-Multiplication
=====================

Assumptions
-----------

- Hadoop is installed in `/usr/local/hadoop`. If not Set the `HADOOP_HOME` variable in the beginning on the `test.sh` file.

Running the Code
----------------

Run the `test.sh` file. It will do the following tasks.

- Set Hadoop libraries to the classpath (if not set) 
- Cleanup previous build/calculation outputs
- Compile the source code
- Create a JAR to be executed in Hadoop
- Run the JAR in Hadoop
- Check if the output is correct