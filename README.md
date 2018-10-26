# H1B Statistics

## Problem





## Approach




## Run

### Dependencies

The code was tested on Ubuntu 16.04.5 LTS using Scala 2.11.6. Therefore, the system must be able to run BASH scripts.

Java 8 and Scala 2.11.6 should be installed in the system. The system should a have `java`, `scala`, and `scalac` available in the environment PATH variable.

Furthermore, the system should have a working `JAVA_HOME` variable that points to the java binaries.

For Ubuntu, run the following code to install scala:

`sudo apt-get install scala`

### Instructions

Edit the `run.sh` if you want to change the names of the input and output files.

In a terminal, run the bash script:

`./run.sh`

## Testing

From the project root directory, cd into src/tests, and run tests.sh to execute the unit tests defined in `src/test/scala/h1b/H1BTestSuite.scala`

```
cd src/test
./test.sh
```

## Comments

At the time development for this coding challenge, the sample output file `insight_testsuite/tests/test_1/output/top_10_occupations.txt` located at the GitHub repo: https://github.com/InsightDataScience/h1b_statistics, has an extra space character at the end of line 4.

This will cause a difference in the diff that's run from `run_tests.sh`. Therefore, I've taken the liberty to remove the space character for top_10_occupations.txt in this repo.



