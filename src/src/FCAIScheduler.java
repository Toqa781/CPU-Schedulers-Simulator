import javax.swing.*;
import java.awt.*;
import java.util.*;

class Process {
    String name;
    int arrivalTime, burstTime, remainingTime, priority, quantum;
    Color color;

    public Process(String name, int arrivalTime, int burstTime, int priority, int quantum, Color color) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
        this.priority = priority;
        this.quantum = quantum;
        this.color = color;
    }
}

public class FCAIScheduler extends JPanel {
    private ArrayList<Process> processes;
    private ArrayList<String> executionOrder;
    private ArrayList<Color> executionColors;
    private HashMap<String, Integer> processYPositions;
    private int time = 0;
    private int contextSwitchTime;

    public FCAIScheduler() {
        processes = new ArrayList<>();
        executionOrder = new ArrayList<>();
        executionColors = new ArrayList<>();
        processYPositions = new HashMap<>();
        setPreferredSize(new Dimension(1200, 500));
    }

    public void addProcess(String name, int arrivalTime, int burstTime, int priority, int quantum, Color color) {
        processes.add(new Process(name, arrivalTime, burstTime, priority, quantum, color));
        processYPositions.put(name, processYPositions.size() + 1);
    }

    public void setContextSwitchTime(int time) {
        this.contextSwitchTime = time;
    }

    public void scheduleProcesses() {
        ArrayList<Process> readyQueue = new ArrayList<>();
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime)); // Sort by arrival time
        int index = 0;

        while (index < processes.size() || !readyQueue.isEmpty()) {
            // Add processes to the queue based on their arrival time
            while (index < processes.size() && processes.get(index).arrivalTime <= time) {
                readyQueue.add(processes.get(index));
                index++;
            }

            if (!readyQueue.isEmpty()) {
                // Execute the first process in the queue
                Process currentProcess = readyQueue.remove(0);

                // Execute 40% of the quantum non-preemptively
                int executionTime = Math.min((int) Math.ceil(currentProcess.quantum * 0.4), currentProcess.remainingTime);
                currentProcess.remainingTime -= executionTime;

                // Add the process execution to the graph timeline
                for (int i = 0; i < executionTime; i++) {
                    executionOrder.add(currentProcess.name);
                    executionColors.add(currentProcess.color);
                }
                time += executionTime;

                // If process is not complete, add back to queue with updated quantum
                if (currentProcess.remainingTime > 0) {
                    currentProcess.quantum += 2; // Quantum increment
                    readyQueue.add(currentProcess); // Add back to the queue
                }

                // Add context switch time
                time += contextSwitchTime;

            } else {
                // Idle time if no process is ready
                executionOrder.add("IDLE");
                executionColors.add(Color.LIGHT_GRAY);
                time++;
            }
        }

        repaint(); // Trigger graphical representation
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw Axes
        g.setColor(Color.BLACK);
        g.drawLine(50, 50, 50, 400); // Y-axis
        g.drawLine(50, 400, 1150, 400); // X-axis

        // Label Axes
        g.drawString("Processes", 10, 30); // Y-axis label
        g.drawString("Time", 1120, 420); // X-axis label

        // Draw processes and execution timeline
        int xPosition = 60; // Start position on the X-axis
        int barHeight = 30;

        for (String processName : executionOrder) {
            int yPosition = processYPositions.getOrDefault(processName, 0) * barHeight + 50;

            if (processName.equals("IDLE")) {
                g.setColor(Color.LIGHT_GRAY);
            } else {
                Process process = processes.stream().filter(p -> p.name.equals(processName)).findFirst().orElse(null);
                if (process != null) {
                    g.setColor(process.color);
                }
            }

            g.fillRect(xPosition, yPosition, 20, barHeight - 10);
            g.setColor(Color.BLACK);
            g.drawRect(xPosition, yPosition, 20, barHeight - 10);
            xPosition += 20; // Increment time step
        }

        // Label process names on the Y-axis
        for (Process process : processes) {
            int yPosition = processYPositions.get(process.name) * barHeight + 50;
            g.drawString(process.name, 10, yPosition + (barHeight / 2));
        }
    }

    public static void main(String[] args) {
        FCAIScheduler scheduler = new FCAIScheduler();

        // User Input
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of processes: ");
        int numProcesses = sc.nextInt();

        System.out.print("Enter context switching time: ");
        int contextSwitchTime = sc.nextInt();
        scheduler.setContextSwitchTime(contextSwitchTime);

        for (int i = 0; i < numProcesses; i++) {
            System.out.println("Process " + (i + 1) + ": ");
            System.out.print("Name: ");
            String name = sc.next();
            System.out.print("Arrival Time: ");
            int arrivalTime = sc.nextInt();
            System.out.print("Burst Time: ");
            int burstTime = sc.nextInt();
            System.out.print("Priority: ");
            int priority = sc.nextInt();
            System.out.print("Initial Quantum: ");
            int quantum = sc.nextInt();

            Color color = new Color((int) (Math.random() * 0x1000000));
            scheduler.addProcess(name, arrivalTime, burstTime, priority, quantum, color);
        }

        JFrame frame = new JFrame("FCAI Scheduling Graph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(scheduler);
        frame.pack();
        frame.setVisible(true);

        // Run Scheduler
        scheduler.scheduleProcesses();
    }
}
