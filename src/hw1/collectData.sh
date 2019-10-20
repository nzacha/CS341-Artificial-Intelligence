#!/bin/bash

javac -d bin src/**/*.java

rm -rf results
mkdir results

for ii in {1..2}
do
	echo "Heuristic number: $ii "
	for i in {1..14}
	do
		echo "Problem number: $i "
		for iii in {1..12}
		do
		java -cp ./bin Sokoban/SokobanAstarSearch $ii problem$i.txt >> results/result-$i-$ii.txt
		done
	done
done
