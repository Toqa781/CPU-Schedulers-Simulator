
# CPU-Schedulers-Simulator

## Overview

This project is a Java-based simulation designed to model and analyze various CPU scheduling algorithms. CPU scheduling is a fundamental concept in operating systems, crucial for managing processes effectively and optimizing system performance. This simulator provides a visual and interactive way to understand how different scheduling algorithms impact process execution order, waiting time, turnaround time, and overall CPU utilization. The simulation includes implementations of both traditional and an adaptive scheduling algorithm, allowing for comparative analysis and experimentation.

## Features

This simulator supports the following CPU scheduling algorithms:

1.  **Non-preemptive Priority Scheduling:** Processes are executed based on their assigned priority. Once a process starts executing, it runs to completion, and context switching occurs only when the current process finishes.
2.  **Non-preemptive Shortest Job First (SJF):** Processes with the shortest burst time are executed first. This algorithm aims to minimize the average waiting time. The implemented version includes a mechanism to mitigate starvation issues.
3.  **Shortest Remaining Time First (SRTF):** A preemptive version of SJF. The process with the smallest remaining time is executed. If a new process arrives with a shorter remaining time than the currently executing process, the current process is preempted. Includes a solution to address starvation.
4.  **FCAI Scheduling (an adaptive algorithm):** This is a custom scheduling algorithm that combines several factors to optimize CPU utilization and fairness.
    *   Combines priority, arrival time, and remaining burst time into a composite FCAI factor.
    *   Features dynamic quantum allocation.
    *   Addresses inefficiencies and starvation issues of traditional algorithms

## Project Goals

*   Provide a clear and visual representation of CPU scheduling algorithms.
*   Enable users to compare the performance of different algorithms under various scenarios.
*   Offer an adaptive scheduling algorithm (FCAI) that addresses the limitations of traditional methods.
*   Serve as an educational tool for understanding operating system concepts.

## Requirements

To run this project, you need the following:

*   **Java Development Kit (JDK):** Make sure you have JDK 8 or later installed. You can download it from [Oracle's website](https://www.oracle.com/java/technologies/javase-downloads.html) or use an open-source distribution like [OpenJDK](https://openjdk.java.net/).
*   **Java Swing:** This project uses Java Swing for the graphical user interface. Swing is included with the JDK, so no additional installation is required.
*   **IDE (Optional):** An Integrated Development Environment like IntelliJ IDEA, Eclipse, or NetBeans can be helpful for development and running the project.

## Setup Instructions

1.  **Clone the repository:**

    bash
    javac src/*.java
    3.  **Run the simulation:**

    Execute the main class to start the simulation:

    The simulator allows you to configure various parameters to observe the behavior of different scheduling algorithms.

1.  **Adding Processes:** You can add processes to the simulation by specifying their arrival time, burst time, and priority (if applicable).
2.  **Selecting an Algorithm:** Choose the desired scheduling algorithm from the available options in the GUI.
3.  **Running the Simulation:** Click the "Start" button to begin the simulation. The simulator will visualize the execution of processes according to the selected algorithm.
4.  **Analyzing Results:** The simulator displays key metrics such as waiting time, turnaround time, and CPU utilization for each process and the overall system.

Example scenario:

*   Add three processes:
    *   Process A: Arrival Time = 0, Burst Time = 8, Priority = 2
    *   Process B: Arrival Time = 1, Burst Time = 4, Priority = 1
    *   Process C: Arrival Time = 2, Burst Time = 9, Priority = 3
*   Select "Non-preemptive Priority Scheduling".
*   Run the simulation and observe the Gantt chart and performance metrics.

## Project Structure

The project structure is organized as follows:


CPU-Schedulers-Simulator/
├── src/                      # Source code
│   ├── Algorithm.java          # Abstract class for scheduling algorithms
│   ├── FCFS.java               # First-Come, First-Served algorithm
│   ├── SJF.java                # Shortest Job First algorithm
│   ├── Priority.java           # Priority Scheduling algorithm
│   ├── SRTF.java               # Shortest Remaining Time First algorithm
│   ├── FCAI.java               # FCAI Scheduling algorithm
│   ├── Process.java            # Process class
│   ├── GUI.java                # Graphical User Interface
│   └── Main.java               # Main class to run the simulation
├── README.md               # Documentation
└── LICENSE                 # License information
1.  **Fork the repository:** Create your own fork of the repository on GitHub.
2.  **Create a branch:** Create a new branch for your feature or bug fix.
3.  **Make changes:** Implement your changes, ensuring that the code is well-documented and follows the project's coding conventions.
4.  **Test your changes:** Thoroughly test your changes to ensure they work as expected.
5.  **Submit a pull request:** Submit a pull request to the main repository, explaining the changes you've made and the reasons for them.

> Make sure to follow the coding style and conventions used in the project. Provide clear and concise commit messages.

## Acknowledgments

This project is part of the CS341 - Operating Systems 1 course at the Faculty of Computers & Artificial Intelligence, Cairo University. We would like to thank the instructors and teaching assistants for their guidance and support.

## Contributors

1.  Habiba Ayman
2.  Toqa Abdalla
3.  Kermina Nashaat
4.  Mohamed Aber

## License

This project is for educational purposes only and is not licensed for commercial use.
