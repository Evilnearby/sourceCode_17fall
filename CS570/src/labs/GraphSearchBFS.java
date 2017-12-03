package labs;

import java.io.*;
import java.util.*;

public class GraphSearchBFS {
    static int[][] matrix = null;
    static int n;
    
    //Input format: Node's index is by default from 0 to n -1
    //Note that ordinarily we should have an undirected graph
    static void readInput() throws IOException {
        BufferedReader bd = new BufferedReader(new InputStreamReader(new FileInputStream("infile.dat")));
        String line;
        int lineNum = 0;
        while ((line = bd.readLine()) != null) {
            String[] numbers = line.split(" ");
            if (matrix == null) {
                n = numbers.length;
                matrix = new int[n][n];
            }
            for (int i = 0; i < n; i++) {
                matrix[lineNum][i] = Integer.parseInt(numbers[i]);
            }
            lineNum++;
        }
    }

    static Map<Integer, Integer> BFS(int start) {
        Map<Integer, Integer> bfn = new HashMap<>();
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(start);
        bfn.put(start, 0);
        int index = 1;
        while (!queue.isEmpty()) {
            int curr = queue.poll();
            for (int i = 0; i < n; i++) {
                if (matrix[curr][i] == 1) {
                    if (!bfn.containsKey(i)) {
                        queue.offer(i);
                        bfn.put(i, index++);
                    }
                }
            }
        }
        return bfn;
    }

    public static void main(String[] args) throws IOException {
        readInput();
        Map<Integer, Integer> res = BFS(0);
        for (int key : res.keySet()) {
            System.out.println(key + " " + res.get(key));
        }
    }
}
