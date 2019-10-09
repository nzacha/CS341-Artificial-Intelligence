#!/bin/bash

rm -rf time
mkdir time

for ii in {1..2}
do
	echo "Heuristic number: $ii "
	for i in {1..14}
	do
		echo "Problem number: $i "
		cat results/result-$i-$ii.txt | grep "Total nodes created:" | cut -d " " -f4 >> time/result-$i-$ii.txt
	done
done
