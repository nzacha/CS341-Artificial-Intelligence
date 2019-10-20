package hw1.Sokoban;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import hw1.gui.Renderer;

public class SokobanAstarSearch {
	private static final int NO_HEURISTIC = 0;
	private static final int HEURISTIC_1 = 1;
	private static final int HEURISTIC_2 = 2;
	private static boolean DEBUG = false, PRINT_ALL_PATHS = false, SHOW_GRAPHICS = false;
	private static int method = 1;

	private SokobanPuzzle puzzle;
	private SokobanNode solution;
	private Renderer renderer;
	private int nodes_created = 0;
	private int totalBranches = 0;

	public SokobanAstarSearch(SokobanPuzzle instance) {
		this.puzzle = instance;
		if (SHOW_GRAPHICS)
			renderer = new Renderer(instance);
	}

	public void start() {
		SokobanNode source = new SokobanNode(puzzle);
		PriorityQueue<SokobanNode> queue = new PriorityQueue<SokobanNode>(20, new SokobanNodeComparator());

		// cost from start
		source.g_score = 0;
		queue.add(source);
		nodes_created++;
		totalBranches++;

		if (DEBUG) {
			System.out.println("Source Puzzle:");
			System.out.printf("Sokoban: (%d,%d)\n", source.getSokobanPos().x, source.getSokobanPos().y);
			SokobanPuzzle.printPuzzle(source.getPuzzle());
		}

		boolean solved = false;
		while ((!queue.isEmpty()) && (!solved)) {
			// the node in having the lowest f_score value
			SokobanNode current = queue.poll();
			if (PRINT_ALL_PATHS)
				printPath(current, false);

			int branchesMade = 0;
			// check every child of current node
			for (Direction d : Direction.values()) {
				if (!d.canMove(current.getPuzzle(), current.getSokobanPos()))
					continue;

				SokobanNode child = new SokobanNode(current, d);
				// goal found
				if (child.getPending() == 0) {
					solution = child;
					solved = true;
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
				while (node != null) {
					if (node.equals(child) && node.f_score < child.f_score) {
						f = true;
					}
					node = node.parent;
				}
				if (f || child.containsBlockedBox()) {
					continue;
				}

				nodes_created++;
				branchesMade++;
				if (branchesMade == 1)
					child.setBranchID(current.getBranchID());
				else
					child.setBranchID(++totalBranches);

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
		int hSum = 0;
		int dClosestBox = Integer.MAX_VALUE;
		for (Point b : boxes) {
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

	public void printPath(SokobanNode pathTail, boolean detailed) {
		if (pathTail == null) {
			System.out.println("Path is empty");
			System.exit(0);
			return;
		}
		List<SokobanNode> path = new ArrayList<SokobanNode>();
		for (SokobanNode node = pathTail; node != null; node = node.parent) {
			path.add(node);
		}
		int branchID = path.get(0).getBranchID();
		Collections.reverse(path);

		if (SHOW_GRAPHICS)
			for (int i = 0; i < path.size(); i++) {
				renderer.update(path.get(i), branchID);
			}

		if (!detailed)
			return;

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
		SokobanAstarSearch.method = Integer.parseInt(args[0]);
		String fileName = args[1];
		for (int i = 2; i < args.length; i++) {
			String arg = args[i];
			switch (arg) {
			case "-all":
			case "-a":
				PRINT_ALL_PATHS = true;
				System.out.println("\t>wll display all branches");
				break;
			case "-d":
			case "-debug":
				DEBUG = true;
				System.out.println("\t>will display debug info");
				break;
			case "-g":
			case "-gui":
			case "-graphic":
				SHOW_GRAPHICS = true;
				System.out.println("\t>will display puzzle graphically");
				break;
			default:
				System.out.println("Unkown argument [" + arg + "]");
			}
		}
		SokobanPuzzle puzzle = new SokobanPuzzle("SOKOBAN_EXAMPLES_TESTSET/" + fileName);

		if (method == 0)
			System.out.println("Running with no heuristic");
		else if (method == 1)
			System.out.println("Running with heuristic 1");
		else if (method == 2)
			System.out.println("Running with heuristic 2");
		else {
			System.out.println("Invalid method");
			throw new InputMismatchException();
		}

		SokobanAstarSearch search = new SokobanAstarSearch(puzzle);
		long start = System.nanoTime();
		search.start();
		long end = System.nanoTime();
		if (search.solution != null) {
			search.printPath(search.solution, true);
			System.out.printf("Total run time: %.3f ms.\n", (double) (end - start) / 1000000);
			System.out.printf("Total nodes created: %d\n", search.nodes_created);
		} else {
			System.out.println("No solution was found! :(");
		}
	}
}
