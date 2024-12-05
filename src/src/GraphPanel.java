import javax.swing.*;
import java.awt.*;
import java.util.List;

class GraphPanel extends JPanel {
    private List<Process> processes;
    private List<String> executionOrder;

    public GraphPanel(List<Process> processes, List<String> executionOrder) {
        this.processes = processes;
        this.executionOrder = executionOrder;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int chartX = 50, chartY = 50;
        int barHeight = 50;
        int unitWidth = 50; 
        int tableY = 200;   
      
        g.setColor(Color.BLACK);
        g.drawString("Gantt Chart", getWidth() / 2 - 50, chartY - 10);

        int totalBurstTime = processes.stream().mapToInt(p -> p.burstTime).sum();

        int x = chartX;
        int currentTime = 0;

       
        g.setColor(Color.BLACK);
        g.drawString("0", x - 10, chartY + barHeight + 20);

        for (String processName : executionOrder) {
            Process current = processes.stream().filter(p -> p.name.equals(processName)).findFirst().orElse(null);
            if (current == null) continue;

           
            int barWidth = (int) (((double) current.burstTime / totalBurstTime) * (getWidth() - 100));

            
            g.setColor(current.color);
            g.fillRect(x, chartY, barWidth, barHeight);

           
            g.setColor(Color.BLACK);
            g.drawRect(x, chartY, barWidth, barHeight);
            g.drawString(current.name, x + barWidth / 2 - 10, chartY + barHeight / 2 + 5);

            
            currentTime += current.burstTime;
            x += barWidth;
            g.drawString(String.valueOf(currentTime), x - 10, chartY + barHeight + 20);
        }

      
        g.setColor(Color.BLACK);
        g.drawString("Process Details", getWidth() / 2 - 50, tableY - 10);

        
        int rowHeight = 20;
        int colWidth = getWidth() / 7; 
        String[] headers = {"Name", "Arrival", "Burst", "Priority", "Completion", "Turnaround", "Waiting"};

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
            y += rowHeight; 
        }
    }
}
