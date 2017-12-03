import java.util.*;
class Pair {
    char process;
    int memory;
    public Pair(char process, int memory) {
        this.process = process;
        this.memory = memory;
    }
}
class AddressAndSize {
    char process;
    int size;
    int address;
    public AddressAndSize(char process, int s, int a) {
        this.process = process;
        size = s;
        address = a;
    }
}

public class BuddySystem {
    static List<List<Integer>> availableLists;
    static List<Pair> sequenceOfIntegers;
    static Map<Character, List<Integer>> allocated;
    static int exponent;
    
    //Read in the two input data: total memory size & sequence of integers
    public static void initiate() {
        availableLists = new ArrayList<>();
        sequenceOfIntegers = new ArrayList<>();
        allocated = new HashMap<>();
        System.out.println("Input the exponent value 'n', the total available memory would be (2 ^ n) * KB");
        Scanner sc = new Scanner(System.in);
        exponent = Integer.valueOf(sc.nextLine());
        for (int i = 0; i < exponent + 1; i++) {
            availableLists.add(new ArrayList<Integer>());
        }
        availableLists.get(exponent).add(0);
        System.out.println("Please input the sequence of integers, allocating begins with plus symbol (or nothing), returning begins with minus symbol. \nSeparate each by a space. Don't input 'K' after the integer");
        System.out.println("For e.g., if you want to input the example proposed in slides, you may type in the following:\n\"A 20 B 35 C 90 D 40 E 240 D -40 A -20 C -90 B -35 E -240\"");
        String requests = sc.nextLine();
        sc = new Scanner(requests);
        while (sc.hasNext()) {
            Pair currRequest = new Pair(sc.next().charAt(0), sc.nextInt());
            sequenceOfIntegers.add(currRequest);
        }
    }
    
    public static void proceed(Pair request) {  //request = {size, location}
        //Allocating operation
        if (request.memory > 0) {
            int r = (int) Math.ceil(Math.log((double) request.memory) / Math.log((double) 2));
            int index = r;
            while (index < exponent && availableLists.get(index).isEmpty()) {
                index++;
            }
            while (index > r) {
                //TODO: What if there is no available hole left
                //System.out.println(index);
                breakInHalf(index, availableLists.get(index).get(0));
                index--;
            }
            //Allocate a block in n = index list to this process
            //Size and location: e.g. 2 4
            List<Integer> sizeLocation = new ArrayList<>();
            sizeLocation.add(index);    //Size
            sizeLocation.add(availableLists.get(index).get(0));     //The address
            //Remove this block from the available list
            availableLists.get(index).remove(0);
            allocated.put(request.process, sizeLocation);
        }
        //Returning operation
        else if (request.memory < 0) {
            List<Integer> sizeLocation = allocated.remove(request.process);
            int size = sizeLocation.get(0), location = sizeLocation.get(1);
            availableLists.get(size).add(location);
            
            boolean fragmented;
            do {
                fragmented = false;
                Collections.sort(availableLists.get(size));
                for (int add : availableLists.get(size)) {
                    boolean proper = (Math.min(add, location) / power(2, size)) % 2 == 0;
                    if (Math.abs(add - location) == power(2, size) && proper) {
                        //TODO: Consolidate only in proper pair
                        location = consolidate(size, add, location);
                        size++;
                        fragmented = true;
                        break;
                    }
                }
            } while(size < exponent && fragmented);
        }
    }
    
    private static void breakInHalf(int index, int address) {
        availableLists.get(index).remove((Integer) address);
        availableLists.get(index - 1).add(address);
        availableLists.get(index - 1).add(address + power(2, index - 1));
        Collections.sort(availableLists.get(index - 1));
    }
    
    private static int consolidate(int size, int add1, int add2) {
        availableLists.get(size).remove((Integer) add1);
        availableLists.get(size).remove((Integer) add2);
        availableLists.get(size + 1).add(Math.min(add1, add2));
        Collections.sort(availableLists.get(size + 1));
        return Math.min(add1, add2);
    }
    
    public static void print() {
        PriorityQueue<AddressAndSize> myMemory = new PriorityQueue<>((ele1, ele2) -> ele1.address - ele2.address);
        for (int size = 0; size < availableLists.size(); size++) {
            for (int add : availableLists.get(size)) {
                myMemory.offer(new AddressAndSize(' ', size, add));
            }
        }
        for (char process : allocated.keySet()) {
            List<Integer> sizeLocation = allocated.get(process);
            int size = sizeLocation.get(0), location = sizeLocation.get(1);
            myMemory.offer(new AddressAndSize(process, size, location));
        }
        while (!myMemory.isEmpty()) {
            AddressAndSize curr = myMemory.poll();
            int capacity = power(2, curr.size);
            System.out.print("" + curr.process + capacity + "K" + "\t");
        }
        System.out.println();
    }
    
    private static int power(int base, int n) {
        return (int) Math.pow(base, (double) n);
    }

    public static void main(String[] args) {
        //Test case:
        //Exponent n : 20 (which means 2^20 KB)
        //A 20 B 35 C 90 D 40 E 240 D -40 A -20 C -90 B -35 E -240
        initiate();
        System.out.print("The initial memory block is: ");
        print();
        for (Pair request : sequenceOfIntegers) {
            String op = request.memory > 0 ? "+" : "-";
            System.out.print(request.process + ": " + op + Math.abs(request.memory) + "KB:  |\t");
            proceed(request);
            print();
        }
    }
}
