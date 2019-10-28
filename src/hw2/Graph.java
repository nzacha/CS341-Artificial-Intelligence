package hw2;

import java.io.*;
import java.util.Scanner;
import java.util.Random;

public class Graph {

    public static void main(String args[]) throws IOException {
        System.out.println("Please select operation");
        System.out.println("Select 1 to read a graph file and print output");
        System.out.println("Select 2 to insert values to create the graph");
        Scanner sc=new Scanner(System.in);
        int selection=sc.nextInt();
        char nd[][] = {{'0'}};
        char ws[][] = {{'0'}};
        int nodeCount = 0;
        if(selection==1){
            sc.nextLine();
            System.out.println("Please give the path to the file\n");
            String path=sc.nextLine();
            try {
                File f = new File(path);
                BufferedReader bf = new BufferedReader(new FileReader(f));
                String ns = bf.readLine();
                nodeCount = Integer.parseInt(ns);
                nd = new char[nodeCount][nodeCount];
                ws = new char[nodeCount][nodeCount];
                ns = bf.readLine();
                ns = bf.readLine();
                ns = bf.readLine();
                for (int i = 0; i < nodeCount; i++) {
                    ns = bf.readLine();
                    int j = 0;
                    for (char x : ns.toCharArray()) {
                        if (x != ' ') {
                            nd[i][j] = x;
                            j++;
                        }
                    }
                }
                ns = bf.readLine();
                for (int i = 0; i < nodeCount; i++) {
                    ns = bf.readLine();
                    int j = 0;
                    for (char x : ns.toCharArray()) {
                        if (x != ' ') {
                            ws[i][j] = x;
                            j++;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
            }
        }else if(selection==2) {
            System.out.println("Please give the number of nodes");
            nodeCount = sc.nextInt();
            sc.nextLine();
            System.out.println("Please give the density of the graph");
            double density = sc.nextDouble();
            sc.nextLine();
            System.out.println("Please give the percentage of positive edges");
            double positve = sc.nextDouble();
            System.out.println("Please give the percentage of negative edges");
            double negative = sc.nextDouble();
            if (positve + negative != 1) {
                System.out.println("Wrong percentages");
                System.exit(0);
            }

            int numEdges = (int) Math.round(density * (nodeCount * (nodeCount - 1)) / 2);
            nd = new char[nodeCount][nodeCount];
            ws = new char[nodeCount][nodeCount];
            for (int i = 0; i < nodeCount; i++) {
                for (int j = 0; j < nodeCount; j++) {
                    nd[i][j] = '0';
                    ws[i][j] = '0';
                }
            }
            int n1 = -1, n2;
            Random rand = new Random();
            boolean disc[] = new boolean[nodeCount];
            for (int i = 0; i < nodeCount; i++)
                disc[i] = false;
            for (int i = 0; i < numEdges; i++) {
                for (int k = 0; k < nodeCount; k++) {
                    for (int j = 0; j < nodeCount; j++)
                        if (nd[k][j] == '1')
                            disc[k] = true;
                }
                boolean stop = true;
                for (int j = 0; ((j < nodeCount) && (stop)); j++)
                    if (disc[j] == false) {
                        stop = false;
                        n1 = j;
                    }
                n2 = rand.nextInt(nodeCount);
                while ((n1 == n2) || (nd[n1][n2] == '1'))
                    n2 = rand.nextInt(nodeCount);
                nd[n1][n2] = '1';
                nd[n2][n1] = '1';
            }
            int numPos = (int) (numEdges * positve);
            int numNeg = (int) (numEdges * negative);
            int b = 0;
            if (numNeg + numPos < numEdges){
                if (rand.nextInt(2) == 1) {
                    numNeg++;
                } else {
                    numPos++;
                }
            }
            for(int i=0;i<nodeCount;i++){
                for(int j=0;j<nodeCount;j++){
                    if(nd[i][j]=='1')
                        if(rand.nextInt(2)==1) {
                            if (numPos > 0) {
                                ws[i][j] = '+';
                                ws[j][i] = '+';
                                numPos--;
                            } else {
                                ws[i][j] = '-';
                                ws[j][i] = '-';
                                numNeg--;
                            }
                        }else{
                            if (numNeg > 0) {
                                ws[i][j] = '-';
                                ws[j][i] = '-';
                                numNeg--;
                            } else {
                                ws[i][j] = '+';
                                ws[j][i] = '+';
                                numPos--;
                            }
                        }
                }
            }
            for(int i=0;i<nodeCount;i++) {
                for (int j = 0; j < nodeCount; j++) {
                    System.out.print(ws[i][j]);
                }
                System.out.println();
            }
        }else{
            System.out.println("Wrong selection given");
            System.exit(0);
        }




        String fline="";
        int count=0;
        int literals=nodeCount*3;
        int curr=1;
        fline="p cnf "+literals;
        String sat="";
        for(int i=0;i<nodeCount;i++){
            for(int j=i+curr;j<i+curr+3;j++){
               sat=sat+j+" ";
            }
            sat=sat+"0\n";
            count++;
            sat=sat+"-"+(i+curr)+" -"+(1+i+curr)+" 0\n";
            sat=sat+"-"+(i+curr)+" -"+(2+i+curr)+" 0\n";
            sat=sat+"-"+(1+i+curr)+" -"+(2+i+curr)+" 0\n";
            count+=3;
            curr+=2;
        }
        curr=1;
        int node=1;
        for(int i=0;i<nodeCount;i++){
            curr=1;
            for(int j=0;j<nodeCount;j++){
                if(nd[i][j]=='1'){
                    switch(ws[i][j]){
                        case ('+'):{
                            for(int k=0;k<3;k++){
                                sat=sat+(i+node+k)+" "+"-"+(j+curr+k)+" 0\n";
                                sat=sat+"-"+(i+node+k)+" "+(j+curr+k)+" 0\n";
                            }
                            count+=6;
                            ws[j][i]='0';
                            break;
                        }
                        case ('-'):{
                            for(int k=0;k<3;k++){
                                sat=sat+"-"+(i+node+k)+" "+"-"+(j+curr+k)+" 0\n";
                            }
                            count+=3;
                            ws[j][i]='0';
                            break;
                        }
                        default:break;
                    }
                }
                curr+=2;
            }
            node+=2;
        }
        for(int i=1;i<=nodeCount*3;i+=3){
            sat=sat+i+" ";
        }
        sat=sat+"0\n";
        for(int i=2;i<=nodeCount*3;i+=3){
            sat=sat+i+" ";
        }
        sat=sat+"0\n";
        for(int i=3;i<=nodeCount*3;i+=3){
            sat=sat+i+" ";
        }
        sat=sat+"0\n";
        count+=3;
        fline=fline+" "+count+"\n";
        try{
            BufferedWriter bw=new BufferedWriter(new FileWriter("expr.cnf"));
            bw.write(fline);
            bw.write(sat);
            bw.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        Runtime rt=Runtime.getRuntime();
        String[] commands = {"/bin/sh","-c","/home/andreas/Downloads/lingeling_solver/lingeling expr.cnf | grep 'v 1 \\|v -1'"};
        Process proc = rt.exec(commands);

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));
        String s = stdInput.readLine();
        int lit[]=new int[literals];
        if(s==null)
            System.out.print("The given graph cannot be devided into 3 sets");
        else{
            System.out.println("The three (3) set that can be generated based on the matrix above are:");
            String sets[]=new String [3];
            sets[0]="Set 1 ={ ";
            sets[1]="Set 2 ={ ";
            sets[2]="Set 3 ={ ";
            int countL=0;
            int var=0;
            int i=0;
            char res[]=s.toCharArray();
            while((i<res.length)&&(countL<nodeCount)){
               if(res[i]=='-') {
                   var++;
                   i++;
                   while(Character.isDigit(res[i]))
                       i++;
                   i--;
               }else if(Character.isDigit(res[i])) {
                   sets[var]=sets[var]+" "+(countL+1)+", ";
                   var++;
                   i++;
                   while(Character.isDigit(res[i]))
                       i++;
                   i--;
               }
               if(var==3){
                   var=0;
                   countL++;
               }
               i++;
            }
            sets[0]=sets[0]+"}";
            sets[1]=sets[1]+"}";
            sets[2]=sets[2]+"}";
            System.out.println(sets[0]);
            System.out.println(sets[1]);
            System.out.println(sets[2]);
        }
    }
}
