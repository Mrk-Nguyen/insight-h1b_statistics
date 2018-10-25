#!/usr/bin/env bash

echo "Cleaning up target folder (if it exists)"
rm -rf ./target
mkdir ./target

echo "Compiling scala code"
scalac -d ./target ./src/main/scala/h1b/*.scala ./src/test/scala/h1b/H1BTestSuite.scala

echo "Running compiled scala code"

scala -classpath ./target h1b.H1BTestSuite