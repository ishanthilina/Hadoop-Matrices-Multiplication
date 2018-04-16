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
- Run the JAR in Hadoop providing all the input matrices
- Check if the output is correct for each matrix multiplication

Test File Format
-----------
- Configs folder : Add the configuration details for the particular test.
The order is number of rows in matrix A, number of columns in matrix A, number of columns in matrix B
- Input folder : Contains the input matrices in sparse format

Do note that the test bed matches config to input matrices by its name. The names need to be equal.

