package hw2;

import javax.tools.StandardLocation;
import java.io.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class SAT_Solver {
    private static final char EMPTY = '0', FULL = '1', POSITIVE = '+', NEGATIVE = '-';
    private int nodeCount;
    private double f_pos = 0.5, f_neg = 0.5, density = 0.5;
    private char graphValues[][], graphAttributes[][];
    private int literals = 0;
    private String fileName = "Generated Graph";

    public SAT_Solver(String fileName) {
        this.fileName = fileName;

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
        int edges = (int) ((density * (nodeCount * (nodeCount - 1))) / 2);

        double f_max = f_pos + f_neg;
        double normalized_pos = f_pos / f_max;

        graphValues = new char[nodeCount][nodeCount];
        graphAttributes = new char[nodeCount][nodeCount];
        Random rand = new Random();
        int remaining = nodeCount * (nodeCount - 1) / 2 - 1;
        for (int i = 0; i < nodeCount; i++) {
            for (int j = 0; j < nodeCount; j++) {
                if (i == j) {
                    graphValues[i][j] = EMPTY;
                    graphValues[j][i] = EMPTY;
                }
                if (i <= j) {
                    continue;
                }
                double chance = 0;
                if (remaining != 0)
                    chance = edges * 1.0 / (remaining);
                if (rand.nextDouble() < chance) {
                    graphValues[i][j] = FULL;
                    graphValues[j][i] = FULL;
                    edges--;
                } else {
                    graphValues[i][j] = EMPTY;
                    graphValues[j][i] = EMPTY;
                }
                remaining--;
            }
        }

        edges = (int) ((density * (nodeCount * (nodeCount - 1))) / 2);
        remaining = (int) ((density * (nodeCount * (nodeCount - 1))) / 2) - 1;
        int pos = (int) (edges * normalized_pos);
        for (int i = 0; i < nodeCount; i++) {
            for (int j = 0; j < nodeCount; j++) {
                graphAttributes[i][j] = EMPTY;
                if (graphValues[i][j] == FULL) {
                    if (i < j)
                        continue;
                    double chance = 0;
                    if (remaining != 0)
                        chance = (pos * 1.0) / remaining;
                    if (rand.nextDouble() < chance) {
                        graphAttributes[i][j] = POSITIVE;
                        graphAttributes[j][i] = POSITIVE;
                        pos--;
                    } else {
                        graphAttributes[i][j] = NEGATIVE;
                        graphAttributes[j][i] = NEGATIVE;
                    }
                    remaining--;
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
                        for (int k = 1; k <= 3; k++) {
                            text.append(String.format("%d -%d", i * 3 + k, j * 3 + k));
                            appendLine(text);
                            text.append(String.format("-%d %d", i * 3 + k, j * 3 + k));
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
                text.append(i * 3 + k);
                if (i < nodeCount - 1)
                    text.append(" ");
            }
            appendLine(text);
        }

        return String.format("p cnf %d %d%s%s", nodeCount * 3, literals, System.lineSeparator(), text.toString());
    }

    private void appendLine(StringBuilder sb) {
        sb.append(" 0" + System.lineSeparator());
        literals++;
    }

    public String executeCommand(String[] commands) {
        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(commands);
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
            br.close();
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
    }

    public void generateResult(String results) {
        String[] sets = results.split(" ");
        ArrayList set1 = new ArrayList();
        ArrayList set2 = new ArrayList();
        ArrayList set3 = new ArrayList();
        for (int i = 0; i < sets.length; i++) {
            try {
                int literal = Integer.parseInt(sets[i]);
                int index = ((literal - 1) / 3) + 1;
                if (literal > 0)
                    switch (literal % 3) {
                        case 0:
                            set1.add(index);
                            break;
                        case 1:
                            set2.add(index);
                            break;
                        case 2:
                            set3.add(index);
                            break;
                    }
            } catch (NumberFormatException nfe) {

            }
        }

        FileWriter fw = null;
        BufferedWriter br;
        try {
            fw = new FileWriter("etc/hw2/results.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        br = new BufferedWriter(fw);

        if (set1.isEmpty() || set2.isEmpty() || set3.isEmpty()) {
            try {
                br.write(fileName + System.lineSeparator());
                br.write("=======================================================================" + System.lineSeparator());
                br.write("The given graph cannot be divided into a total of three (3) sets" + System.lineSeparator());
                br.close();
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            br.write(fileName + System.lineSeparator());
            br.write("=======================================================================" + System.lineSeparator());
            br.write("The three (3) sets that can be generated based on the matrix above are:" + System.lineSeparator());
            br.write("Set1 = " + set1 + System.lineSeparator());
            br.write("Set2 = " + set2 + System.lineSeparator());
            br.write("Set3 = " + set3 + System.lineSeparator());
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("Type 0 to read graph from file");
        System.out.println("Type 1 generate graph");
        Scanner scan = new Scanner(System.in);
        SAT_Solver ss = null;
        switch (scan.nextInt()) {
            case 0:
                scan.nextLine();
                System.out.println("Give me the file name");
                ss = new SAT_Solver(scan.nextLine());
                //ss = new SAT_Solver("etc/hw2/testset/EX1.txt");
                break;
            case 1:
                System.out.println("Give me the number of nodes");
                int arg0 = scan.nextInt();
                System.out.println("Give me the density of the edges");
                double arg1 = scan.nextDouble();
                System.out.println("Give me the frequency of the positive edges");
                double arg2 = scan.nextDouble();
                System.out.println("Give me the frequency of the negative edges");
                double arg3 = scan.nextDouble();
                ss = new SAT_Solver(arg0, arg1, arg2, arg3);
                break;
            default:
                throw new InputMismatchException();
        }

        ss.debug();
        ss.saveCNF();

        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        String commands[] = {"/bin/sh", "-c", "etc/hw2/lingeling_solver/lingeling etc/hw2/expr.cnf | grep 'v '\n"};
        String results = ss.executeCommand(commands);

        ss.generateResult(results);
    }
}
