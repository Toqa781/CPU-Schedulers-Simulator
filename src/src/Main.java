import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.print("Welcome to the Process Scheduler Simulator!\n");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the scheduler you want to use: ");
        System.out.println("1. Non-Preemptive SJF");
        System.out.println("2. Non-Preemptive SRTF");
        System.out.println("3. Non-Preemptive Priority");
        System.out.println("4. FCAIScheduler");
        int choice = scanner.nextInt();
        switch (choice) {
            case 1:
                System.out.print("Enter the number of processes: ");
                int n = scanner.nextInt();
                NonPreemptiveSJF scheduler = new NonPreemptiveSJF();
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

                scheduler.schedule();
                scheduler.printResults();
                scheduler.displayGraph();
                break;
            case 2:
                System.out.print("Enter 1 to use console input or 2 to read from file: ");
                int SRTFchoice = scanner.nextInt();

                NonPreemptiveSRTF SRTFscheduler;
                if (SRTFchoice == 2) {
                    String fileName = "input.txt";
                    SRTFscheduler = NonPreemptiveSRTF.readFromFile(fileName);
                } else {
                    SRTFscheduler = NonPreemptiveSRTF.readFromConsole(scanner);
                }
                SRTFscheduler.schedule();
                SRTFscheduler.printResults();
                SRTFscheduler.displayGraph();

                break;
            case 3:
                System.out.print("Enter the number of processes: ");
                int numberOfProccess = scanner.nextInt();
                Scanner sc = new Scanner(System.in);

                System.out.print("Enter context switching time: ");
                int contextSwitchTime = sc.nextInt();

                NonPreemptivePriorityScheduler scheduler2 = new NonPreemptivePriorityScheduler(contextSwitchTime);

                for (int i = 0; i < numberOfProccess; i++) {
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

                    scheduler2.addProcess(new Process(name, new Color(r, g, b), arrivalTime, burstTime, priority));
                }

                scheduler2.schedule();
                scheduler2.printResults();
                scheduler2.displayGraph();
                break;
            case 4:
                List<fcaiProcess> processes = new ArrayList<>(Arrays.asList(
                        new fcaiProcess("1", "P1", 0, 17, 4, 4, "#6D1685"),//purple
                        new fcaiProcess("2", "P2", 3, 6, 9, 3, "#8D6F64"),//tan
                        new fcaiProcess("3", "P3", 4, 10, 3, 5, "#2A5519"),//green
                        new fcaiProcess("4", "P4", 29, 4, 10, 2, "#EFC3CA")//rose
                ));
                System.out.println("Scheduling Completed.");

                FCAIScheduler scheduler3 = new FCAIScheduler(processes);
                scheduler3.simulateFCAIFactor();
                break;
            default:
                System.out.println("Invalid choice");
        }
    }
}
