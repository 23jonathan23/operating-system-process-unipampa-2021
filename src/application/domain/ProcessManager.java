package application.domain;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ProcessManager {
    private LinkedList <Process> readyQueue;
    private LinkedList <Process> jobQueue;
    private LinkedList <Process> deviceQueue;
    
    public ProcessManager() {
        this.readyQueue = new LinkedList<>();
        this.jobQueue = new LinkedList<>();
        this.deviceQueue = new LinkedList<>();
    }

    public Process getCurrentProcessRunning() {
        var process = this.jobQueue
            .stream()
            .filter(p -> p.getState() == ProcessState.RUNNING)
            .findFirst();

        if(process.isPresent()) {
            return process.get();
        }

        return null;
    }

    public Process getReadyProcessByIndex(int index) {
        return this.readyQueue.get(index);
    }

    public int getLengthReadyQueue() {
        return this.readyQueue.size();
    }

    public void addProcess(Process process) {
        process.setState(ProcessState.READY);
        
        this.readyQueue.add(process);

        sortQueuesByPriority();
    }

    public void runNextProcessByPreemption() {
        var processReady = this.readyQueue.poll();
        
        this.jobQueue.add(processReady);

        var processRunning = getCurrentProcessRunning();

        if(processRunning != null) {
            if(processRunning.getPriority() > 1)
                processRunning.setPriority(processRunning.getPriority() - 1);
            
            processRunning.setState(ProcessState.READY);

            this.jobQueue.remove(processRunning);
            this.readyQueue.add(processRunning);
        }
        
        sortQueuesByPriority();
        
        var processToRun = this.jobQueue.getFirst();

        processToRun.setState(ProcessState.RUNNING);
    }

    public void runSpecificProcess(Process processToRun) {
        var processRunning = getCurrentProcessRunning();

        if(processRunning != null) {
            processRunning.setPriority(processRunning.getPriority() + 1);
            processRunning.setState(ProcessState.READY);

            this.jobQueue.remove(processRunning);
            this.readyQueue.add(processRunning);
        }

        this.readyQueue.remove(processToRun);
        
        processToRun.setState(ProcessState.RUNNING);

        this.jobQueue.add(processToRun);

        sortQueuesByPriority();
    }

    private void sortQueuesByPriority() {
        sortQueueByPriority(this.readyQueue);
        sortQueueByPriority(this.jobQueue);
        sortQueueByPriority(this.deviceQueue);
    }

    private void sortQueueByPriority(List<Process> queue) {
        Collections.sort(queue);
    }
}
