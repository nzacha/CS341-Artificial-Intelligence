#!/bin/bash

javac -d bin src/**/*.java

rm -rf results
mkdir results

for iii in {1..12}
do
	for ii in {1..2}
	do
		echo "Heuristic number: $ii "
		for i in {1..10}
		do
			echo "Problem number: $i "
			java -cp ./bin Sokoban/SokobanAstarSearch $ii problem$i.txt >> results/result_$i_$ii.txt
		done
	done
done
