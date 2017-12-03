import java.io.*;
import java.util.Scanner;

class Node {
    int val;
    Node left;
    Node right;
    char lable;
    public Node (int v) {
        val = v;
        left = null;
        right = null;
        lable = '-';
    }
}

public class BinaryTree {
    

    public Node root;
    public BinaryTree () {
        root = null;
    }
    public BinaryTree(Node rt) {
        this.root = rt;
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
}