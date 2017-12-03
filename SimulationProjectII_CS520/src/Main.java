import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

class Event {
    String type;
    int process;
    float time;
    float burstTime;
    public Event(String type, int process, float time, float burst) {
        this.type = type;
        this.time = time;
        this.process = process;
        this.burstTime = burst;
        //Insert this event to its correct place
        int index = 0;
        for (Event ev : Main.eventQueue) {
            if(ev.time <= time) index++;
            else break;
        }
        Main.eventQueue.add(index, this);
    }
    public Event(String type, int process, float time) {
        this.type = type;
        this.time = time;
        this.process = process;
        //Insert this event to its correct place
        int index = 0;
        for (Event ev : Main.eventQueue) {
            if(ev.time <= time) index++;
            else break;
        }
        Main.eventQueue.add(index, this);
    }
}
class Process {
    int index;
    String state;   //state could be: running, ready, IOPending
    float startTime;
    float burstTime;
    float remaining;
    float mean_interIO_intervals;
    float completionTime;
    float waitingTime;
    float lastTimeEnteringReadyQueue;
    float currBurstRemaining;
    boolean isNewBurst;
    static float[] intervalsArray = new float[] {0.03f, 0.035f, 0.04f, 0.045f, 0.05f, 0.055f, 0.06f, 0.065f, 0.07f, 0.075f};
    public Process(int index, String state, float startTime) {
        this.index = index;
        this.state = state;
        this.startTime = startTime;
        this.burstTime = 120f + (120f * Main.rd.nextFloat()); //Uniformely from 2mins to 4mins
        remaining = burstTime;
        mean_interIO_intervals = intervalsArray[index];
        completionTime = -1f;
        waitingTime = 0f;
        isNewBurst = true;
        lastTimeEnteringReadyQueue = 0f;
    }
    //This constructor is for RR algorith, without any random values
    public Process(int index, String state, float startTime, String str) {
        this.index = index;
        this.state = state;
        this.startTime = startTime;
        this.burstTime = 120f; //Uniformely from 2mins to 4mins
        remaining = burstTime;
        mean_interIO_intervals = intervalsArray[index];
        completionTime = -1f;
        waitingTime = 0f;
        isNewBurst = true;
        lastTimeEnteringReadyQueue = 0f;
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
class FloatPair {
    float trueValue;
    float predictedValue;
    public FloatPair(float t, float tau) {
        trueValue = t;
        predictedValue = tau;
    }
}

public class Main {
    static LinkedList<Event> eventQueue = new LinkedList<>();
    static Map<Integer, Process> PCB = new HashMap<>();
    static LinkedList<Integer> readyQueue = new LinkedList<>();
    static Queue<Integer> IOQueue = new LinkedList<>();
    
    //Variables for exponential averages
    static Map<Integer, FloatPair> expoAvgMap = new HashMap<>();
    static float alpha = (float) 1 / (float) 3; //alpha could be 1, 0.5f or (float) 1 / (float) 3
    
    static List<CheckPoint> CPURecords = new ArrayList<>();
    public static Random rd = new Random();
    public static float clock = 0;  //The time unit is second
    static boolean isCPUFree = true;
    static boolean isIOChannelFree = true;
    
    private static void initialization() {  //Generate 10 arrivals events.
        for (int i = 0; i < 10; i++) {
            new Event("arrival", i, 0f);
        }
    }

    public static float getExpRandom(float mean_interIO_intervals) {
        //Ref.: https://stackoverflow.com/questions/2106503/pseudorandom-number-generator-exponential-distribution
        float uniRd = rd.nextFloat(); //uniRd is a uniform randome number
        float expRd = (float) Math.log(1 - uniRd) * (-mean_interIO_intervals);  //mean arrival rate = 1 / mean inter arrival rate
        return expRd;
    }
    
    //Sort the ready queue by predicted burst
    static void offerByAvg(int process) {
        float thisPredicated = expoAvgMap.get(process).predictedValue;
        int index = 0;
        for (int p : readyQueue) {
            float predicted = expoAvgMap.get(p).predictedValue;
            if(predicted <= thisPredicated) index++;
            else break;
        }
        readyQueue.add(index, process);
    }
    
    static void updateFloatPair(int process, float nextIOInterval) {
        float taunMinus1 = expoAvgMap.get(process).predictedValue;
        expoAvgMap.get(process).trueValue = nextIOInterval;
        if (taunMinus1 == 0) {
            expoAvgMap.get(process).predictedValue = alpha * nextIOInterval + (1 - alpha) * PCB.get(process).mean_interIO_intervals;
        } else {
            expoAvgMap.get(process).predictedValue = alpha * nextIOInterval + (1 - alpha) * taunMinus1;
        }
    }
    
    static void requestOrCompletion(int processIndex) {
        float nextIOInterval = getExpRandom(PCB.get(processIndex).mean_interIO_intervals);
        updateFloatPair(processIndex, nextIOInterval);
        if (nextIOInterval >= PCB.get(processIndex).remaining) {
            new Event("completion", processIndex, clock + PCB.get(processIndex).remaining);
        } else {
            new Event("IORequest", processIndex, clock + nextIOInterval, nextIOInterval);
        }
    }
    static void pollNextProcess() {
        if (!readyQueue.isEmpty()) {
            isCPUFree = false;
            int nextP = readyQueue.poll();
            PCB.get(nextP).state = "running";
            PCB.get(nextP).waitingTime += clock - PCB.get(nextP).lastTimeEnteringReadyQueue;
            //Process starts
            CPURecords.add(new CheckPoint(nextP, clock, "starts"));
            requestOrCompletion(nextP);
        } else {
            isCPUFree = true;
        }
    }
    //For RR, we don't use any random value here
    //Determine if next operation is interrupt, IORequest or completion
    static void nextOperation(int nextP, float quantum) {
        isCPUFree = false;
        PCB.get(nextP).waitingTime += clock - PCB.get(nextP).lastTimeEnteringReadyQueue;
        //System.out.println(clock - PCB.get(nextP).lastTimeEnteringReadyQueue);
        float nextIOInterval;
        if (PCB.get(nextP).isNewBurst) {
            nextIOInterval = getExpRandom(PCB.get(nextP).mean_interIO_intervals);
            PCB.get(nextP).currBurstRemaining = nextIOInterval; //This is the value of the new current CPU burst.
        } else {
            //If it's not a new burst, we will use its remained current burst time
            nextIOInterval = PCB.get(nextP).currBurstRemaining;
        }
        PCB.get(nextP).isNewBurst = false;
        PCB.get(nextP).state = "running";
        CPURecords.add(new CheckPoint(nextP, clock, "starts"));
        float min = Math.min(Math.min(nextIOInterval, quantum), PCB.get(nextP).remaining);
        if (PCB.get(nextP).remaining == min) {
            new Event("completion", nextP, clock + PCB.get(nextP).remaining);
        } else if (nextIOInterval == min){
            new Event("IORequest", nextP, clock + nextIOInterval, nextIOInterval);
        } else {
            new Event("timerInterrupt", nextP, clock + quantum);
        }
    }
    
    public static void FCFS() {
        while (!eventQueue.isEmpty()) {
            Event curr = eventQueue.poll();
            clock = curr.time;
            int processIndex = curr.process;
            //System.out.println(curr.process + curr.type + " " + clock);
            switch (curr.type) {
                case "arrival":
                    PCB.putIfAbsent(processIndex, new Process(processIndex, "ready", curr.time));
                    expoAvgMap.putIfAbsent(processIndex, new FloatPair(0, 0));
                    if (isCPUFree) {
                        isCPUFree = false;
                        //Update PCB
                        PCB.get(processIndex).state = "running";
                        PCB.get(processIndex).waitingTime += clock - PCB.get(processIndex).lastTimeEnteringReadyQueue;
                        //Process starts
                        CPURecords.add(new CheckPoint(processIndex, clock, "starts"));
                        requestOrCompletion(processIndex);  //Insert either completion or IORequest in next event
                    } else {
                        readyQueue.offer(curr.process);
                        //PCB.get(processIndex).lastTimeEnteringReadyQueue = clock;
                    }
                    break;
                case "completion":
                    PCB.get(processIndex).completionTime = clock;
                    PCB.get(processIndex).remaining = 0f;
                    CPURecords.add(new CheckPoint(curr.process, clock, "ends"));
                    //Start next process from the ready queue
                    pollNextProcess();
                    break;
                case "IORequest":
                    isCPUFree = true;
                    PCB.get(processIndex).state = "IOPending";
                    PCB.get(processIndex).remaining -= curr.burstTime;
                    CPURecords.add(new CheckPoint(curr.process, clock, "ends"));
                    IOQueue.offer(processIndex);
                    PCB.get(processIndex).lastTimeEnteringReadyQueue = clock;
                    if (isIOChannelFree) {
                        int IOProcess = IOQueue.poll();
                        isIOChannelFree = false;
                        new Event("IOCompletion", IOProcess, clock + 0.06f);
                    }
                    //Start next process from the ready queue
                    pollNextProcess();
                    break;
                case "IOCompletion":
                    isIOChannelFree = true;
                    new Event("arrival", processIndex, clock);
                    if (!IOQueue.isEmpty()) {
                        int IOProcess = IOQueue.poll();
                        isIOChannelFree = false;
                        new Event("IOCompletion", IOProcess, clock + 0.06f);
                    }
                    break;
                default:
                    System.out.println("Wrong Event Exception: " + curr.type);
                    break;
            }
        }
    }
    
    public static void SJF() {
        while (!eventQueue.isEmpty()) {
            Event curr = eventQueue.poll();
            clock = curr.time;
            int processIndex = curr.process;
            //System.out.println(curr.process + curr.type + " " + clock);
            switch (curr.type) {
                case "arrival":
                    PCB.putIfAbsent(processIndex, new Process(processIndex, "ready", curr.time));
                    expoAvgMap.putIfAbsent(processIndex, new FloatPair(0, 0));
                    if (isCPUFree) {
                        isCPUFree = false;
                        //Update PCB
                        PCB.get(processIndex).state = "running";
                        PCB.get(processIndex).waitingTime += clock - PCB.get(processIndex).lastTimeEnteringReadyQueue;
                        //Process starts
                        CPURecords.add(new CheckPoint(processIndex, clock, "starts"));
                        requestOrCompletion(processIndex);  //Insert either completion or IORequest in next event
                    } else {
                        //Sort the process according to its predicted burst
                        offerByAvg(processIndex);
                        //PCB.get(processIndex).lastTimeEnteringReadyQueue = clock;
                    }
                    break;
                case "completion":
                    PCB.get(processIndex).completionTime = clock;
                    PCB.get(processIndex).remaining = 0f;
                    CPURecords.add(new CheckPoint(curr.process, clock, "ends"));
                    //Start next process from the ready queue
                    pollNextProcess();
                    break;
                case "IORequest":
                    isCPUFree = true;
                    PCB.get(processIndex).state = "IOPending";
                    PCB.get(processIndex).remaining -= curr.burstTime;
                    CPURecords.add(new CheckPoint(curr.process, clock, "ends"));
                    IOQueue.offer(processIndex);
                    PCB.get(processIndex).lastTimeEnteringReadyQueue = clock;
                    if (isIOChannelFree) {
                        int IOProcess = IOQueue.poll();
                        isIOChannelFree = false;
                        new Event("IOCompletion", IOProcess, clock + 0.06f);
                    }
                    //Start next process from the ready queue
                    pollNextProcess();
                    break;
                case "IOCompletion":
                    isIOChannelFree = true;
                    new Event("arrival", processIndex, clock);
                    if (!IOQueue.isEmpty()) {
                        int IOProcess = IOQueue.poll();
                        isIOChannelFree = false;
                        new Event("IOCompletion", IOProcess, clock + 0.06f);
                    }
                    break;
                default:
                    System.out.println("Wrong Event Exception: " + curr.type);
                    break;
            }
        }
    }

    public static void RR(float quantum) {
        while (!eventQueue.isEmpty()) {
            Event curr = eventQueue.poll();
            clock = curr.time;
            int processIndex = curr.process;
            //System.out.println(curr.process + curr.type + " " + clock);
            switch (curr.type) {
                case "arrival":
                    PCB.putIfAbsent(processIndex, new Process(processIndex, "ready", curr.time));
                    if (isCPUFree) {
                        isCPUFree = false;
                        //Update PCB
                        PCB.get(processIndex).state = "running";
                        //Process starts
                        nextOperation(processIndex, quantum);
                    } else {
                        readyQueue.offer(processIndex);
                        //PCB.get(processIndex).lastTimeEnteringReadyQueue = clock;
                    }
                    break;
                case "completion":
                    isCPUFree = true;
                    PCB.get(processIndex).completionTime = clock;
                    PCB.get(processIndex).remaining = 0f;
                    CPURecords.add(new CheckPoint(curr.process, clock, "ends"));
                    //Start next process from the ready queue
                    if (!readyQueue.isEmpty()) {
                        isCPUFree = false;
                        int nextP = readyQueue.poll();
                        nextOperation(nextP, quantum);
                    } else {
                        isCPUFree = true;
                    }
                    break;
                case "timerInterrupt":  //Quantum interrupt
                    isCPUFree = true;
                    PCB.get(processIndex).remaining -= quantum;
                    PCB.get(processIndex).currBurstRemaining -= quantum;
                    PCB.get(processIndex).state = "ready";
                    CPURecords.add(new CheckPoint(processIndex, clock, "ends"));
                    readyQueue.offer(processIndex);
                    PCB.get(processIndex).lastTimeEnteringReadyQueue = clock;
                    //Next process starts
                    int nextP = readyQueue.poll();
                    nextOperation(nextP, quantum);
                    break;
                case "IORequest":   //IO interrupt
                    isCPUFree = true;
                    PCB.get(processIndex).state = "IOPending";
                    PCB.get(processIndex).remaining -= curr.burstTime;
                    //This process' curr CPU burst time has been ended by an IO. Its next CPU burst time would be a new one
                    PCB.get(processIndex).isNewBurst = true;
                    CPURecords.add(new CheckPoint(curr.process, clock, "ends"));
                    IOQueue.offer(processIndex);
                    PCB.get(processIndex).lastTimeEnteringReadyQueue = clock;
                    if (isIOChannelFree) {
                        int IOProcess = IOQueue.poll();
                        isIOChannelFree = false;
                        new Event("IOCompletion", IOProcess, clock + 0.06f);
                    }
                    //Start next process from the ready queue
                    if (!readyQueue.isEmpty()) {
                        isCPUFree = false;
                        nextP = readyQueue.poll();
                        nextOperation(nextP, quantum);
                    } else {
                        isCPUFree = true;
                    }
                    break;
                case "IOCompletion":
                    isIOChannelFree = true;
                    new Event("arrival", processIndex, clock);
                    if (!IOQueue.isEmpty()) {
                        int IOProcess = IOQueue.poll();
                        isIOChannelFree = false;
                        new Event("IOCompletion", IOProcess, clock + 0.06f);
                    }
                    break;
                default:
                    System.out.println("Wrong Event Exception: " + curr.type);
                    break;
            }
        }
    }

    public static void printRecords() {
        float utilized = 0;
        for (int i = 1; i < CPURecords.size(); i+=2) {
            //Test
            if (!(CPURecords.get(i).comment.equals("ends") && CPURecords.get(i - 1).comment.equals("starts"))) {
                System.out.println("Exception!");
            }
            //Sum up every slice time that CPU executes a process
            utilized += CPURecords.get(i).time - CPURecords.get(i - 1).time;
        }
        System.out.println("CPU Utilization: ");
        System.out.println((utilized / clock) * 100f + "%");
        System.out.println("Throughput: ");
        System.out.println(10f / (clock / 60) + " processes per minute\n");
        
        float avgExeTime = 0f;
        System.out.println("Execution Time:");
        for (int i = 0; i < 10; i++) {
            avgExeTime += PCB.get(i).burstTime;
            System.out.print("P" + i + " " + keepThreeDec(PCB.get(i).burstTime) + "s, ");
        }
        avgExeTime /= 10;
        System.out.println("\n");
        
        float avgTurn = 0f;
        System.out.println("Turnaround time: ");
        for (int i = 0; i < 10; i++) {
            avgTurn += PCB.get(i).completionTime;
            System.out.print("P" + i + " " + keepThreeDec(PCB.get(i).completionTime) + "s, ");
        }
        avgTurn /= 10;
        System.out.println("\n");
        
        float avgWaiting = 0f;
        System.out.println("Waiting time: ");
        for (int i = 0; i < 10; i++) {
            avgWaiting += PCB.get(i).waitingTime;
            System.out.print("P" + i + " " + keepThreeDec(PCB.get(i).waitingTime) + "s, ");
        }
        avgWaiting /= 10;
        System.out.println("\n");

        System.out.println("The average execution time is: " + avgExeTime);
        System.out.println("The average turnaround time is: " + avgTurn);
        System.out.println("The average waiting time is: " + avgWaiting);
        
        //Printing Gant charts:
        System.out.println();
        System.out.print("|");
        int index = 0;
        for (CheckPoint record : CPURecords) {
            switch(record.comment) {
                case "starts":
                    System.out.print("" + keepThreeDec(record.time) + "   P" + record.processIndex + " ");
                    break;
                case "ends":
                    System.out.print("  " + keepThreeDec(record.time) + "|");
                    if ((index + 1) % 10 == 0) {
                        System.out.println("");
                    }
                    break;
            }
            index++;
        }
        CPURecords.clear();
    }
    
    private static void printRecordsRR() throws FileNotFoundException, UnsupportedEncodingException {
        float utilized = 0;
        for (int i = 1; i < CPURecords.size(); i+=2) {
            //Test
            if (!(CPURecords.get(i).comment.equals("ends") && CPURecords.get(i - 1).comment.equals("starts"))) {
                System.out.println("Exception!");
            }
            //Sum up every slice time that CPU executes a process
            utilized += CPURecords.get(i).time - CPURecords.get(i - 1).time;
        }
        PrintWriter writer = new PrintWriter("RRlogfile.txt", "UTF-8");
        
        writer.println("CPU Utilization: ");
        writer.println((utilized / clock) * 100f + "%");
        writer.println("Throughput: ");
        writer.println(10f / (clock / 60) + " processes per minute\n");

        float avgExeTime = 0f;
        writer.println("Execution Time:");
        for (int i = 0; i < 10; i++) {
            avgExeTime += PCB.get(i).burstTime;
            writer.print("P" + i + " " + keepThreeDec(PCB.get(i).burstTime) + "s, ");
        }
        avgExeTime /= 10;
        writer.println("\n");

        float avgTurn = 0f;
        writer.println("Turnaround time: ");
        for (int i = 0; i < 10; i++) {
            avgTurn += PCB.get(i).completionTime;
            writer.print("P" + i + " " + keepThreeDec(PCB.get(i).completionTime) + "s, ");
        }
        avgTurn /= 10;
        writer.println("\n");

        float avgWaiting = 0f;
        writer.println("Waiting time: ");
        for (int i = 0; i < 10; i++) {
            avgWaiting += PCB.get(i).waitingTime;
            writer.print("P" + i + " " + keepThreeDec(PCB.get(i).waitingTime) + "s, ");
        }
        avgWaiting /= 10;
        writer.println("\n");

        writer.println("The average execution time is: " + avgExeTime);
        writer.println("The average turnaround time is: " + avgTurn);
        writer.println("The average waiting time is: " + avgWaiting);
        System.out.println("The average waiting time is: " + avgWaiting);

        //Printing Gant charts:
        writer.println();
        writer.print("|");
        int index = 0;
        for (CheckPoint record : CPURecords) {
            switch(record.comment) {
                case "starts":
                    writer.print("" + keepThreeDec(record.time) + "   P" + record.processIndex + " ");
                    break;
                case "ends":
                    writer.print("  " + keepThreeDec(record.time) + "|");
                    if ((index + 1) % 10 == 0) {
                        writer.println("");
                    }
                    break;
            }
            index++;
        }
        writer.close();
        CPURecords.clear();
    }
    
    static float keepThreeDec(float input) {
        return (float)(Math.round(input * 100)) / 100;
    }

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        System.out.println("You input an integer to select which algorith to execute: ");
        System.out.println("'0' for FCFS\n'1' for SJF while you may modify the value of alpha in the static attribute\n'2' for RR conditions 1\n'3' for RR conditions 2");
        System.out.println("type 'quit' to quit the loop");
        Scanner sc = new Scanner(System.in);
        do {
            String ans = sc.nextLine();
            if (ans.equals("quit")) break;
            switch(Integer.parseInt(ans)) {
                case 0:
                    initialization();
                    FCFS();
                    printRecords();
                    break;
                case 1:
                    initialization();
                    SJF();  //You could modify the alpha's value in the attribute
                    printRecords();
                    break;
                case 2:
                    initialization();
                    RR(0.03f); //The parameter here is the length of quantom
                    printRecordsRR();
                    break;
                case 3:
                    //Random values: CPU bursts & execution time
                    Process.intervalsArray = new float[] {0.03f, 0.03f, 0.03f, 0.03f, 0.03f, 0.03f, 0.03f, 0.03f, 0.3f, 0.3f};
                    initialization();
                    RR(0.075f); //The parameter here is the length of quantom
                    printRecordsRR();
                    break;
            }
            System.out.println("The total time is: " + clock);
        }while (true);
        System.out.println("You chose to quit.");
    }
    
}
