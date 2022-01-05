package application.domain;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Process implements Comparable<Process> {
    private UUID pid;
    private int priority;
    private ProcessState state;
    private TablePages tablePages;
    private int sizeInKiloBytes;
    private Random random;
    public static int limitPages = 40;

    public Process(int priority, int sizeInKiloBytes) {
        this.pid = UUID.randomUUID();
        this.priority = priority;
        this.state = ProcessState.NEW;
        this.sizeInKiloBytes = sizeInKiloBytes;
        this.tablePages = new TablePages(limitPages);
        this.random = new Random();
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

    public int getSize() {
        return sizeInKiloBytes;
    }

    public UUID getRandomPageId() {
        var indexRandom = random.nextInt(this.tablePages.getTotalPages());

        return this.tablePages.getPageId(indexRandom);
    }

    public List<UUID> getCurrentPagesId() {
        return this.tablePages.getPagesId();
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setState(ProcessState state) {
        this.state = state;
    }

    public void addPageId(UUID pageId) {
        this.tablePages.addPageId(pageId);
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