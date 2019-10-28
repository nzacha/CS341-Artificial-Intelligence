package hw2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class SAT_Solver {
	private static final char EMPTY = '0', FULL = '1', POSITIVE = '+', NEGATIVE = '-';
	private int nodeCount;
	private double f_pos = 0.5, f_neg = 0.5, density = 0.5;
	private char graphValues[][], graphAttributes[][];
	private int literals = 0;

	public SAT_Solver(String fileName) {
		FileReader fr = null;
		try {
			fr = new FileReader(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(fr);
		try {
			nodeCount = Integer.parseInt(br.readLine());
			f_pos = Double.parseDouble(br.readLine());
			f_neg = Double.parseDouble(br.readLine());
			density = Double.parseDouble(br.readLine());
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		graphValues = new char[nodeCount][nodeCount];
		graphAttributes = new char[nodeCount][nodeCount];
		String line = null;
		try {
			for (int i = 0; i < nodeCount; i++) {
				line = br.readLine();
				String[] temp = line.split(" ");
				for (int j = 0; j < nodeCount; j++) {
					graphValues[i][j] = temp[j].charAt(0);
				}
			}

			br.readLine();

			for (int i = 0; i < nodeCount; i++) {
				line = br.readLine();
				String[] temp = line.split(" ");
				for (int j = 0; j < nodeCount; j++) {
					graphAttributes[i][j] = temp[j].charAt(0);
				}
			}
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public SAT_Solver(int newNodeCount, double new_f_pos, double new_f_neg, double newDensity) {
		this.nodeCount = newNodeCount;
		this.f_pos = new_f_pos;
		this.f_neg = new_f_neg;
		this.density = newDensity;

		double f_max = f_pos + f_neg;
		double normalized_pos = f_pos / f_max;

		graphValues = new char[nodeCount][nodeCount];
		graphAttributes = new char[nodeCount][nodeCount];
		Random rand = new Random();
		for (int i = 0; i < nodeCount; i++) {
			for (int j = 0; j < nodeCount; j++) {
				if (rand.nextDouble() > density && i != j) {
					graphValues[i][j] = FULL;
					graphValues[j][i] = FULL;
				} else {
					graphValues[i][j] = EMPTY;
					graphValues[j][i] = EMPTY;
				}
			}
		}

		for (int i = 0; i < nodeCount; i++) {
			for (int j = 0; j < nodeCount; j++) {
				graphAttributes[i][j] = EMPTY;
				if (graphValues[i][j] == FULL) {
					if (rand.nextDouble() > normalized_pos) {
						graphAttributes[i][j] = POSITIVE;
						graphAttributes[j][i] = POSITIVE;
					} else {
						graphAttributes[i][j] = NEGATIVE;
						graphAttributes[j][i] = NEGATIVE;
					}
				}
			}
		}

	}

	private String generateCNF() {
		StringBuilder text = new StringBuilder();
		for (int i = 1; i < nodeCount + 1; i += 3) {
			text.append(String.format("%d %d %d", i, i + 1, i + 2));
			appendLine(text);
			text.append(String.format("-%d -%d", i, i + 1));
			appendLine(text);
			text.append(String.format("-%d -%d", i, i + 2));
			appendLine(text);
			text.append(String.format("-%d -%d", i + 1, i + 2));
			appendLine(text);
		}

		for (int i = 0; i < nodeCount; i++) {
			for (int j = 0; j < nodeCount; j++) {
				if (graphValues[i][j] != FULL)
					continue;

				switch (graphAttributes[i][j]) {
				case POSITIVE:
					for (int k = 0; k < 3; k++) {
						text.append(String.format("%d -%d", i * 3 + 1 + k, (j * 3 + 3 - k)));
						appendLine(text);
						text.append(String.format("-%d %d", (i * 3 + 3 - k), j * 3 + 1 + k));
						appendLine(text);
					}
					break;
				case NEGATIVE:
					for (int k = 0; k < 3; k++) {
						text.append(String.format("-%d -%d", i * 3 + 1 + k, j * 3 + 1 + k));
						appendLine(text);
					}
					graphValues[j][i] = EMPTY;
					break;
				}
			}
		}

		for (int k = 1; k <= 3; k++) {
			for (int i = 0; i < nodeCount; i++) {
				text.append(String.format("%d ", i * 3 + k));
			}
			appendLine(text);
		}

		return String.format("p cnf %d %d%s%s", nodeCount * 3, literals, System.lineSeparator(), text.toString());
	}

	private void appendLine(StringBuilder sb) {
		sb.append(" 0" + System.lineSeparator());
		literals++;
	}

	public static String executeCommand(String command) {
		StringBuffer output = new StringBuffer();

		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line = "";
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return output.toString();
	}

	public void saveCNF() {
		String cnf = generateCNF();
		FileWriter fw = null;
		BufferedWriter br;
		try {
			fw = new FileWriter("etc/hw2/expr.cnf");
		} catch (IOException e) {
			e.printStackTrace();
		}
		br = new BufferedWriter(fw);
		
		try {
			br.write(cnf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void debug() {
		System.out.println("N size: " + nodeCount);
		System.out.println("Frequency of positives: " + f_pos);
		System.out.println("Frequency of negatives: " + f_neg);
		System.out.println("Edge density: " + density);
		System.out.println();

		System.out.println("Edges: ");
		for (int i = 0; i < nodeCount; i++) {
			for (int j = 0; j < nodeCount; j++) {
				System.out.print(graphValues[i][j] + " ");
			}
			System.out.println();
		}

		System.out.println();
		System.out.println("Edges Attributes: ");
		for (int i = 0; i < nodeCount; i++) {
			for (int j = 0; j < nodeCount; j++) {
				System.out.print(graphAttributes[i][j] + " ");
			}
			System.out.println();
		}

		System.out.println();
		System.out.println(generateCNF());

	}

	public static void main(String[] args) {
		// SAT_Solver ss = new SAT_Solver(5, 0.5, 0.5, 0.5);
		SAT_Solver ss = new SAT_Solver("etc/hw2/testset/EX1.txt");
		ss.debug();
		
		ss.saveCNF();
		
		System.out.println("Working Directory = " + System.getProperty("user.dir"));
		
		String commands = "etc/hw2/lingeling_solver/lingeling expr.cnf | grep 'v 1 \\|v -1'";
		SAT_Solver.executeCommand(commands);
	}
}
