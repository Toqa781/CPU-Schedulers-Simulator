import java.util.*;

class Process {
    String name;
    int arrivalTime, burstTime, priority, remainingBurstTime, quantum, waitingTime = 0, turnaroundTime = 0;
    double fcaiFactor;

    public Process(String name, int arrivalTime, int burstTime, int priority, int quantum) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingBurstTime = burstTime;
        this.priority = priority;
        this.quantum = quantum;
    }

    public void calculateFcaiFactor(double v1, double v2) {
        fcaiFactor = (10 - priority) + (arrivalTime / v1) + (remainingBurstTime / v2);
    }
}

public class FCAIScheduler {
    public static void main(String[] args) {
    }
}
