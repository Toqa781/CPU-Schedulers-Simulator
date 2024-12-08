import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class NonPreemptiveSRTF {
    private List<Process> processes = new ArrayList<>();
    private List<String> executionOrder = new ArrayList<>();

    public void addProcess(Process process) {
        processes.add(process);
    }

    public void schedule() {
        int currentTime = 0;
        int completed = 0;

        while (completed < processes.size()) {
            // Find the process with the shortest remaining time
            int finalCurrentTime = currentTime;
            Process currentProcess = processes.stream()
                    .filter(p -> !p.isCompleted && p.arrivalTime <= finalCurrentTime)
                    .min(Comparator.comparingInt(p -> p.remainingBurstTime))
                    .orElse(null);

            if (currentProcess == null) {
                currentTime++;
                continue;
            }

            // Simulate execution of the process for one time unit (context switching included)
            executionOrder.add(currentProcess.name);
            currentProcess.remainingBurstTime--;
            currentTime++;

            // If the process finishes execution
            if (currentProcess.remainingBurstTime == 0) {
                currentProcess.isCompleted = true;
                currentProcess.completionTime = currentTime;
                currentProcess.turnaroundTime = currentProcess.completionTime - currentProcess.arrivalTime;
                currentProcess.waitingTime = currentProcess.turnaroundTime - currentProcess.burstTime;
                completed++;
            }
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
        JFrame frame = new JFrame("Non-Preemptive SRTF Scheduler");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);
        frame.add(new GraphPanel(processes, executionOrder));
        frame.setVisible(true);
    }

    static class GraphPanel extends JPanel {
        private List<Process> processes;
        private List<String> executionOrder;

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
            int timeWidth = width / executionOrder.size();
            int tableY = 200;

            int x = 0;

            for (int i = 0; i < executionOrder.size(); i++) {
                String processName = executionOrder.get(i);

                // Find the process by name
                Process process = processes.stream()
                        .filter(p -> p.name.equals(processName))
                        .findFirst()
                        .orElse(null);

                if (process != null) {
                    // Set the color of the process
                    g.setColor(process.color);
                    g.fillRect(x, height / 2 - barHeight / 2, timeWidth, barHeight);

                    // Draw process name
                    g.setColor(Color.BLACK);
                    g.drawString(process.name, x + timeWidth / 2 - 10, height / 2 - barHeight / 2 - 5);
                }

                // Draw time markers
                g.setColor(Color.BLACK);
                g.drawString(String.valueOf(i), x, height / 2 + barHeight / 2 + 15);

                x += timeWidth;
            }
            g.setColor(Color.BLACK);
            g.drawString("Process Details", getWidth() / 2 - 50, tableY - 10);


            int rowHeight = 20;
            int colWidth = getWidth() / 7;
            String[] headers = {"Name", "Arrival", "Burst", "Completion", "Turnaround", "Waiting"};

            int tableX = 50;
            int y = tableY;
            for (String header : headers) {
                g.drawRect(tableX, y, colWidth, rowHeight);
                g.drawString(header, tableX + 10, y + rowHeight - 5);
                tableX += colWidth;
            }


            y += rowHeight;
            for (Process process : processes) {
                tableX = 50;
                String[] data = {
                        process.name,
                        String.valueOf(process.arrivalTime),
                        String.valueOf(process.burstTime),
                        String.valueOf(process.completionTime),
                        String.valueOf(process.turnaroundTime),
                        String.valueOf(process.waitingTime)
                };

                for (String value : data) {
                    g.drawRect(tableX, y, colWidth, rowHeight);
                    g.drawString(value, tableX + 10, y + rowHeight - 5);
                    tableX += colWidth;
                }
                y += rowHeight;
            }
        }
    }



    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int n;
        System.out.print("Enter number of processes: ");
        try {
             n = sc.nextInt();
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a valid number.");
            return;
        }
        NonPreemptiveSRTF scheduler = new NonPreemptiveSRTF();

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

            Process process = new Process(name, new Color(r, g, b), arrivalTime, burstTime);
            process.remainingBurstTime = burstTime; // Initialize remaining burst time
            scheduler.addProcess(process);
        }

        scheduler.schedule();
        scheduler.printResults();
        scheduler.displayGraph();
    }
}
