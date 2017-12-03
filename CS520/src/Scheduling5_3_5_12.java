import java.util.HashMap;
import java.util.*;

class Event {
    String type;
    int process;
    float time;
    float burst;
    //type could be string of "arrival" and "completion"
    //Constructor for arrival event
    public Event (String type, int process, float time, float burst) {
        this.type = type;
        this.process = process;
        this.time = time;
        this.burst = burst;
    }
    //Constructor for completion event
    public Event (String type, int process, float time) {
        this.type = type;
        this.process = process;
        this.time = time;
        this.burst = 0;
    }
}
class Process {
    int index;
    boolean isRunning;
    float startTime;
    float burstTime;
    float completionTime;
    float lastTimeEnteringReadyQueue;
    float remaining;
    float waitingTime;
    public Process(int index, boolean isRunning,float startTime, float burstTime) {
        this.index = index;
        this.isRunning = isRunning;
        this.startTime = startTime;
        this.burstTime = burstTime;
        remaining = burstTime;
        lastTimeEnteringReadyQueue = 0f;
        waitingTime = 0f;
    }
}
class CheckPoint {
    int processIndex;
    float time;
    String comment;
    public CheckPoint(int process, float time, String comment) {
        this.processIndex = process;
        this.time = time;
        this.comment = comment;
    }
}

public class Scheduling5_3_5_12 {
    static LinkedList<Event> eventList = new LinkedList<>();
    static LinkedList<Integer> readyQueue = new LinkedList<>();
    static Map<Integer, Process> PCB = new HashMap<>();
    
    static List<CheckPoint> CPURecords = new ArrayList<>();
    
    static float clock = 0;
    static boolean isCPUFree = true;
    
    public static void FCFS() {
        while (!eventList.isEmpty()) {
            Event curr = eventList.poll();
            clock = curr.time;
            //System.out.println(curr.process + curr.type + " " + clock);
            switch (curr.type) {
                case "arrival":
                    PCB.putIfAbsent(curr.process, new Process(curr.process, false, curr.time, curr.burst));
                    if (isCPUFree) {
                        CPURecords.add(new CheckPoint(curr.process, clock, "starts"));
                        isCPUFree = false;
                        PCB.get(curr.process).isRunning = true;
                        PCB.get(curr.process).waitingTime += clock - PCB.get(curr.process).lastTimeEnteringReadyQueue;
                        addToWaitList(new Event("completion", curr.process, clock + PCB.get(curr.process).burstTime));
                    } else {
                        readyQueue.offer(curr.process);
                        PCB.get(curr.process).lastTimeEnteringReadyQueue = clock;
                    }
                    break;
                case "completion":
                    PCB.get(curr.process).completionTime = clock;
                    PCB.get(curr.process).remaining = 0f;
                    CPURecords.add(new CheckPoint(curr.process, clock, "ends"));
                    if (!readyQueue.isEmpty()) {
                        int nextP = readyQueue.poll();
                        PCB.get(nextP).isRunning = true;
                        PCB.get(nextP).waitingTime += clock - PCB.get(nextP).lastTimeEnteringReadyQueue;
                        CPURecords.add(new CheckPoint(nextP, clock, "starts"));
                        addToWaitList(new Event("completion", nextP, clock + PCB.get(nextP).burstTime));
                    } else {
                        isCPUFree = true;
                    }
                    break;
                default:
                    System.out.println("Wrong Event Exception");
                    break;
            }
        }
    }

    public static void SJF() {
        while (!eventList.isEmpty()) {
            Event curr = eventList.poll();
            clock = curr.time;
            switch (curr.type) {
                case "arrival":
                    PCB.putIfAbsent(curr.process, new Process(curr.process, false, curr.time, curr.burst));
                    if (isCPUFree) {
                        CPURecords.add(new CheckPoint(curr.process, clock, "starts"));
                        isCPUFree = false;
                        PCB.get(curr.process).isRunning = true;
                        PCB.get(curr.process).waitingTime += clock - PCB.get(curr.process).lastTimeEnteringReadyQueue;
                        float completionTime = clock + PCB.get(curr.process).burstTime;
                        addToWaitList(new Event("completion", curr.process, completionTime));
                    } else {
                        float completionTime = clock + PCB.get(curr.process).burstTime;
                        PCB.get(curr.process).completionTime = completionTime;
                        int index = 0;
                        for (int readyProcess : readyQueue) {
                            if(PCB.get(readyProcess).completionTime <= completionTime) index++;
                            else break;
                        }
                        readyQueue.add(index, curr.process);
                        PCB.get(curr.process).lastTimeEnteringReadyQueue = clock;
                    }
                    break;
                case "completion":
                    PCB.get(curr.process).completionTime = clock;
                    PCB.get(curr.process).remaining = 0f;
                    CPURecords.add(new CheckPoint(curr.process, clock, "ends"));
                    if (!readyQueue.isEmpty()) {
                        int nextP = readyQueue.poll();
                        PCB.get(nextP).isRunning = true;
                        PCB.get(nextP).waitingTime += clock - PCB.get(nextP).lastTimeEnteringReadyQueue;
                        CPURecords.add(new CheckPoint(nextP, clock, "starts"));
                        float completionTime = clock + PCB.get(nextP).burstTime;
                        addToWaitList(new Event("completion", nextP, completionTime));
                    } else {
                        isCPUFree = true;
                    }
                    break;
                default:
                    System.out.println("Wrong Event Exception");
                    break;
            }
        }
    }
    
    public static void nonpreemptivePriority(Map<Integer, Integer> priorities) {
        while (!eventList.isEmpty()) {
            Event curr = eventList.poll();
            clock = curr.time;
            switch (curr.type) {
                case "arrival":
                    PCB.putIfAbsent(curr.process, new Process(curr.process, false, curr.time, curr.burst));
                    if (isCPUFree) {
                        CPURecords.add(new CheckPoint(curr.process, clock, "starts"));
                        isCPUFree = false;
                        PCB.get(curr.process).isRunning = true;
                        PCB.get(curr.process).waitingTime += clock - PCB.get(curr.process).lastTimeEnteringReadyQueue;
                        float completionTime = clock + PCB.get(curr.process).burstTime;
                        addToWaitList(new Event("completion", curr.process, completionTime));
                    } else {
                        float completionTime = clock + PCB.get(curr.process).burstTime;
                        PCB.get(curr.process).completionTime = completionTime;
                        int index = 0;
                        for (int readyProcess : readyQueue) {
                            if(priorities.get(readyProcess) <= priorities.get(curr.process)) index++;
                            else break;
                        }
                        readyQueue.add(index, curr.process);
                        PCB.get(curr.process).lastTimeEnteringReadyQueue = clock;
                    }
                    break;
                case "completion":
                    PCB.get(curr.process).completionTime = clock;
                    PCB.get(curr.process).remaining = 0f;
                    CPURecords.add(new CheckPoint(curr.process, clock, "ends"));
                    if (!readyQueue.isEmpty()) {
                        int nextP = readyQueue.poll();
                        PCB.get(nextP).isRunning = true;
                        PCB.get(nextP).waitingTime += clock - PCB.get(nextP).lastTimeEnteringReadyQueue;
                        CPURecords.add(new CheckPoint(nextP, clock, "starts"));
                        float completionTime = clock + PCB.get(nextP).burstTime;
                        addToWaitList(new Event("completion", nextP, completionTime));
                    } else {
                        isCPUFree = true;
                    }
                    break;
                default:
                    System.out.println("Wrong Event Exception");
                    break;
            }
        }
    }
    
    public static void RR(float quantum) {
        while (!eventList.isEmpty()) {
            Event curr = eventList.poll();
            clock = curr.time;
            //System.out.println(curr.process + curr.type + " " + clock);
            switch (curr.type) {
                case "arrival":
                    PCB.putIfAbsent(curr.process, new Process(curr.process, false, curr.time, curr.burst));
                    if (isCPUFree) {
                        CPURecords.add(new CheckPoint(curr.process, clock, "starts"));
                        isCPUFree = false;
                        PCB.get(curr.process).isRunning = true;
                        PCB.get(curr.process).waitingTime += clock - PCB.get(curr.process).lastTimeEnteringReadyQueue;
                        if (PCB.get(curr.process).remaining > quantum) {
                            addToWaitList(new Event("timerInterrupt", curr.process, clock + quantum));
                        } else {
                            addToWaitList(new Event("completion", curr.process, clock + PCB.get(curr.process).remaining));
                        }
                    } else {
                        PCB.get(curr.process).isRunning = false;
                        readyQueue.offer(curr.process);
                        PCB.get(curr.process).lastTimeEnteringReadyQueue = clock;
                    }
                    break;
                case "completion":
                    PCB.get(curr.process).completionTime = clock;
                    PCB.get(curr.process).remaining = 0f;
                    CPURecords.add(new CheckPoint(curr.process, clock, "ends"));
                    if (!readyQueue.isEmpty()) {
                        int nextP = readyQueue.poll();
                        PCB.get(nextP).isRunning = true;
                        PCB.get(nextP).waitingTime += clock - PCB.get(nextP).lastTimeEnteringReadyQueue;
                        CPURecords.add(new CheckPoint(nextP, clock, "starts"));
                        if (PCB.get(nextP).remaining > quantum) {
                            addToWaitList(new Event("timerInterrupt", nextP, clock + quantum));
                        } else {
                            addToWaitList(new Event("completion", nextP, clock + PCB.get(nextP).remaining));
                        }
                    } else {
                        isCPUFree = true;
                    }
                    break;
                case "timerInterrupt":
                    PCB.get(curr.process).remaining -= quantum;
                    PCB.get(curr.process).isRunning = false;
                    CPURecords.add(new CheckPoint(curr.process, clock, "ends"));
                    readyQueue.offer(curr.process);
                    PCB.get(curr.process).lastTimeEnteringReadyQueue = clock;
                    int nextP = readyQueue.poll();
                    PCB.get(nextP).isRunning = true;
                    PCB.get(nextP).waitingTime += clock - PCB.get(nextP).lastTimeEnteringReadyQueue;
                    CPURecords.add(new CheckPoint(nextP, clock, "starts"));
                    if (PCB.get(nextP).remaining > quantum) {
                        addToWaitList(new Event("timerInterrupt", nextP, clock + quantum));
                    } else {
                        addToWaitList(new Event("completion", nextP, clock + PCB.get(nextP).remaining));
                    }
                    break;
                default:
                    System.out.println(curr.type);
                    System.out.println("Wrong Event Exception");
                    break;
            }
        }
    }
    
    public static void printRecords() {
        float avgTurn = 0f;
        int processNumber = PCB.keySet().size();
        System.out.println("Turnaround time: ");
        for (int i = 1; i <= processNumber; i++) {
            avgTurn += PCB.get(i).completionTime;
            System.out.print("P" + i + " " + keepThreeDec(PCB.get(i).completionTime) + "s, ");
        }
        avgTurn /= processNumber;
        System.out.println("\n");

        float avgWaiting = 0f;
        System.out.println("Waiting time: ");
        for (int i = 1; i <= processNumber; i++) {
            avgWaiting += PCB.get(i).waitingTime;
            System.out.print("P" + i + " " + keepThreeDec(PCB.get(i).waitingTime) + "s, ");
        }
        avgWaiting /= processNumber;
        System.out.println("\n");

        System.out.println("The average turnaround time is: " + avgTurn);
        System.out.println("The average waiting time is: " + avgWaiting);
        
        System.out.println("-------------------------------------------------------------------");
        System.out.print("|");
        for (CheckPoint record : CPURecords) {
            switch(record.comment) {
                case "starts":
                    System.out.print("" + record.time + "   P" + record.processIndex + " ");
                    break;
                case "ends":
                    System.out.print("  " + "|");
                    break;
            }
        }
        System.out.print(clock);
        System.out.println("\n-------------------------------------------------------------------");
        CPURecords.clear();
    }
    
    public static void addToWaitList(Event e) {
        if(eventList.isEmpty()) {
            eventList.add(e);
        } else {
            int index = 0;
            for (Event ev : eventList) {
                if(ev.time <= e.time) index++;
                else break;
            }
            eventList.add(index, e);
        }
    }
    
    private static void loadEvents5_3() {
        addToWaitList(new Event("arrival", 1, (float) 0, 8));
        addToWaitList(new Event("arrival", 2, (float) 0.4, 4));
        addToWaitList(new Event("arrival", 3, (float) 1.0, 1));
    }
    
    private static void loadEvents5_12() {
        addToWaitList(new Event("arrival", 1, (float) 0, 10));
        addToWaitList(new Event("arrival", 2, (float) 0, 1));
        addToWaitList(new Event("arrival", 3, (float) 0, 2));
        addToWaitList(new Event("arrival", 4, (float) 0, 1));
        addToWaitList(new Event("arrival", 5, (float) 0, 5));
    }

    static float keepThreeDec(float input) {
        return (float)(Math.round(input * 100)) / 100;
    }
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please input an integer to select the subroutine to execute, type 'quit' to quit");
        System.out.println("'0' for Question 5.3 FCFS");
        System.out.println("'1' for Question 5.3 SJF");
        System.out.println("'2' for Question 5.3 future-knowledge SJF");
        System.out.println("'3' for Question 5.12 FCFS");
        System.out.println("'4' for Question 5.12 SJF");
        System.out.println("'5' for Question 5.12 nonpreemptive Priority");
        System.out.println("'6' for Question 5.12 RR");
        String ans = "";
        do{
            ans = sc.nextLine();
            if (ans.equals("quit")) break;
            switch (Integer.parseInt(ans)) {
                case 0:
                    //Question 5.3 FCFS
                    loadEvents5_3();
                    FCFS();
                    printRecords();
                    break;
                case 1:
                    //Question 5.3 SJF
                    loadEvents5_3();
                    SJF();
                    printRecords();
                    break;
                case 2:
                    //Question 5.3 future-knowledge
                    addToWaitList(new Event("arrival", -100,0, 1));
                    loadEvents5_3();
                    SJF();
                    PCB.remove(-100);
                    printRecords();
                    break;
                case 3:
                    //Question 5.12 FCFS
                    loadEvents5_12();
                    FCFS();
                    printRecords();
                    break;
                case 4:
                    //Question 5.12 SJF
                    addToWaitList(new Event("arrival", -100, (float) -1, 1));
                    loadEvents5_12();
                    SJF();
                    CPURecords.remove(0);
                    CPURecords.remove(0);
                    PCB.remove(-100);
                    printRecords();
                    break;
                case 5:
                    //Question 5.12 nonpreemptivePriority
                    Map<Integer, Integer> priorities = new HashMap<>();
                    priorities.put(1, 3);   //P1 has priority 3
                    priorities.put(2, 1);   //...
                    priorities.put(3, 3);
                    priorities.put(4, 4);
                    priorities.put(5, 2);
                    addToWaitList(new Event("arrival", -100, (float) -1, 1));
                    loadEvents5_12();
                    nonpreemptivePriority(priorities);
                    CPURecords.remove(0);
                    CPURecords.remove(0);
                    PCB.remove(-100);
                    printRecords();
                    break;
                case 6:
                    //Question 5.12 RR
                    loadEvents5_12();
                    RR(1);
                    printRecords();
                    break;
                default:
                    System.out.println("Wrong Call Number Exception");
                    break;
            }
        }while(!ans.equals("quit"));
    }
    
}
