package labs;

import java.io.*;
import java.util.*;

public class TopologicalSort {
    static Map<Integer, Integer> indegrees;
    static Map<Integer, List<Integer>> descendents;
    static List<Integer> nodes;
    
    public static void readInput() throws IOException {
        indegrees = new HashMap<>();
        descendents = new HashMap<>();
        nodes = new ArrayList<>();
        
        BufferedReader bd = new BufferedReader(new InputStreamReader(new FileInputStream("infile.dat")));
        String line;
        while ((line = bd.readLine()) != null) {
            //Line example : 0 1
            //Indicates 0 is 1's pre-requisite
            Scanner sc = new Scanner(line);
            int a = sc.nextInt();
            int b = sc.nextInt();
            if (!nodes.contains(a)) nodes.add(a);
            if (!nodes.contains(b)) nodes.add(b);
            
            indegrees.putIfAbsent(a, 0);
            indegrees.putIfAbsent(b, 0);
            descendents.putIfAbsent(a, new ArrayList<Integer>());
            descendents.putIfAbsent(b, new ArrayList<Integer>());
            indegrees.put(b, indegrees.get(b) + 1);
            descendents.get(a).add(b);
        }
    }
    
    public static void topoSortI() {
        List<Integer> ans = new ArrayList<>();
        Queue<Integer> queue = new LinkedList<>();
        for (int node : nodes) {
            if (indegrees.get(node) == 0) {
                queue.offer(node);
            }
        }
        while (!queue.isEmpty()) {
            int curr = queue.poll();
            ans.add(curr);
            for (int descendent : descendents.get(curr)) {
                indegrees.put(descendent, indegrees.get(descendent) - 1);
                if (indegrees.get(descendent) == 0) {
                    queue.offer(descendent);
                }
            }
        }
        
        System.out.println("The first ordering is: ");
        for (int num : ans) {
            System.out.print(num);
            System.out.print(" ");
        }
        System.out.println();
    }
    
    //Second ordering by tweaking one id(0) node
    public static void topoSortII() {
        boolean isTweaked = false;
        List<Integer> ans = new ArrayList<>();
        Queue<Integer> queue = new LinkedList<>();
        for (int node : nodes) {
            if (indegrees.get(node) == 0) {
                queue.offer(node);
            }
        }
        if (queue.size() > 1) {
            int tmp = queue.poll();
            queue.offer(tmp);
            isTweaked = true;
        }
        while (!queue.isEmpty()) {
            int curr = queue.poll();
            ans.add(curr);
            for (int num : ans) {
                System.out.print(num);
                System.out.print(" ");
            }
            System.out.println();
            if (isTweaked) {
                for (int descendent : descendents.get(curr)) {
                    indegrees.put(descendent, indegrees.get(descendent) - 1);
                    if (indegrees.get(descendent) == 0) {
                        queue.offer(descendent);
                    }
                }
            } else {
                List<Integer> candidates = new ArrayList<>();
                for (int descendent : descendents.get(curr)) {
                    indegrees.put(descendent, indegrees.get(descendent) - 1);
                    if (indegrees.get(descendent) == 0) {
                        candidates.add(descendent);
                    }
                }
                if (candidates.size() > 1) {
                    for (int i = candidates.size() - 1;i >=0 ;i--) {
                        queue.offer(candidates.get(i));
                    }
                    isTweaked = true;
                } else {
                    queue.offer(candidates.get(0));
                }
            }
        }

        System.out.println("The second ordering is: ");
        for (int num : ans) {
            System.out.print(num);
            System.out.print(" ");
        }
        System.out.println();
    }

    public static void main(String[] args) throws IOException {
        readInput();
        topoSortI();
        readInput();
        topoSortII();
    }
}
