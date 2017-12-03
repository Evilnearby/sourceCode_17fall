package courses;

import java.util.*;

class Edge {
    int vertex1;
    int vertex2;
    int cost;

    public Edge(int vertex1, int vertex2, int cost) {
        this.vertex1 = vertex1;
        this.vertex2 = vertex2;
        this.cost = cost;
    }
}

public class DijkstraAlgorithm {
    
    static List<Edge> edges = new ArrayList<Edge>();
    
    //The algorithm works by maintaining a set S of selected nodes whose shortest distance
    //from the source is already known.
    static void dijkstra(int source) {
        Set<Integer> S = new HashSet<>();
        Set<Integer> V_S = new HashSet<>();
        Map<Integer, Integer> D = new HashMap<>();
        
        S.add(source);
        
        for (Edge e : edges) {
            V_S.add(e.vertex1);
            V_S.add(e.vertex2);
        }
        V_S.remove(source);
        
        for (Integer n : V_S) {
            D.put(n, getCost(source, n));
        }
        
        while (!V_S.isEmpty()) {
            int min = Collections.min(V_S, (Integer i1, Integer i2) -> D.get(i1) - D.get(i2));
            //System.out.println(min);
            S.add(min);
            V_S.remove(min);
            for (Integer n : V_S) {
                D.put(n, Math.min(D.get(n), D.get(min) + getCost(min, n)));
            }
        }
        
        print(D);
    }
    
    private static int getCost(int start, int end) {
        for (Edge e : edges) {
            if (e.vertex1 == start && e.vertex2 == end) {
                return e.cost;
            }
        }
        return 99999999;
    }
    
    static void print(Map<Integer, Integer> D) {
        for (int n : D.keySet()) {
            System.out.println(n + " " + D.get(n));
        }
    }

    public static void main(String[] args) {
        edges.add(new Edge(1, 2, 10));
        edges.add(new Edge(1, 5, 100));
        edges.add(new Edge(1, 4, 30));
        edges.add(new Edge(2, 3, 50));
        edges.add(new Edge(3, 5, 10));
        edges.add(new Edge(4, 3, 20));
        edges.add(new Edge(4, 5, 60));
        
        dijkstra(1);
    }
}
