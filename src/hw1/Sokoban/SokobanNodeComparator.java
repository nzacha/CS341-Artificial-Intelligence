package hw1.Sokoban;

import java.util.Comparator;

public class SokobanNodeComparator implements Comparator<SokobanNode> {
	// override compare method
	public int compare(SokobanNode i, SokobanNode j) {
		if (i.f_score > j.f_score) {
			return 1;
		} else if (i.f_score < j.f_score) {
			return -1;
		} else {
			return 0;
		}
	}

}
