package hw1.Sokoban;

import java.awt.Point;
import java.util.InputMismatchException;

public enum Direction {
	UP, RIGHT, DOWN, LEFT;

	public boolean canMove(char[][] puzzle, Point sakobanPos) {
		Point newLoc = move(sakobanPos);
		switch (puzzle[newLoc.y][newLoc.x]) {
		case SokobanPuzzle.WALL:
			return false;
		case SokobanPuzzle.EMPTY:
		case SokobanPuzzle.TARGET:
			return true;
		case SokobanPuzzle.BOX:
		case SokobanPuzzle.TARGET_BOX:
			Point newBoxLoc = move(newLoc);
			switch (puzzle[newBoxLoc.y][newBoxLoc.x]) {
			case SokobanPuzzle.EMPTY:
			case SokobanPuzzle.TARGET:
				return true;
			default:
				return false;
			}
		default:
			throw new InputMismatchException();
		}
	}

	public Point move(Point location) {
		Point loc = new Point(location);
		switch (this) {
		case UP:
			loc.y--;
			break;
		case RIGHT:
			loc.x++;
			break;
		case DOWN:
			loc.y++;
			break;
		case LEFT:
			loc.x--;
			break;
		default:
			throw new InputMismatchException();
		}
		return loc;
	}

	public static void main(String[] args) {

		System.out.println("Test 1\n");
		Direction d = Direction.RIGHT;
		Point pos = new Point(1, 1);
		System.out.println(pos);
		System.out.println("moving: " + d);
		pos = d.move(pos);
		System.out.println(pos);

		System.out.println("\nTest 2\n");
		SokobanPuzzle sp = new SokobanPuzzle("SOKOBAN_EXAMPLES_TESTSET/input.txt");

		SokobanNode node = new SokobanNode(sp);
		char[][] puzzle = node.getPuzzle();
		SokobanPuzzle.printPuzzle(puzzle);

		SokobanNode newNode = new SokobanNode(node, d);
		puzzle = newNode.getPuzzle();
		SokobanPuzzle.printPuzzle(puzzle);
		System.out.println("----------------------\n");
	}
}
