#!/bin/bash

echo "Cleaning up target folder (if it exists)"
rm -rf ./target
mkdir ./target

echo "Compiling scala code"
scalac -d ./target ./src/main/scala/h1b/*.scala

echo "Running compiled scala code"

# Change the filenames here. Do not add a "." in front of the file name such as ".input/h1b_input.csv"
INPUT="input/h1b_input.csv"
OCCUPATIONS_OUTPUT="output/top_10_occupations.txt"
STATES_OUTPUT="output/top_10_states.txt"

scala -classpath ./target h1b.Main $INPUT $OCCUPATIONS_OUTPUT $STATES_OUTPUT