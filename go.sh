#!/bin/bash -e

echo "-- Setup -----------------------------"
mkdir -p build

echo "-- Compilation -----------------------"
javac -d build -source 1.6 -sourcepath src src/*.java

echo "-- Test ------------------------------"
cp data/input.txt build/
cp data/output.txt build/output-reference.txt
cd build/
time java Balancer
diff output.txt output-reference.txt && echo "OK"

