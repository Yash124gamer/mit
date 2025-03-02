package core;
import java.io.*;
import java.util.*;

public class FileDiff {

    // variable used to store changes in each line of two files
    private List<Character> stack = new ArrayList<>();

    // Function that will return the data of a given file as List of lines
    public List<String> readFile(String filename){
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }catch (Exception e) {
            throw new RuntimeException("Error Reading data to file",e);
        }
        return lines;
    }
    // Calculating shortest edit path between 2 lines using Myer's Algorithm
    public List<int[]> shortestLineEdit(List<String> a, List<String> b) {
        int n = a.size();
        int m = b.size();
        int max = n + m;
        int[] v = new int[max * 2 + 1];
        v[max] = 0;
        List<int[]> trace = new ArrayList<>();

        for (int d = 0; d <= max; d++) {
            for (int k = -d; k <= d; k += 2) {
                int shiftedIndex = k + max;
                int x;
                if (k == -d || (k != d && v[shiftedIndex - 1] < v[shiftedIndex + 1])) {
                    x = v[shiftedIndex + 1];
                } else {
                    x = v[shiftedIndex - 1] + 1;
                }
                int y = x - k;

                while (x < n && y < m && a.get(x).equals(b.get(y))) {
                    x++;
                    y++;
                }
                v[shiftedIndex] = x;

                if (x >= n && y >= m) {
                    trace.add(v.clone());
                    return trace.subList(0, d + 1);
                }
            }
            trace.add(v.clone());
        }
        return trace;
    }
    @FunctionalInterface
    public interface EditCallback {
        void apply(int prev_x, int prev_y, int x, int y);
    }
    // Function that backtracks the sgortest edit and finds the path from starting 
    public void backtrack(List<int[]> shortestEdit, int n, int m, EditCallback  callback) {
        int x = n, y = m;
        int max = n + m;

        for (int d = shortestEdit.size() - 1; d >= 0; d--) {
            int[] v = shortestEdit.get(d);
            int k = x - y;
            int shiftedIndex = k + max;
            int prev_k;
            if (k == -d || (k != d && v[shiftedIndex - 1] < v[shiftedIndex + 1])) {
                prev_k = k + 1;
            } else {
                prev_k = k - 1;
            }
            int prev_x = v[prev_k + max];
            int prev_y = prev_x - prev_k;

            while (x > prev_x && y > prev_y) {
                callback.apply(x - 1, y - 1, x, y);
                x--;
                y--;
            }
            if (d > 0) {
                callback.apply(prev_x, prev_y, x, y);
            }
            x = prev_x;
            y = prev_y;
        }
    }
    // Callback function that iss used to store insertion or deletion of a line during backtracking 
    public void changes(int prev_x, int prev_y, int x, int y) {
        if (x > prev_x && y > prev_y) {
            stack.add('c');
        } else if (x > prev_x) {
            stack.add('-');
        } else {
            stack.add('+');
        }
    }
    // Funtion that will print the differences between two file content
    public void print(String fileName, List<String> n, List<String> m) {
        System.out.println("File: " + fileName);
        int a = 0, b = 0, lineNumber = 1;
        System.out.println("------------------------------------");
        for (int i = stack.size() - 1; i >= 0; i--) {
            char changeType = stack.get(i);
            switch (changeType) {
                case 'c': // No change
                    System.out.printf("%-4d   %s%n", lineNumber, n.get(a));
                    a++;
                    b++;
                    lineNumber++;
                    break;
                case '-': // Deletion (Red)
                    System.out.printf("\033[31m%-4d - %s\033[0m%n", (a + 1), n.get(a));
                    a++;
                    break;
                case '+': // Addition (Green)
                    System.out.printf("\033[32m%-4d + %s\033[0m%n", lineNumber, m.get(b));
                    b++;
                    lineNumber++;
                    break;
            }
        }
        System.out.println("------------------------------------");
    }     
}