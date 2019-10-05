package Sokoban;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;

public class SokobanPuzzle {
	public static final char WALL = '#';
	public static final char EMPTY = ' ';
	public static final char TARGET = '.';
	public static final char BOX = '$';
	public static final char TARGET_BOX = '*';
	public static final char SOKOBAN = '@';
	public static final char TARGET_SOKOBAN = '+';
	private static boolean DEBUG = false;

	public static void removeBoxAt(Point location, char[][] puzzle) {
		switch (puzzle[location.y][location.x]) {
		case BOX:
			puzzle[location.y][location.x] = EMPTY;
			break;
		case TARGET_BOX:
			puzzle[location.y][location.x] = TARGET;
			break;
		default:
			System.out.println(String.format("(%d,%d)is: ", location.x, location.y) + puzzle[location.y][location.x]);
			throw new InputMismatchException();
		}
	}

	public static void addBoxAt(Point location, char[][] puzzle) {
		switch (puzzle[location.y][location.x]) {
		case EMPTY:
			puzzle[location.y][location.x] = BOX;
			break;
		case TARGET:
			puzzle[location.y][location.x] = TARGET_BOX;
			break;
		default:
			System.out.println(String.format("(%d,%d)is: ", location.x, location.y) + puzzle[location.y][location.x]);
			throw new InputMismatchException();
		}
	}

	public static void placeSokoban(Point location, char[][] puzzle) {
		switch (puzzle[location.y][location.x]) {
		case EMPTY:
			puzzle[location.y][location.x] = SOKOBAN;
			break;
		case TARGET:
			puzzle[location.y][location.x] = TARGET_SOKOBAN;
			break;
		default:
			System.out.println(String.format("(%d,%d)is: ", location.x, location.y) + puzzle[location.y][location.x]);
			throw new InputMismatchException();
		}
	}

	public static void printPuzzle(char[][] puzzle) {
		for (int i = 0; i < puzzle.length; i++) {
			for (int j = 0; j < puzzle[0].length; j++)
				System.out.print(puzzle[i][j]);
			System.out.println();
		}
		System.out.println();
	}

	private char[][] puzzle;
	private int length;

	public SokobanPuzzle(String fileName) {
		puzzle = readFromFile(fileName);
		if (DEBUG)
			printPuzzle(puzzle);
	}

	private char[][] readFromFile(String fileName) {
		FileReader fr;
		BufferedReader br;
		ArrayList<String> rows = new ArrayList<String>();

		try {
			fr = new FileReader(fileName);
			br = new BufferedReader(fr);

			String line = br.readLine();
			length = Integer.parseInt(line);
			for (int i = 0; i < length; i++) {
				line = br.readLine();
				rows.add(line);
			}
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		} catch (NumberFormatException nfe) {
			System.out.println(nfe.getMessage());
		}

		int size = 0;
		for (int i = 0; i < rows.size(); i++) {
			int tmp = rows.get(i).length();
			if (tmp > size) {
				size = tmp;
			}
		}
		char[][] puzzle = new char[length][size];

		if (DEBUG)
			System.out.printf("Puzzle has dimensions: (%d,%d)\n", length, size);
		for (int i = 0; i < rows.size(); i++) {
			String line = rows.get(i);
			for (int j = 0; j < line.length(); j++) {
				puzzle[i][j] = line.charAt(j);
			}
		}

		return puzzle;
	}

	public char[][] getPuzzle() {
		return puzzle;
	}

	public static void main(String[] args) {
		SokobanPuzzle.DEBUG = true;
		new SokobanPuzzle("input.txt");
	}
}
