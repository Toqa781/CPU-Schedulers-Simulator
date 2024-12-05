import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class NonPreemptivePriorityScheduler {

    private List<Process> processes = new ArrayList<>();
    private List<String> executionOrder = new ArrayList<>();
    private int contextSwitchTime;

    public NonPreemptivePriorityScheduler(int contextSwitchTime) {
        this.contextSwitchTime = contextSwitchTime;
    }

    public void addProcess(Process process) {
        processes.add(process);
    }

    public void schedule() {
        int currentTime = 0;
        int completed = 0;

        while (completed < processes.size()) {
            
            int finalCurrentTime = currentTime;
            Process currentProcess = processes.stream()
                    .filter(p -> !p.isCompleted && p.arrivalTime <= finalCurrentTime)
                    .min(Comparator.comparingInt(p -> p.priority))  
                    .orElse(null);

            if (currentProcess == null) {
                currentTime++; 
                continue;
            }
            
            currentTime += contextSwitchTime;

            
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
        System.out.println("Name\tArrival\tBurst\tPriority\tCompletion\tTurnaround\tWaiting");
        int totalWaitingTime = 0, totalTurnaroundTime = 0;

        for (Process process : processes) {
            System.out.printf("%s\t\t%d\t\t%d\t\t%d\t\t\t%d\t\t\t%d\t\t\t%d\n",
                    process.name, process.arrivalTime, process.burstTime, process.priority,
                    process.completionTime, process.turnaroundTime, process.waitingTime);
            totalWaitingTime += process.waitingTime;
            totalTurnaroundTime += process.turnaroundTime;
        }

        System.out.printf("\nAverage Waiting Time: %.2f\n", (double) totalWaitingTime / processes.size());
        System.out.printf("Average Turnaround Time: %.2f\n", (double) totalTurnaroundTime / processes.size());
    }

    public void displayGraph() {
        JFrame frame = new JFrame("Non-Preemptive Priority Scheduler");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);
        frame.add(new GraphPanel(processes, executionOrder));
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();

        System.out.print("Enter context switching time: ");
        int contextSwitchTime = sc.nextInt();

        NonPreemptivePriorityScheduler scheduler = new NonPreemptivePriorityScheduler(contextSwitchTime);

        for (int i = 0; i < n; i++) {
            System.out.print("Enter Process Name: ");
            String name = sc.next();

            System.out.print("Enter Process Color (R G B): ");
            int r = 0, g = 0, b = 0;
            boolean validColor = false;

            while (!validColor) {
                try {
                    r = sc.nextInt();
                    g = sc.nextInt();
                    b = sc.nextInt();

                    
                    if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
                        throw new IllegalArgumentException("RGB values must be between 0 and 255.");
                    }
                    validColor = true;
                } catch (Exception e) {
                    System.out.println("Invalid input. Please enter three integers (R G B) between 0 and 255:");
                    sc.nextLine(); 
                }
            }


            System.out.print("Enter Arrival Time: ");
            int arrivalTime = sc.nextInt();

            System.out.print("Enter Burst Time: ");
            int burstTime = sc.nextInt();

            System.out.print("Enter Priority: ");
            int priority = sc.nextInt();

            scheduler.addProcess(new Process(name, new Color(r, g, b), arrivalTime, burstTime, priority));
        }

        scheduler.schedule();
        scheduler.printResults();
        scheduler.displayGraph();
    }
}

