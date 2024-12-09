import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

class fcaiProcess {
    public String color;
    String name, id;
    int arrivalTime, burstTime, priority, remainingTime, quantum, fcaiFactor, turnAroundTime, waitingTime;
    int originalQuantum;
    boolean done;

    public fcaiProcess(String id, String name, int arrivalTime, int burstTime, int priority, int quantum, String color) {
        this.id = id;
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
        this.priority = priority;
        this.quantum = quantum;
        this.originalQuantum = quantum;
        this.color = color ; 
    }

    public void calcFcaiFactor(double v1, double v2) {
        this.fcaiFactor = (int) (Math.ceil(10 - priority) + Math.ceil(arrivalTime / v1) + Math.ceil(remainingTime / v2));
    }

    @Override
    public String toString() {
        return "fcaiProcess{" + name + ", Arrival Time=" + arrivalTime + ", Burst Time=" + burstTime + ", Priority=" + priority + ".}";
    }
}

class GUI {
    private final JFrame frame;
    private final JPanel chartPanel;
    private final JTextArea procssExecution;
    private final Map<Integer, JPanel> processLines;
    private final Map<Integer, Integer> processEndTimes;
    private final int processHeight = 30;
    private final int timeWidth = 25;

    public GUI(int numOfProcesses) {

        // Setup JFrame
        frame = new JFrame("FCAI Scheduling");
        frame.setSize(1000, 400);

        //GRAPH
        chartPanel = new JPanel();
        chartPanel.setLayout(new GridBagLayout());
        chartPanel.setBackground(Color.BLACK);
        JScrollPane chartScrollPane = new JScrollPane(chartPanel);
        frame.add(chartScrollPane, BorderLayout.CENTER);

        // process execution area (at the bottom)
        procssExecution = new JTextArea();
        procssExecution.setBackground(Color.BLACK);
        procssExecution.setForeground(Color.WHITE);
        JScrollPane statisticsScrollPane = new JScrollPane(procssExecution);
        frame.add(statisticsScrollPane, BorderLayout.SOUTH);

        // Initialize process lines and end times
        processLines = new HashMap<>();
        processEndTimes = new HashMap<>();

        // Create process rows
        for (int i = 1; i <= numOfProcesses; i++) {
            JPanel processLine = new JPanel();
            processLine.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));//row starting from left with no space
            processLine.setBackground(Color.BLACK);

            // Process name
            JLabel nameLabel = new JLabel("P" + i);
            nameLabel.setBackground(Color.BLACK);
            nameLabel.setForeground(Color.WHITE);//text
            nameLabel.setPreferredSize(new Dimension(50, processHeight));
            processLine.add(nameLabel);

            // Add process line to graph
            chartPanel.add(processLine, new GridBagConstraints(0, i - 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
            processLines.put(i, processLine);
        }

        frame.setVisible(true);
    }

    public void updateGUI(int startTime, fcaiProcess p, int execTime) {
        int pnumber = Integer.parseInt(p.id);
        JPanel processLine = processLines.get(pnumber);

        if (processLine != null) {
            // Calculate idle time (if there's a gap between last end time and the current start time)
            int lastEndTime = processEndTimes.getOrDefault(pnumber, 0);
            int idleTime = Math.max(0, startTime - lastEndTime);

            // Handle idle time
            if (idleTime > 0) {
                for (int i = 0; i < idleTime; i++) {
                    JLabel idleBlock = new JLabel();
                    idleBlock.setBackground(Color.BLACK);
                    idleBlock.setPreferredSize(new Dimension(timeWidth, processHeight));
                    processLine.add(idleBlock);
                }
            }

            // Handle execution block
            for (int i = 0; i < execTime; i++) {
                JLabel processBlock = new JLabel();
                processBlock.setOpaque(true);
                processBlock.setBackground(Color.decode(p.color));
                processBlock.setPreferredSize(new Dimension(timeWidth, processHeight));
                processLine.add(processBlock);
            }

            processEndTimes.put(pnumber, startTime + execTime);

            processLine.revalidate();
            processLine.repaint();
        }
    }

    public void updateProcessExec(String statistics) {
        procssExecution.setText(statistics);
    }
}


public class FCAIScheduler {
    List<fcaiProcess> processes;
    List<fcaiProcess> tempProcesses;
    double v1, v2, AvgTurnaroundTime = 0, AvgWaitingTime = 0;
    List<Vector<Integer>> changesQuantum = new ArrayList<>();
    private List<String> processOrder = new LinkedList<>();
    private GUI gui;

    FCAIScheduler(List<fcaiProcess> processes) {
        this.processes = processes;
        this.tempProcesses = new ArrayList<>(Collections.nCopies(processes.size(), null));
        this.v1 = 0;
        this.v2 = 0;
        for (fcaiProcess p : processes) {
            v1 = Math.max(v1, p.arrivalTime);
            v2 = Math.max(v2, p.burstTime);
        }
        v1 /= 10;
        v2 /= 10;
        for (int i = 0; i < processes.size(); i++) {
            changesQuantum.add(new Vector<>());
        }
        this.gui = new GUI(processes.size()); // Initialize the GUI
    }

    public void simulateFCAIFactor() {
        for (fcaiProcess p : processes) {
            p.calcFcaiFactor(v1, v2);
            changesQuantum.get(Integer.parseInt(p.id) - 1).add(p.quantum);
        }

        Deque<fcaiProcess> readyQueue = new LinkedList<>();
        int currentTime = 0;
        int done = 0;
        boolean completed = false;
        int processesSize = processes.size();
        String lastId = "";

        while (done < processesSize) {
            Iterator<fcaiProcess> it = processes.iterator();
            while (it.hasNext()) {
                fcaiProcess p = it.next();
                if (p.arrivalTime <= currentTime) {
                    readyQueue.add(p);
                    it.remove();
                }
            }
            if (readyQueue.isEmpty()) {
                currentTime++;
                continue;
            }

            int minFCAIFactor = (int) 1e9;
            fcaiProcess currentProcess = null;
            if (!completed) {
                for (fcaiProcess p : readyQueue) {
                    if (p.fcaiFactor < minFCAIFactor && (!p.id.equals(lastId) || readyQueue.size() == 1)) {
                        minFCAIFactor = p.fcaiFactor;
                        currentProcess = p;
                    }
                }
                if (currentProcess != null) {
                    readyQueue.remove(currentProcess);
                }
            } else {
                currentProcess = readyQueue.pollFirst();
                if (currentProcess != null) {
                    minFCAIFactor = currentProcess.fcaiFactor;
                }
            }

            lastId = currentProcess.id;
            processOrder.add(currentProcess.name);
            int unusedQuantum = currentProcess.quantum;
            int execTime = (int) Math.min(Math.ceil(0.4 * currentProcess.quantum), currentProcess.remainingTime);
            currentProcess.waitingTime = currentTime - currentProcess.arrivalTime - (currentProcess.burstTime - currentProcess.remainingTime);
            currentTime += execTime;
            unusedQuantum -= execTime;
            currentProcess.remainingTime -= execTime;

            boolean found = false;
            while (currentProcess.remainingTime > 0 && unusedQuantum > 0) {
                Iterator<fcaiProcess> it1 = processes.iterator();
                while (it1.hasNext()) {
                    fcaiProcess p = it1.next();
                    if (p.arrivalTime <= currentTime) {
                        readyQueue.add(p);
                        it1.remove();
                    }
                }
                for (fcaiProcess p : readyQueue) {
                    if (p.fcaiFactor < minFCAIFactor) {
                        found = true;
                        currentProcess.calcFcaiFactor(v1, v2);
                        if (currentProcess.remainingTime > 0)
                            readyQueue.add(currentProcess);
                        break;
                    }
                }
                if (found)
                    break;
                unusedQuantum--;
                currentProcess.remainingTime--;
                execTime++;
                currentTime++;
            }

            if (unusedQuantum > 0)
                currentProcess.quantum += unusedQuantum;
            else currentProcess.quantum += 2;

            currentProcess.turnAroundTime = currentTime - currentProcess.arrivalTime;
            if (currentProcess.remainingTime > 0) {
                changesQuantum.get(Integer.parseInt(currentProcess.id) - 1).add(currentProcess.quantum);
                if (!found) {
                    currentProcess.calcFcaiFactor(v1, v2);
                    readyQueue.add(currentProcess);
                    completed = true;
                } else completed = false;
            } else {
                AvgWaitingTime += currentTime - currentProcess.arrivalTime - (currentProcess.burstTime);
                AvgTurnaroundTime += currentTime - currentProcess.arrivalTime;
                tempProcesses.set(Integer.parseInt(currentProcess.id) - 1, currentProcess);
                completed = true;
                done++;
            }

            gui.updateGUI(currentTime - execTime, currentProcess, execTime);
        }

        StringBuilder statistics = new StringBuilder("Processes Order: " + processOrder.toString() + "\n\n");

        for (fcaiProcess p : tempProcesses) {
            if (p != null) {
                statistics.append("Process " + p.name + ": Turnaround Time= " + p.turnAroundTime +
                        ", Waiting Time= " + p.waitingTime + "\n");
            }
        }
        int processIndex = 1;
        for (Vector<Integer> quantumChanges : changesQuantum) {
            statistics.append( "Process " + processIndex++ + ": " + quantumChanges.toString() + "\n");
        }

        statistics.append("\nAverage Waiting Time: " + (AvgWaitingTime / tempProcesses.size()) + "\n" +
                "Average Turnaround Time: " + (AvgTurnaroundTime / tempProcesses.size()) + "\n");

        gui.updateProcessExec(statistics.toString());
    }

    public static void main(String[] args) {
        List<fcaiProcess> processes = new ArrayList<>(Arrays.asList(
                new fcaiProcess("1", "P1", 0, 17, 4, 4, "#6D1685"),//purple
                new fcaiProcess("2", "P2", 3, 6, 9, 3, "#8D6F64"),//tan
                new fcaiProcess("3", "P3", 4, 10, 3, 5, "#2A5519"),//green
                new fcaiProcess("4", "P4", 29, 4, 10, 2, "#EFC3CA")//rose
        ));

        FCAIScheduler scheduler = new FCAIScheduler(processes);
        scheduler.simulateFCAIFactor();
    }
}
