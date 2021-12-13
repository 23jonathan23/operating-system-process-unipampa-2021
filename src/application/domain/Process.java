package application.domain;
import java.util.UUID;

public class Process implements Comparable<Process> {
    private UUID pid;
    private int priority;
    private ProcessState state;

    public Process(int priority) {
        this.pid = UUID.randomUUID();
        this.priority = priority;
        this.state = ProcessState.NEW;
    }

    public UUID getPid() {
        return pid;
    }

    public int getPriority() {
        return priority;
    }

    public ProcessState getState() {
        return state;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setState(ProcessState state) {
        this.state = state;
    }

    @Override
    public int compareTo(Process process) {
        if(process.getPriority() > this.priority) {
            return -1;
        } else if(process.getPriority() < this.priority) {
            return 1;
        }
        
        return 0;
    }
}