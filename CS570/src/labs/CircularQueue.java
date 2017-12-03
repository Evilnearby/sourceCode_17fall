package labs;

import java.util.Scanner;

class ListNode {
    public String val;
    public ListNode next;
    public ListNode(String str) {
        val = str;
        next = null;
    }
}

public class CircularQueue {
    ListNode dummy = null; //dummy.next = head;
    ListNode currPointer = null;

    int size;
    public CircularQueue() {
        dummy = new ListNode("");
        size = 0;
    }

    public void offer(String str) {
        if(size == 0) {
            currPointer = new ListNode(str);
            dummy.next = currPointer;
            currPointer.next = dummy.next;
            size++;
        } else if (size < 12){
            ListNode curr = new ListNode(str);
            currPointer.next = curr;
            currPointer = currPointer.next;
            currPointer.next = dummy.next;
            size++;
        } else {
            //prevP -> newNode -> currPointer
            //newNode = currPointer
            currPointer = currPointer.next;
            currPointer.val = str;
            //currPointer = currPointer.next;
        }
    }
    
    public String poll() {
        String currStr = dummy.next.val;
        dummy = dummy.next;
        size--;
        return currStr;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        CircularQueue cQueue = new CircularQueue();
        do {
            System.out.println("Please input the string you want to enqueue, type quit to quit and print.");
            String in = sc.nextLine();
            if(in.equals("quit")) {
                break;
            } else {
                cQueue.offer((in));
            }
        }while(true);
        
        while(cQueue.size > 0) {
            System.out.println(cQueue.poll());
        }
    }
}
