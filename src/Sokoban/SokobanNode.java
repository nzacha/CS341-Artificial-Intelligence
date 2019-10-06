package Sokoban;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

public class SokobanNode {
	public static final int COST = 1;

	@SuppressWarnings("unused")
	private static final boolean DEBUG = false;
	// heuristic
	public final int h_score;
	// best actual cost
	public int g_score = 0;
	// best guess cost + heuristic
	public int f_score = 0;
	public SokobanNode parent;

	private char[][] puzzle;
	private Set<Point> boxes = new HashSet<Point>();
	private Set<Point> targets = new HashSet<Point>();
	private Point sokobanPos;
	private int pending = 0;
	private Direction direction = null;

	public SokobanNode(SokobanPuzzle sokobanPuzzle) {
		char[][] prevPuzzle = sokobanPuzzle.getPuzzle();
		this.puzzle = new char[prevPuzzle.length][prevPuzzle[0].length];
		for (int y = 0; y < prevPuzzle.length; y++) {
			for (int x = 0; x < prevPuzzle[0].length; x++) {
				this.puzzle[y][x] = prevPuzzle[y][x];
				switch (prevPuzzle[y][x]) {
				case SokobanPuzzle.BOX:
					pending++;
					boxes.add(new Point(x, y));
					break;
				case SokobanPuzzle.SOKOBAN:
					sokobanPos = new Point(x, y);
					break;
				case SokobanPuzzle.TARGET_SOKOBAN:
					sokobanPos = new Point(x, y);
				case SokobanPuzzle.TARGET:
					targets.add(new Point(x, y));
					break;
				}
			}
		}

		h_score = SokobanAstarSearch.findHeuristic(boxes, targets, sokobanPos);
	}

	public SokobanNode(SokobanNode node, Direction d) {
		char[][] prevPuzzle = node.getPuzzle();
		this.puzzle = new char[prevPuzzle.length][prevPuzzle[0].length];
		this.direction = d;
		this.parent = node;

		for (int y = 0; y < prevPuzzle.length; y++) {
			for (int x = 0; x < prevPuzzle[0].length; x++) {
				this.puzzle[y][x] = prevPuzzle[y][x];
				switch (prevPuzzle[y][x]) {
				case SokobanPuzzle.BOX:
					pending++;
					boxes.add(new Point(x, y));
					break;
				case SokobanPuzzle.SOKOBAN:
					puzzle[y][x] = SokobanPuzzle.EMPTY;
					sokobanPos = d.move(new Point(x, y));
					break;
				case SokobanPuzzle.TARGET_SOKOBAN:
					puzzle[y][x] = SokobanPuzzle.TARGET;
					sokobanPos = d.move(new Point(x, y));
				case SokobanPuzzle.TARGET:
					targets.add(new Point(x, y));
					break;
				}
			}
		}

		// place sokoban
		if (puzzle[sokobanPos.y][sokobanPos.x] == SokobanPuzzle.BOX) {
			SokobanPuzzle.removeBoxAt(sokobanPos, puzzle);
			boxes.remove(sokobanPos);
			pending--;
			SokobanPuzzle.placeSokoban(sokobanPos, puzzle);
			Point newLoc = d.move(sokobanPos);
			SokobanPuzzle.addBoxAt(newLoc, puzzle);
			if (puzzle[newLoc.y][newLoc.x] != SokobanPuzzle.TARGET_BOX) {
				boxes.add(newLoc);
				pending++;
			}
		} else if (puzzle[sokobanPos.y][sokobanPos.x] == SokobanPuzzle.TARGET_BOX) {
			SokobanPuzzle.removeBoxAt(sokobanPos, puzzle);
			SokobanPuzzle.placeSokoban(sokobanPos, puzzle);
			Point newLoc = d.move(sokobanPos);
			SokobanPuzzle.addBoxAt(newLoc, puzzle);
			if (puzzle[newLoc.y][newLoc.x] != SokobanPuzzle.TARGET_BOX) {
				boxes.add(newLoc);
				pending++;
			}
		} else {
			SokobanPuzzle.placeSokoban(sokobanPos, puzzle);
		}

		h_score = SokobanAstarSearch.findHeuristic(boxes, targets, sokobanPos);
		g_score = node.g_score + COST;
		f_score = g_score + h_score;
	}

	public char[][] getPuzzle() {
		return puzzle;
	}

	public Set<Point> getBoxes() {
		return boxes;
	}

	public Set<Point> getTargets() {
		return targets;
	}

	public Point getSokobanPos() {
		return sokobanPos;
	}

	public int getPending() {
		return pending;
	}

	public Direction getDirection() {
		return direction;
	}

	public boolean containsBlockedBox() {
		for (Point b : boxes) {
			boolean[] walls = { false, false, false, false };
			int iterator = 0;
			for (Direction d : Direction.values()) {
				Point newLoc = d.move(b);
				if (puzzle[newLoc.y][newLoc.x] == SokobanPuzzle.WALL)
					walls[iterator] = true;
				iterator++;
			}
			for (int i = 0; i < walls.length; i++)
				if (walls[i] && walls[(i + 1) % 4])
					return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o == this)
			return true;
		if (!(o instanceof SokobanNode))
			return false;

		SokobanNode c = (SokobanNode) o;
		// same (untargeted) box state and same player position
		return boxes.equals(c.boxes) && sokobanPos.equals(c.sokobanPos);
	}

	@Override
	public String toString() {
		String ret = String.format("Sokoban: (%d,%d)\n", sokobanPos.x, sokobanPos.y);
		ret += String.format("(Untargeted) Boxes: %s\n", boxes);
		ret += String.format("g_score: %d, h_score: %d, f_score: %d\n", g_score, h_score, f_score);
		return ret;
	}

	public static void main(String[] args) {
		SokobanPuzzle sp = new SokobanPuzzle("SOKOBAN_EXAMPLES_TESTSET/input.txt");
		SokobanNode source = new SokobanNode(sp);
		SokobanNode node = new SokobanNode(source, Direction.RIGHT);
		SokobanNode node2 = new SokobanNode(node, Direction.DOWN);
		SokobanNode node3 = new SokobanNode(node2, Direction.UP);
		SokobanNode similar = new SokobanNode(node3, Direction.LEFT);
		Set<SokobanNode> set = new HashSet<SokobanNode>();
		set.add(source);
		System.out.println(source.hashCode());
		System.out.println(source);
		System.out.println(similar.hashCode());
		System.out.println(similar);

		System.out.println(source.equals(similar));
		System.out.println(set.contains(similar));

		SokobanNode source2 = new SokobanNode(sp);
		System.out.println(source.equals(source2));
		System.out.println("---------------------\n");

		SokobanNode node1 = new SokobanNode(new SokobanPuzzle("SOKOBAN_EXAMPLES_TESTSET/Example1.txt"));
		SokobanPuzzle.printPuzzle(node1.getPuzzle());
		node2 = new SokobanNode(new SokobanPuzzle("SOKOBAN_EXAMPLES_TESTSET/Example2.txt"));
		SokobanPuzzle.printPuzzle(node2.getPuzzle());
		System.out.println(node1.equals(node2));

		node3 = new SokobanNode(new SokobanPuzzle("SOKOBAN_EXAMPLES_TESTSET/Example3.txt"));
		System.out.println(node3.containsBlockedBox());
	}
}