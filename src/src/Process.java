import java.awt.*;

public class Process {
    String name;
    Color color;
    int arrivalTime;
    int burstTime;
    int priority;
    int completionTime;
    int waitingTime;
    int turnaroundTime;
    boolean isCompleted;
    int remainingBurstTime;
    int waitingTimeCounter;
    int startTime;
    int endTime;
    // SJF
    public Process(String name, Color color, int arrivalTime, int burstTime) {
        this.name = name;
        this.color = color;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingBurstTime = burstTime;
        this.isCompleted = false;
        this.waitingTimeCounter = 0;
    }

    // priority
    public Process(String name, Color color, int arrivalTime, int burstTime, int priority) {
        this(name, color, arrivalTime, burstTime);
        this.priority = priority;
    }

    public void calculateTurnaroundTime() {
        this.turnaroundTime = this.completionTime - this.arrivalTime;
    }

    public void calculateWaitingTime() {
        this.waitingTime = this.turnaroundTime - this.burstTime;
    }

    // Aging of sjf for starvation problem
    public void applyAging(int agingThreshold) {
        if (waitingTimeCounter >= agingThreshold) {
            this.remainingBurstTime--;
            this.remainingBurstTime = Math.max(this.remainingBurstTime, 1);
            this.waitingTimeCounter = 0;
        }
    }

    @Override
    public String toString() {
        return "Process{" +
                "name='" + name + '\'' +
                ", arrivalTime=" + arrivalTime +
                ", burstTime=" + burstTime +
                ", remainingBurstTime=" + remainingBurstTime +
                ", completionTime=" + completionTime +
                ", waitingTime=" + waitingTime +
                ", turnaroundTime=" + turnaroundTime +
                ", isCompleted=" + isCompleted +
                '}';
    }
}
