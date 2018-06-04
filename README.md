Hadoop 3 Matrices Multiplication
=====================

About
-----
This a pet project written with Hadoop to multiply three matrices. Based on the implementation of [Studhadoop's code](https://github.com/studhadoop/Matrix-Multiplication/blob/master/MatrixMultiplication.java)

Compiling the Project
----------------
If run using the given test.sh script, there’s no need to manually compile the project (check Running the Project section on how to use the script).

Else, when compiling the project, Hadoop libraries needs to be in the classpath. Location of the Hadoop library files can be found by running the following command

**Hadoop classpath**

Those list of libraries should be in the classpath when compiling the project.

Running the Project
-------------------
This section covers the configurations and steps required to run the system.

**Assumptions**

- Hadoop 2.7 is installed in the system.
- Hadoop is installed at /usr/local/hadoop. If not check the Configurations section below.

Configurations
----------
If the project is run using the provided testbed in test.sh file, first the user needs to check whether the HADOOP_HOME variable is correct. HADOOP_HOME should point to where Hadoop is installed in the computer.

Running the Test Bed
-------
Running the test.sh script runs all the tests in the testbed against the system. At the end of each test the testbed validates the output as well. More information regarding the testbed can be found in the Testbed section.

**Steps involved in the script**:

- Set Hadoop libraries to the classpath (if not set) 
- Cleanup previous build/calculation outputs
- Compile the source code
- Create a JAR to be executed in Hadoop
- Run the JAR in Hadoop providing all the input matrices
- Check if the output is correct for each matrix multiplication

Running the Project Manually
-------
The format to run the project manually is as follows.

`hadoop jar <jar name> MatrixMultiplication <test file to run>`

*Test file to run* is the name of the test file in the Input folder.

Testbed
-----
The project has a test bed to test the correct functionality of the code. 

**Testbed Structure**

Test bed has 3 important folders.
- Input - Contains the input matrices for the system. Matrices should be written in the Sparse format as follows.
		Matrix ID (A or B), row number, column number, value
- Config - Contains the number of columns, rows in each matrix. The format is as follows.
		Number of rows in matrix A, Number of columns in matrix A, Number of columns in matrix B

(Do note that Number of columns in matrix A = Number of rows in matrix B)
- Expected - Contains the expected values from the matrix computation. It’s against the values in this folder the testbed checks values produced by the system.

Testbed finds the connection with each file in each folder by its name. If the input files name is test1 in Input folder, system will expect the configs to be in a file named test1 in the Configs folder, also the expected output to be in the Expected folder with a name test1.
