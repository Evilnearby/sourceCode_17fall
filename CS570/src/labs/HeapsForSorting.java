package labs;

import java.util.*;

public class HeapsForSorting {
    List<Integer> heap;
    int size;
    public HeapsForSorting() {
        heap = new ArrayList<>();
        size = 0;
    }
    
    public void offer(int v) {
        heap.add(v);
        size++;
        int index = size - 1;
        while (index != 0 && heap.get(index) > heap.get((index - 1) / 2)) {
            swap(index, (index - 1) / 2);
            index = (index - 1) / 2;
        }
    }
    public int poll() {
        int first = heap.get(0);
        int last = heap.get(size - 1);
        heap.set(0, last);
        heap.remove(heap.size() - 1);
        size--;
        int index = 0;
        while (index * 2 + 1 < size) {  //while has child
            int numOfNodes = 2;
            int leftInd = 2 * index + 1;
            int rightInd = 2 * index + 2;
            if (rightInd < size) numOfNodes++;
            int[] candidates = new int[] {index, leftInd, rightInd};
            Integer[] indices = new Integer[numOfNodes];
            for (int i = 0; i < numOfNodes; i++) {
                indices[i] = candidates[i];
            }
            Arrays.sort(indices, (Integer i1, Integer i2) -> heap.get(i2) - heap.get(i1));
            if (indices[0] == index) return first;
            swap(index, indices[0]);
            index = indices[0];
        }
        return first;
    }
    
    private void swap(int index1, int index2) {
        int temp = heap.get(index1);
        heap.set(index1, heap.get(index2));
        heap.set(index2, temp);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        HeapsForSorting maxheap = new HeapsForSorting();
        System.out.println("You should enter 10 numbers into a sorting heap");
        for (int i = 0; i < 10; i++) {
            System.out.println("Please type in next number, " + (10 - i) + " numbers are left");
            int curr = Integer.parseInt(sc.next());
            maxheap.offer(curr);
        }
        System.out.println("\nBegin polling: \n");
        for (int i = 0; i < 10; i++) {
            System.out.println(maxheap.poll());
        }
    }
    
}
