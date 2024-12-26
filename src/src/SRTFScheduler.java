import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class SRTFScheduler {
    private final List<Process> processes = new ArrayList<>();
    private final List<String> executionOrder = new ArrayList<>();
    private int contextSwitchTime;

    private Process findNextProccess(int currentTime, Process previousProcess) {
       final int finalCurrentTime = currentTime;
        Process currentProccessInCaseOfStarvation = processes.stream()
                .filter(p -> !p.isCompleted && p.arrivalTime <= finalCurrentTime)
                .max(Comparator.comparingInt(p -> p.waitingTime))
                .orElse(previousProcess);


        Process currentProcess = processes.stream()
                .filter(p -> !p.isCompleted && p.arrivalTime <= finalCurrentTime)
                .min(Comparator.comparingInt(p -> p.remainingBurstTime))
                .orElse(null);

        if(currentProccessInCaseOfStarvation.waitingTime>5){
            return currentProccessInCaseOfStarvation;
        }
        return currentProcess;
    }

    //default constructor
    public SRTFScheduler() {}
    //constructor to initialize the context switch time
    public SRTFScheduler(int contextSwitchTime) {
        this.contextSwitchTime = contextSwitchTime;
    }

    //method to add a process to the list of processes
    public void addProcess(Process process) {
        processes.add(process);
    }

    //method to schedule the processes
    public void schedule() {
        int currentTime = 0; // Tracks the current system time
        int completed = 0;   // Tracks the number of completed processes
        Process previousProcess = null; // Tracks the process executed in the previous cycle

        while (completed < processes.size()) {

            // calculating waiting time for each process.
            for (Process process : processes) {
                if (!process.isCompleted && process.arrivalTime <= currentTime && process != previousProcess) {
                    process.waitingTime++; // Increment waiting time for aging
                }
            }

            // Find the next process to execute
            Process currentProcess = findNextProccess(currentTime, previousProcess);

            if (currentProcess == null) {
                // No process is ready, simulate idle time
                executionOrder.add("Idle");
                currentTime++;
                continue;
            }

            // Add context switch time if needed
            if (previousProcess != null && !currentProcess.equals(previousProcess)) {
                for (int i = 0; i < contextSwitchTime; i++) {
                    executionOrder.add("CS");
                    currentTime++;
                }
            }

            // Execute the current process
            executionOrder.add(currentProcess.name);
            currentProcess.remainingBurstTime--;
            currentProcess.waitingTime=0;//reset waiting time to ensure that no starvation will happen.
            currentTime++;

            // Check if the current process is completed
            if (currentProcess.remainingBurstTime == 0) {
                currentProcess.isCompleted = true;
                currentProcess.completionTime = currentTime;
                currentProcess.turnaroundTime = currentProcess.completionTime - currentProcess.arrivalTime;
                currentProcess.waitingTime = currentProcess.turnaroundTime - currentProcess.burstTime;
                completed++;
            }

            // Update reference to the last executed process
            previousProcess = currentProcess;
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
        System.out.printf("\nContext Switch Time: " + contextSwitchTime);
        System.out.printf("\nAverage Waiting Time: %.2f\n", (double) totalWaitingTime / processes.size());
        System.out.printf("Average Turnaround Time: %.2f\n", (double) totalTurnaroundTime / processes.size());
    }

    public void displayGraph() {
        JFrame frame = new JFrame("Non-Preemptive SRTF Scheduler");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);
        frame.add(new GraphPanel(processes, executionOrder));
        frame.setVisible(true);
    }

    static class GraphPanel extends JPanel {
        private final List<Process> processes;
        private final List<String> executionOrder;

        public GraphPanel(List<Process> processes, List<String> executionOrder) {
            this.processes = processes;
            this.executionOrder = executionOrder;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            int width = getWidth();
            int height = getHeight();
            int barHeight = 50;
            int timeWidth = Math.max(width / executionOrder.size(), 1); // Ensure timeWidth is non-zero
            int x = 0;

            for (int i = 0; i < executionOrder.size(); i++) {
                String processName = executionOrder.get(i);

                if (processName.equals("Idle")) {
                    g.setColor(Color.LIGHT_GRAY);
                    g.fillRect(x, height / 2 - barHeight / 2, timeWidth, barHeight);
                    g.setColor(Color.black);
                    g.drawString("Idle", x + timeWidth / 2 - 10, height / 2 - barHeight / 2 - 5);
                } else if (processName.equals("CS")) {
                    g.setColor(Color.YELLOW);
                    g.fillRect(x, height / 2 - barHeight / 2, timeWidth, barHeight);
                    g.setColor(Color.BLACK);
                    g.drawString("CS", x + timeWidth / 2 - 10, height / 2 - barHeight / 2 - 5);
                } else {
                    Process process = processes.stream()
                            .filter(p -> p.name.equals(processName))
                            .findFirst()
                            .orElse(null);

                    if (process != null) {
                        g.setColor(process.color);
                        g.fillRect(x, height / 2 - barHeight / 2, timeWidth, barHeight);
                        g.setColor(Color.BLACK);
                        g.drawString(process.name, x + timeWidth / 2 - 10, height / 2 - barHeight / 2 - 5);
                    }
                }

                g.setColor(Color.BLACK);
                g.drawString(String.valueOf(i), x, height / 2 + barHeight / 2 + 15);

                x += timeWidth;
            }
        }
    }

    static SRTFScheduler readFromConsole(Scanner scanner) {
        System.out.print("Enter number of processes: ");
        int n = scanner.nextInt();

        System.out.print("Enter context switching time: ");
        int contextSwitchTime = scanner.nextInt();

        SRTFScheduler scheduler = new SRTFScheduler(contextSwitchTime);

        for (int i = 0; i < n; i++) {
            System.out.print("Enter Process Name: ");
            String name = scanner.next();

            System.out.print("Enter Process Color (R G B): ");
            int r = scanner.nextInt();
            int g = scanner.nextInt();
            int b = scanner.nextInt();

            System.out.print("Enter Arrival Time: ");
            int arrivalTime = scanner.nextInt();

            System.out.print("Enter Burst Time: ");
            int burstTime = scanner.nextInt();

            scheduler.addProcess(new Process(name, new Color(r, g, b), arrivalTime, burstTime));
        }

        return scheduler;
    }

    static SRTFScheduler readFromFile(String fileName) {
        SRTFScheduler scheduler = null;

        try (Scanner fileScanner = new Scanner(new java.io.File(fileName))) {
            // Read number of processes
            int n = fileScanner.nextInt();

            // Read context switching time
            int contextSwitchTime = fileScanner.nextInt();

            scheduler = new SRTFScheduler(contextSwitchTime);

            // Read process data
            for (int i = 0; i < n; i++) {
                String name = fileScanner.next();
                int r = fileScanner.nextInt();
                int g = fileScanner.nextInt();
                int b = fileScanner.nextInt();
                int arrivalTime = fileScanner.nextInt();
                int burstTime = fileScanner.nextInt();

                scheduler.addProcess(new Process(name, new Color(r, g, b), arrivalTime, burstTime));
            }
        } catch (java.io.FileNotFoundException e) {
            System.out.println("File not found: " + fileName);
        }

        return scheduler;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter 1 to use console input or 2 to read from file: ");
        int choice = scanner.nextInt();

        SRTFScheduler scheduler;
        if (choice == 2) {
            String fileName = "input.txt";
            scheduler = readFromFile(fileName);
        } else {
            scheduler = readFromConsole(scanner);
        }
        scheduler.schedule();
        scheduler.printResults();
        scheduler.displayGraph();
    }
}

