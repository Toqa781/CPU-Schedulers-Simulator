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
        boolean isFirstProcess = true;

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

            // Calculate the start and end times including context switch
            if (!isFirstProcess) {
                currentTime += contextSwitchTime; // Account for context switch
            }

            currentProcess.startTime = currentTime;
            executionOrder.add(currentProcess.name);
            currentTime += currentProcess.burstTime;
            currentProcess.endTime = currentTime;

            currentProcess.completionTime = currentTime;
            currentProcess.turnaroundTime = currentProcess.completionTime - currentProcess.arrivalTime;
            currentProcess.waitingTime = currentProcess.turnaroundTime - currentProcess.burstTime;
            currentProcess.isCompleted = true;

            isFirstProcess = false;
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
        frame.add(new GraphPanel(processes, executionOrder, contextSwitchTime));
        frame.setVisible(true);
    }
    class GraphPanel extends JPanel {
        private List<Process> processes;
        private List<String> executionOrder;
        private int contextSwitchTime;

        public GraphPanel(List<Process> processes, List<String> executionOrder, int contextSwitchTime) {
            this.processes = processes;
            this.executionOrder = executionOrder;
            this.contextSwitchTime = contextSwitchTime;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            int chartX = 50, chartY = 50;
            int barHeight = 50;
            int unitWidth = 10; // Width for 1 unit of time (scaled for better visibility)
            int tableY = 250;   // Starting Y-coordinate for the table

            // Draw Gantt Chart Title
            g.setColor(Color.BLACK);
            g.drawString("Gantt Chart", getWidth() / 2 - 50, chartY - 10);

            // Draw Gantt Chart
            int x = chartX;
            int currentTime = 0;

            // Draw the initial "0" time marker
            g.setColor(Color.BLACK);
            g.drawString("0", x - 10, chartY + barHeight + 20);

            // Process each process from the execution order
            for (String processName : executionOrder) {
                Process current = processes.stream().filter(p -> p.name.equals(processName)).findFirst().orElse(null);
                if (current == null) continue;

                // Calculate bar width based on completion time minus current time
                int barWidth = (current.completionTime - currentTime) * unitWidth;

                // Draw process bar
                g.setColor(current.color);
                g.fillRect(x, chartY, barWidth, barHeight);

                // Draw border and label
                g.setColor(Color.BLACK);
                g.drawRect(x, chartY, barWidth, barHeight);
                g.drawString(current.name, x + barWidth / 2 - 10, chartY + barHeight / 2 + 5);

                // Advance x position and update time markers
                currentTime = current.completionTime;
                x += barWidth;

                // Draw time markers for process completion
                g.drawString(String.valueOf(currentTime), x - 10, chartY + barHeight + 20);

                // If not the last process, account for context switch time
                if (executionOrder.indexOf(processName) < executionOrder.size() - 1) {
                    x += contextSwitchTime * unitWidth; // Add context switch gap between processes
                    g.drawString("+" + contextSwitchTime, x - 10, chartY + barHeight + 20); // Display context switch time
                }
            }

            // Draw Table Title
            g.setColor(Color.BLACK);
            g.drawString("Process Details", getWidth() / 2 - 50, tableY - 10);

            // Draw Table Headers
            int rowHeight = 20;
            int colWidth = getWidth() / 7; // Seven columns: Name, Arrival, Burst, Priority, Completion, Turnaround, Waiting
            String[] headers = {"Name", "Arrival", "Burst", "Priority", "Completion", "Turnaround", "Waiting"};

            int tableX = 50;
            int y = tableY;
            for (String header : headers) {
                g.drawRect(tableX, y, colWidth, rowHeight);
                g.drawString(header, tableX + 10, y + rowHeight - 5);
                tableX += colWidth;
            }

            // Draw Table Data
            y += rowHeight; // Move to the next row for data
            for (Process process : processes) {
                tableX = 50; // Reset x position
                String[] data = {
                        process.name,
                        String.valueOf(process.arrivalTime),
                        String.valueOf(process.burstTime),
                        String.valueOf(process.priority),
                        String.valueOf(process.completionTime),
                        String.valueOf(process.turnaroundTime),
                        String.valueOf(process.waitingTime)
                };

                for (String value : data) {
                    g.drawRect(tableX, y, colWidth, rowHeight);
                    g.drawString(value, tableX + 10, y + rowHeight - 5);
                    tableX += colWidth;
                }
                y += rowHeight; // Move to the next row for the next process
            }
        }
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
