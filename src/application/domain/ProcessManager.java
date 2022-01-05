package application.domain;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class ProcessManager {
    private LinkedList <Process> readyQueue;
    private LinkedList <Process> jobQueue;
    private LinkedList <Process> deviceQueue;
    private PrincipalMemory principalMemory;
    private SecondaryMemory secondaryMemory;
    
    public ProcessManager() {
        this.readyQueue = new LinkedList<>();
        this.jobQueue = new LinkedList<>();
        this.deviceQueue = new LinkedList<>();
        this.principalMemory = new PrincipalMemory(1024);
        this.secondaryMemory = new SecondaryMemory();
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

        createPagesForProcess(process);
    }

    private void createPagesForProcess(Process process) {
        var numberOfPages = process.getSize() / Page.sizeInKiloBytes;

        numberOfPages = numberOfPages > Process.limitPages ? Process.limitPages : numberOfPages;

        for (int i = 0; i < numberOfPages; i++) {
            var page = new Page();

            secondaryMemory.addPage(page);
            process.addPageId(page.getId());
        }
    }

    public boolean canAccessPageInPrincipalMemory(UUID pageId) {
        return principalMemory.pageIsInMemory(pageId);
    }

    public void loadPageFromSecondaryMemoryToPrincipal(UUID pageId) {
        var page = secondaryMemory.getPageById(pageId);

        if(page != null) {
            if(principalMemory.memoryIsFull()) {
                var firstPageInMemory = principalMemory.getFirstPage();

                principalMemory.removePage(firstPageInMemory);
                secondaryMemory.addPage(page);
            }

            secondaryMemory.removePage(page);
            principalMemory.addPage(page);
        }
    }

    public boolean principalMemoryIsFull() {
        return principalMemory.memoryIsFull();
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

            principalMemory.deallocateMemory(processRunning.getSize());
        }
        
        sortQueuesByPriority();
        
        var processToRun = this.jobQueue.getFirst();

        processToRun.setState(ProcessState.RUNNING);

        principalMemory.allocateMemory(processToRun.getSize());
    }

    public void runSpecificProcess(Process processToRun) {
        var processRunning = getCurrentProcessRunning();

        if(processRunning != null) {
            processRunning.setPriority(processRunning.getPriority() + 1);
            processRunning.setState(ProcessState.READY);

            this.jobQueue.remove(processRunning);
            this.readyQueue.add(processRunning);

            principalMemory.deallocateMemory(processRunning.getSize());
        }

        this.readyQueue.remove(processToRun);
        
        processToRun.setState(ProcessState.RUNNING);

        this.jobQueue.add(processToRun);

        principalMemory.allocateMemory(processToRun.getSize());

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
