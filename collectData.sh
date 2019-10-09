#!/bin/bash

javac -d bin src/**/*.java

for i in {1..2}
do
	echo "Heuristic number: $i "
	for ii in {0..2}
	do
		java -cp ./bin Sokoban/SokobanAstarSearch $i input.txt
	done
done
