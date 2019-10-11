package Sokoban;

import java.util.Comparator;

/*
 * Compares two SokobanNodes using the f_score
 */
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
