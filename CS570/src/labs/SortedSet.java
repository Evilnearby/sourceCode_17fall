package labs;

import java.io.*;
import java.util.Scanner;

public class SortedSet {
    class Node {
        int val;
        Node left;
        Node right;
        public Node (int v) {
            val = v;
            left = null;
            right = null;
        }
    }

    public Node root;
    public SortedSet () {
        root = null;
    }
    
    public boolean isEmpty() {
        if (this.root == null) {
            return true;
        }else {
            return false;
        }
    }

    /**
     * 
     * @param v - the value of node to be inserted
     * @return false if a node with this value already present; otherwise true
     */
    public boolean add(int v) {
        Node newNode = new Node(v);
        if (this.root == null) {
            root = newNode;
            return true;
        }
        Node curr = this.root;
        Node parent = null;
        while (true) {
            parent = curr;
            if (v < parent.val) {
                curr = parent.left;
                if (curr == null) {
                    parent.left = newNode;
                    return true;
                }
            } else if (v > parent.val) {
                curr = parent.right;
                if (curr == null) {
                    parent.right = newNode;
                    return true;
                }
            } else {
                return false;
            }
        }
    }

    public boolean remove (int v) {
        Node parent = root;
        Node curr = root;
        boolean isLeftChild = false;
        while (curr.val != v) {
            parent = curr;
            if (v < curr.val) {
                curr = curr.left;
                isLeftChild = true;
            } else if (v > curr.val){
                isLeftChild = false;
                curr = curr.right;
            }
            if (curr == null) {
                return false;
            }
        }
        //Case 1, deleted has no child
        if (curr.left == null && curr.right == null) {
            if (curr == root) {
                root = null;
                return true;
            }
            if (isLeftChild) {
                parent.left = null;
            } else {
                parent.right = null;
            }
        }
        //Case 2, deleted has one child
        else if (curr.right == null) {
            if (curr == root) {
                root = curr.left;
            } else if (isLeftChild) {
                parent.left = curr.left;
            } else {
                parent.right = curr.left;
            }
        } else if (curr.left == null) {
            if (curr == root) {
                root = curr.right;
            } else if (isLeftChild) {
                parent.left = curr.right;
            } else {
                parent.right = curr.right;
            }
        }
        //Case 3, deleted has 2 children
        else if (curr.left != null && curr.right != null) {
            Node prede = this.findPredecessor(curr);
            if (curr == root) root = prede;
            else if (isLeftChild) {
                parent.left = prede;
            } else {
                parent.right = prede;
            }
            prede.right = curr.right;
        }
        return true;
    }
    
    private Node findPredecessor(Node deleted) {
        Node curr = deleted.left;
        Node next = curr.right;
        Node prev = null;
        while (next != null) {
            prev = curr;
            curr = next;
            next = next.right;
        }
        if (curr != deleted.left) {
            prev.right = curr.left;
            curr.left = deleted.left;
        }
        return curr;
    }

    /**
     * 
     * @param v - the value of node to find
     * @return true if found; otherwise flase
     */
    public boolean contains (int v) {
        Node curr = this.root;
        while (curr != null) {
            if (v < curr.val) {
                curr = curr.left;
            } else if (v > curr.val) {
                curr = curr.right;
            } else {
                System.out.println("Yes");
                return true;
            }
        }
        System.out.println("No");
        return false;
    }

    public static void main(String[] args) throws IOException {
        //Input
        BufferedReader d = new BufferedReader(new InputStreamReader(new FileInputStream("infile.dat")));
        SortedSet bst = new SortedSet();
        String str = d.readLine();

        String[] array = str.split(", ");
        for (String number:array) {
            bst.add(Integer.valueOf(number));
        }
        //bst.remove(-99);
        System.out.println("Please input a Integer to search in the set");
        Scanner sc = new Scanner(System.in);
        int inp = sc.nextInt();
        bst.contains(inp);
    }
}
