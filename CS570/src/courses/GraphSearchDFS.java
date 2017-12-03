package courses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class GraphSearchDFS {
    static List<List<Integer>> graph = new ArrayList<>();
            
    static void DFS(int start) {
        List<Integer> res = new ArrayList<>();
        
        Stack<Integer> S = new Stack<>();
        S.push(start);
        //res.add(start);
        
        while (!S.isEmpty()) {
            int curr = S.pop();
            if (!res.contains(curr)) res.add(curr);
            for (int i =  graph.get(curr).size() - 1; i >= 0; i--) {
                int adj = graph.get(curr).get(i);
                if (res.contains(adj)) continue;
                int count = 0;
                for (int nextEdge : graph.get(adj)) {
                    if (!res.contains(nextEdge)) {
                        count++;
                    }
                }
                if (count > 0) {
                    S.push(adj);
                } else if (!res.contains(adj)) {
                    S.push(adj);
                }
            }
        }

        System.out.println(res);
    }

    public static void main(String[] args) {
        graph.add(Arrays.asList(new Integer[] {1, 2, 3}));
        graph.add(Arrays.asList(new Integer[] {0, 2}));
        graph.add(Arrays.asList(new Integer[] {0, 1, 4}));
        graph.add(Arrays.asList(new Integer[] {0}));
        graph.add(Arrays.asList(new Integer[] {2}));
        
        DFS(0);
    }
}
