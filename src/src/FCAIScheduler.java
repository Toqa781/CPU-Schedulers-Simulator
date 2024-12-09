import java.util.*;

class Process {
    String name,id;
    int arrivalTime, burstTime, priority, remainingTime, quantum, fcaiFactor,turnAroundTime,waitingTime;
    int originalQuantum;
    boolean done;

    public Process(String id,String name, int arrivalTime, int burstTime, int priority, int quantum) {
        this.id=id;
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
        this.priority = priority;
        this.quantum = quantum;
        this.originalQuantum = quantum;
    }

    public void calcFcaiFactor(double v1, double v2) {
        this.fcaiFactor = (int)( Math.ceil(10 - priority) +Math.ceil (arrivalTime / v1) + Math.ceil(remainingTime / v2));
    }

    @Override
    public String toString(){
        return "Process{name= "+name+", Arrival Time="+arrivalTime+", Busrt Time="+burstTime+", Priority="+priority+".}";
    }
}

public class FCAIScheduler {
    List<Process>processes;
    List<Process>tempProcesses;
    double v1,v2, AvgTurnaroundTime=0, AvgWaitingTime=0;
    List<Vector<Integer>> changesQuantum=new ArrayList<>();
    private List<String>processOrder=new LinkedList<String>();
    FCAIScheduler(List<Process>processes){
        this.processes=processes;
        this.tempProcesses=new ArrayList<>(Collections.nCopies(processes.size(),null));
        this.v1=0;
        this.v2=0;
        for(Process p: processes){
            v1=Math.max(v1,p.arrivalTime);
            v2=Math.max(v2,p.burstTime);
        }
        v1/=10;
        v2/=10;
        for(int i=0;i<processes.size();i++){
            changesQuantum.add(new Vector<>());
        }
    }
    public void simulateFCAIFactor(){
        for(Process p: processes){
            p.calcFcaiFactor(v1,v2);
            changesQuantum.get(Integer.parseInt(p.id)-1).add(p.quantum);
        }
        Deque<Process>readyQueue=new LinkedList<>();
        int currentTime=0;
        int done=0;
        boolean completed=false;
        int processesSize=processes.size();
        String lastId="";
        while(done<processesSize){
            Iterator<Process>it=processes.iterator();
            while(it.hasNext()){
                Process p=it.next();
                if(p.arrivalTime<=currentTime){
                    readyQueue.add(p);
                    it.remove();
                }
            }
            if(readyQueue.isEmpty()){
                currentTime++;
                continue;
            }
            int minFCAIFactor=(int)1e9;
            Process currentProcess=null;
            if(!completed){
                for(Process p:readyQueue){
                    if(p.fcaiFactor<minFCAIFactor&&(!p.id.equals(lastId) || readyQueue.size()==1)){
                        minFCAIFactor=p.fcaiFactor;
                        currentProcess=p;
                    }
                }
                if(currentProcess!=null){
                    readyQueue.remove(currentProcess);
                }
            }
            else{
                currentProcess=readyQueue.pollFirst();
                if(currentProcess!=null){
                    minFCAIFactor=currentProcess.fcaiFactor;
                }
            }
            lastId=currentProcess.id;
            processOrder.add(currentProcess.name);
            int unusedQuantum=currentProcess.quantum;
            int execTime=(int) Math.min(Math.ceil(0.4*currentProcess.quantum),currentProcess.remainingTime);
            int timeBefore=currentTime;
            currentProcess.waitingTime=currentTime-currentProcess.arrivalTime-(currentProcess.burstTime-currentProcess.remainingTime);
            currentTime+=execTime;
            unusedQuantum-=execTime;
            currentProcess.remainingTime-=execTime;

            boolean found=false;
            while (currentProcess.remainingTime>0 &&unusedQuantum>0){
                Iterator<Process>it1=processes.iterator();
                while (it1.hasNext()){
                    Process p=it1.next();
                    if(p.arrivalTime<=currentTime){
                        readyQueue.add(p);
                        it1.remove();
                    }
                }
                for(Process p: readyQueue){
                    if(p.fcaiFactor<minFCAIFactor){
                        found=true;
                        currentProcess.calcFcaiFactor(v1,v2);
                        if(currentProcess.remainingTime>0)
                            readyQueue.add(currentProcess);
                        break;
                    }
                }
                if(found)
                    break;
                unusedQuantum--;
                currentProcess.remainingTime--;
                execTime++;
                currentTime++;
            }
            if(unusedQuantum>0)
                currentProcess.quantum+=unusedQuantum;
            else currentProcess.quantum+=2;
            currentProcess.turnAroundTime=currentTime-currentProcess.arrivalTime;
            if(currentProcess.remainingTime>0){
                changesQuantum.get(Integer.parseInt(currentProcess.id)-1).add(currentProcess.quantum);
                if(!found){
                    int oldFcai=currentProcess.fcaiFactor;
                    currentProcess.calcFcaiFactor(v1,v2);
                    readyQueue.add(currentProcess);
                    completed=true;
                }
                else completed=false;
            }
            else{
                AvgWaitingTime+=currentTime-currentProcess.arrivalTime-(currentProcess.burstTime);
                AvgTurnaroundTime+=currentTime-currentProcess.arrivalTime;
                tempProcesses.set(Integer.parseInt(currentProcess.id)-1,currentProcess);
                completed=true;
                System.out.println("Process name: " + currentProcess.id + " completed.");
                done++;
            }
        }
        System.out.println("Processes Order: "+processOrder.toString()+ "\n");

        for(Process p:tempProcesses){
            if(p!=null){
                System.out.println("Process "+p.name+": Turnaround Time= "+p.turnAroundTime+
                        ", Waiting Time= "+p.waitingTime);
            }
        }

        System.out.println("Average Waiting Time: "+(AvgWaitingTime/tempProcesses.size())+"\n"+
        "Average Turnarount Time: "+(AvgTurnaroundTime/tempProcesses.size())+"\n");

        int processIndex = 1;
        for (Vector<Integer> quantumChanges : changesQuantum) {
            System.out.println( "Process " + processIndex++ + ": " + quantumChanges.toString() + "\n");
        }
    }
    public static void main(String[] args) {
        List<Process> processes = new ArrayList<>(Arrays.asList(
                new Process("1", "P1", 0, 17, 4, 4),
                new Process("2", "P2", 3, 6, 9, 3),
                new Process("3", "P3", 4, 10, 3, 5),
                new Process("4", "P4", 29, 4, 8, 2)
        ));

        // Instantiate the scheduler with the process list
        FCAIScheduler scheduler = new FCAIScheduler(processes);

        // Simulate the FCAI scheduling algorithm
        scheduler.simulateFCAIFactor();
    }
}
