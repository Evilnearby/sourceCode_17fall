package courses;
class ListNode {
    int val;
    ListNode next;
    public ListNode(int v) {
        val = v;
        next = null;
    }
}
public class MyLinkedList {
    ListNode dummy;
    public MyLinkedList() {
        dummy = new ListNode(0);
    }
    
    public ListNode Head() {
        return this.dummy.next;
    }
    public void add(int value) {
        ListNode curr = dummy;
        while(curr.next != null) {
            curr = curr.next;
        }
        curr.next = new ListNode(value);
    }
    
    public boolean remove(int index) {
        ListNode curr = dummy;
        int len = 0;
        while (curr.next != null) {
            curr = curr.next;
            len++;
        }
        if(index >= len || index <= 0) {
            return false;
        }
        int i = 0;
        curr = dummy;
        while( i < index ) {
            curr = curr.next;
            i++;
        }
        //System.out.println("Curr:" + curr.val);
        curr.next = curr.next.next;
        return true;
    }
    
    public boolean insertAt(int index, int value) {
        ListNode curr = dummy;
        int len = 0;
        while (curr.next != null) {
            curr = curr.next;
            len++;
        }
        if(index >= len || index <= 0) {
            return false;
        }
        int i = 0;
        curr = dummy;
        while( i < index) {
            curr = curr.next;
            i++;
        }
        ListNode newN = new ListNode(value);
        newN.next = curr.next;
        curr.next = newN;
        return true;
    }

    public static void main(String[] args) {
        MyLinkedList list = new MyLinkedList();
        for(int i = 0; i < 4; i++) {
            list.add(i);
        }
        list.remove(1);
        list.insertAt(1,100);
        ListNode hd = list.Head();
        System.out.println(hd.val);
        System.out.println(hd.next.val);
        System.out.println(hd.next.next.val);
    }
}
