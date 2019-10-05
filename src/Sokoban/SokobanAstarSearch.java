package Sokoban;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class SokobanAstarSearch {
	private static final int NO_HEURISTIC = 0;
	private static final int HEURISTIC_1 = 1;
	private static final int HEURISTIC_2 = 2;
	private static final boolean DEBUG = false, PRINT_PATH = false;
	private static int method = 1;

	private SokobanPuzzle puzzle;
	private SokobanNode solution;

	public SokobanAstarSearch(SokobanPuzzle instance, int method) {
		this.puzzle = instance;
		SokobanAstarSearch.method = method;
	}

	public void start() {
		SokobanNode source = new SokobanNode(puzzle);

		Set<SokobanNode> explored = new HashSet<SokobanNode>();
		PriorityQueue<SokobanNode> queue = new PriorityQueue<SokobanNode>(20, new SokobanNodeComparator());

		// cost from start
		source.g_score = 0;
		queue.add(source);

		if (DEBUG) {
			System.out.println("Source Puzzle:");
			System.out.printf("Sokoban: (%d,%d)\n", source.getSokobanPos().x, source.getSokobanPos().y);
			SokobanPuzzle.printPuzzle(source.getPuzzle());
		}

		boolean found = false;
		while ((!queue.isEmpty()) && (!found)) {
			// the node in having the lowest f_score value
			SokobanNode current = queue.poll();

			explored.add(current);

			// check every child of current node
			for (Direction d : Direction.values()) {
				if (!d.canMove(current.getPuzzle(), current.getSokobanPos()))
					continue;
				SokobanNode child = new SokobanNode(current, d);
				// goal found
				if (child.getPending() == 0) {
					solution = child;
					found = true;
				}

				// IF we already explored a node with the same UNTARGETED BOXES's STATE (number
				// & locations)
				// AND the f_score of the previously explored state is lower than the current
				// one
				// AND sokoban is at the SAME position
				// THEN we know that the new state will never be as efficient as the previously
				// explored one
				boolean f = false;
				SokobanNode node = child.parent;
				while (node!=null) {
					if (node.equals(child) && node.f_score < child.f_score) {
						f = true;
					}
					node=node.parent;
				}
				if (f)
					continue;
				if (child.containsBlockedBox())
					continue;

				queue.add(child);
				if (DEBUG) {
					System.out.printf("Adding new child, Sokoban moves: %s to: (%d,%d) scores(g: %d, f: %d)\n", d,
							child.getSokobanPos().x, child.getSokobanPos().y, child.g_score, child.f_score);
					SokobanPuzzle.printPuzzle(child.getPuzzle());
				}

			}
		}

	}

	public static int findHeuristic(Set<Point> boxes, Set<Point> targets, Point sokoban) {
		switch (method) {
		case NO_HEURISTIC:
			return 0;
		case HEURISTIC_1:
			return findHeuristic_1(boxes, targets);
		case HEURISTIC_2:
			return findHeuristic_2(boxes, targets, sokoban);
		default:
			throw new InputMismatchException();
		}
	}

	private static int findHeuristic_1(Set<Point> boxes, Set<Point> targets) {
		Set<Point> availableTargets = new HashSet<Point>(targets);
		int hSum = 0;

		for (Point b : boxes) {
			int distance = Integer.MAX_VALUE;
			Point target = null;
			for (Point t : availableTargets) {
				if (b.equals(t)) {
					throw new InputMismatchException();
				}
				int d = manhattanDistance(b, t);
				if (distance > d) {
					target = t;
					distance = d;
				}
			}
			availableTargets.remove(target);
			hSum += distance;
		}
		return hSum;
	}

	private static int findHeuristic_2(Set<Point> boxes, Set<Point> targets, Point sokobanPos) {
		Set<Point> availableTargets = new HashSet<Point>(targets);

		int hSum = 0;
		int dClosestBox = Integer.MAX_VALUE;
		for (Point b : boxes) {
			int distance = Integer.MAX_VALUE;
			Point target = null;
			for (Point t : availableTargets) {
				if (b.equals(t)) {
					throw new InputMismatchException();
				}
				int d = manhattanDistance(b, t);
				if (distance > d) {
					target = t;
					distance = d;
				}
			}
			availableTargets.remove(target);
			hSum += distance;

			int temp = manhattanDistance(b, sokobanPos);
			if (dClosestBox > temp) {
				dClosestBox = temp;
			}
		}
		hSum += dClosestBox;
		hSum += boxes.size();
		return hSum;
	}

	private static int manhattanDistance(Point a, Point b) {
		return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
	}

	public void printPath() {
		if (solution == null) {
			System.out.println("Solution not found :(");
			return;
		}
		List<SokobanNode> path = new ArrayList<SokobanNode>();
		for (SokobanNode node = solution; node != null; node = node.parent) {
			path.add(node);
		}
		Collections.reverse(path);

		System.out.println("The solution is:");
		if (PRINT_PATH)
			for (int i = 0; i < path.size(); i++) {
				SokobanPuzzle.printPuzzle(path.get(i).getPuzzle());
			}

		System.out.print("Path:");
		for (int i = 0; i < path.size(); i++) {
			if (i % 10 == 0)
				System.out.print("\n\t");
			System.out.printf("%s(%d,%d), ",
					path.get(i).getDirection() == null ? "START: " : path.get(i).getDirection() + ": ",
					path.get(i).getSokobanPos().x, path.get(i).getSokobanPos().y);
		}
		System.out.println();
		System.out.println("Total Steps: " + (path.size() - 1));
	}

	public static void main(String[] args) {
		SokobanPuzzle puzzle = new SokobanPuzzle("SOKOBAN_EXAMPLES_TESTSET/SOK_HARD1.txt");

		SokobanAstarSearch search = new SokobanAstarSearch(puzzle, SokobanAstarSearch.NO_HEURISTIC);
		System.out.println("Running with no heuristic");
		long start = System.nanoTime();
		search.start();
		long end = System.nanoTime();
		search.printPath();
		System.out.printf("Total run time: %.3f ms.\n", (double) (end - start) / 1000000);

		search = new SokobanAstarSearch(puzzle, SokobanAstarSearch.HEURISTIC_1);
		System.out.println("\nRunning with heuristic 1");
		start = System.nanoTime();
		search.start();
		end = System.nanoTime();
		search.printPath();
		System.out.printf("Total run time: %.3f ms.\n", (double) (end - start) / 1000000);

		search = new SokobanAstarSearch(puzzle, SokobanAstarSearch.HEURISTIC_2);
		System.out.println("\nRunning with heuristic 2");
		start = System.nanoTime();
		search.start();
		end = System.nanoTime();
		search.printPath();
		System.out.printf("Total run time: %.3f ms.\n", (double) (end - start) / 1000000);
	}
}
