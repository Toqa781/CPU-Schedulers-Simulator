import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class NonPreemptiveSJF {
    private List<Process> processes = new ArrayList<>();
    private List<String> executionOrder = new ArrayList<>();

    public void addProcess(Process process) {
        processes.add(process);
    }

    public void schedule() {
        int currentTime = 0;
        int completed = 0;
        final int AGING_THRESHOLD = 5;

        while (completed < processes.size()) {

            for (Process p : processes) {
                if (!p.isCompleted && p.arrivalTime <= currentTime) {
                    p.waitingTimeCounter++;
                }
            }

            // Decrease burst time slightly for processes that waited too long "aging"
            for (Process p : processes) {
                if (!p.isCompleted && p.waitingTimeCounter >= AGING_THRESHOLD) {
                    p.burstTime--;
                    p.burstTime = Math.max(p.burstTime, 1);
                    p.waitingTimeCounter = 0;
                }
            }

            // Select the shortest job that has arrived, considering aging-adjusted burst time
            int finalCurrentTime = currentTime;
            Process currentProcess = processes.stream()
                    .filter(p -> !p.isCompleted && p.arrivalTime <= finalCurrentTime)
                    .min(Comparator.comparingInt(p -> p.burstTime))
                    .orElse(null);

            if (currentProcess == null) {
                currentTime++;
                continue;
            }

            executionOrder.add(currentProcess.name);
            currentTime += currentProcess.burstTime;
            currentProcess.completionTime = currentTime;
            currentProcess.turnaroundTime = currentProcess.completionTime - currentProcess.arrivalTime;
            currentProcess.waitingTime = currentProcess.turnaroundTime - currentProcess.burstTime;
            currentProcess.isCompleted = true;

            completed++;
        }
    }

    public void printResults() {
        System.out.println("Processes Execution Order: " + executionOrder);
        System.out.println("\nProcess Details:");
        System.out.println("Name\tArrival\tBurst\tCompletion\tTurnaround\tWaiting");
        int totalWaitingTime = 0, totalTurnaroundTime = 0;

        for (Process process : processes) {
            System.out.printf("%s\t\t%d\t\t%d\t\t%d\t\t\t%d\t\t\t%d\n",
                    process.name, process.arrivalTime, process.burstTime,
                    process.completionTime, process.turnaroundTime, process.waitingTime);
            totalWaitingTime += process.waitingTime;
            totalTurnaroundTime += process.turnaroundTime;
        }

        System.out.printf("\nAverage Waiting Time: %.2f\n", (double) totalWaitingTime / processes.size());
        System.out.printf("Average Turnaround Time: %.2f\n", (double) totalTurnaroundTime / processes.size());
    }

    public void displayGraph() {
        JFrame frame = new JFrame("Non-Preemptive SJF Scheduler");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);
        frame.add(new GraphPanel(processes, executionOrder));
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();

        NonPreemptiveSJF scheduler = new NonPreemptiveSJF();

        for (int i = 0; i < n; i++) {
            System.out.print("Enter Process Name: ");
            String name = sc.next();

            System.out.print("Enter Process Color (R G B): ");
            int r = sc.nextInt();
            int g = sc.nextInt();
            int b = sc.nextInt();

            System.out.print("Enter Arrival Time: ");
            int arrivalTime = sc.nextInt();

            System.out.print("Enter Burst Time: ");
            int burstTime = sc.nextInt();

            scheduler.addProcess(new Process(name, new Color(r, g, b), arrivalTime, burstTime));
        }

        scheduler.schedule();
        scheduler.printResults();
        scheduler.displayGraph();
    }
}


