package Starter;

import Game.Grid;
import Solver.DetSolve;

import java.math.BigInteger;
import java.util.ArrayList;

public class Util {


    public static int RGBDistance(int[] a, int[] b){
        int d = 0;
        for(int i = 0; i < 3; i++){
            d += (a[i] - b[i])*(a[i] - b[i]);
        }
        return d; // gibt das Quadrat zurück
    }

    public static void test(int num){
        Grid g;
        int won = 0;
        for(int i = 0; i < num; i++){
            g = new Grid('h');
            DetSolve s = new DetSolve(g);
            if(s.start()){
                won++;
            }
            if(i % (num / 100) == 0){
                System.out.println(100 * i / num + "% Done");
            }
            //System.out.println("Finished:" + (i+1));
        }
        System.out.println("Played:\t" + num + ",\tWon:\t" + won + ",\t%:\t" + ((100d * won) / num) );
    }

    public static String toNum(int n, int space) {
        int numspace;
        if (n == 0) {
            numspace = 1;
        } else {
            numspace = (int) (Math.floor(Math.log10(n))) + 1;
        }
        String s = Integer.toString(n);
        for (int i = 0; i < space - numspace; i++) {
            s = " " + s;
        }
        return s;
    }

    public static void printSolutions(ArrayList<BigInteger> arr){
        BigInteger and = new BigInteger("0");
        BigInteger or = new BigInteger("0");
        for(int i = 0; i < arr.size(); i++){
            and = and.and(arr.get(i));
            or = or.or(arr.get(i));
            System.out.println(i + ":" + arr.get(i).toString(2));
        }
        System.out.println("AND:" + and.toString(2));
        System.out.println("OR:" + or.toString(2));
    }

    public static void printEqns(int[][] m, int[] b) {
        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < m[0].length; j++) {
                if (m[i][j] == 1) {
                    System.out.print(" + x_" + toNum(j, 2));
                } else {
                    System.out.print("       ");
                }
            }
            System.out.print(" = " + b[i] + "\n");
        }
    }

    public static void printArr(int[] arr) {
        for(int i = 0; i < arr.length; i++){
            if(i == arr.length - 1){
                System.out.print(arr[i] + "\n");
            } else {
                System.out.print(arr[i] + ", ");
            }
        }
    }

    public static void printMatrix(int[][] a) {
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                System.out.print(a[i][j] + " ");
            }
            System.out.print("\n");
        }
    }

    public static void toRowEchelon(int[][] oldMatrix, int[] b) {
        // convert to one matrix
        int[][] matrix = new int[oldMatrix.length][oldMatrix[0].length + 1];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (j == matrix[0].length - 1) {
                    matrix[i][j] = b[i];
                } else {
                    matrix[i][j] = oldMatrix[i][j];
                }
            }
        }
        int m = matrix.length;
        int n = matrix[0].length;
        int h = 0;
        int k = 0;
        int imax;
        int f;
        int[] temp;
        while (h < m && k < n) {
            // find i-max
            imax = 0;
            for (int i = h; i < m; i++) {
                if (matrix[i][k] == 1) {
                    imax = i;
                    break;
                }
            }
            if (matrix[imax][k] != 0) {
                temp = matrix[imax];
                matrix[imax] = matrix[h];
                matrix[h] = temp;
                for (int i = h + 1; i < m; i++) {
                    f = matrix[i][k] / matrix[h][k];
                    matrix[i][k] = 0;
                    for (int j = k + 1; j < n; j++) {
                        matrix[i][j] = matrix[i][j] - matrix[h][j] * f;
                    }
                }
                h++;
            }
            k++;

        }
        printMatrix(matrix);
        // recover
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (j == matrix[0].length - 1) {
                    b[i] = matrix[i][j];
                } else {
                    oldMatrix[i][j] = matrix[i][j];
                }
            }
        }
    }
}
