package application;

import application.domain.ProcessManager;
import application.domain.TypeInterruption;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import application.domain.Process;

public class OperationalSystem {
    private String name;
    private String version;
    private ProcessManager processManager;
    private Random random;
    private Timer executor;
    private int current_execution = 0;
    private final int MAX_PROCESS = 2;
    private final int EXECUTION_LIMIT = 30;
    private final int TIME_TO_CHANGE_PROCESS = 1300;
    private final int DELAY_TO_CHANGE_PROCESS = 70;

    public OperationalSystem(String name, String version) {
        this.name = name;
        this.version = version;
        this.random = new Random();
        this.executor = new Timer();
    }

    public void initialize() {
        System.out.println("Initializing " + name + " " + version);
        
        this.processManager = new ProcessManager();

        for(int i = 0; i < MAX_PROCESS; i++) {
            createProcess(generateRandomPriority(), generateRandomProcessSize());
        }

        var task = new TimerTask(){
            @Override
            public void run(){
                if(++current_execution > EXECUTION_LIMIT) {
                    executor.cancel();

                    System.out.println("\nOperating system " + name + " " + version +  " finished");

                    return;
                }

                runProcess();
            }
        };

        this.executor.scheduleAtFixedRate(task, DELAY_TO_CHANGE_PROCESS, TIME_TO_CHANGE_PROCESS);
    }

    private void runProcess() {
        var interruption = getRandomInterruption();

        if(interruption == TypeInterruption.PREEMPTION) {
            runNextProcessByPreemption();
        } else {
            var indexRandom = random.nextInt(processManager.getLengthReadyQueue());
            
            runSpecificProcess(processManager.getReadyProcessByIndex(indexRandom));
        }

        var currentProcess = processManager.getCurrentProcessRunning();

        var randomPageId = currentProcess.getRandomPageId();

        accessPageForProcessByPageId(currentProcess ,randomPageId);
    }

    private void accessPageForProcessByPageId(Process process, UUID pageId) {
        var pageFoundInPrincipalMemory = processManager.canAccessPageInPrincipalMemory(pageId);

        var message = pageFoundInPrincipalMemory
            ? "Page: " + pageId + " is in principal memory"
            : "Page: " + pageId + " isn't in principal memory";

        System.out.println(message);

        validateAlocatedPagesByProcess(process);

        validateIfPrincipalMemoryIsFull();

        processManager.loadPageFromSecondaryMemoryToPrincipal(pageId);
    }

    private void validateAlocatedPagesByProcess(Process process) {
        var pagesId = process.getCurrentPagesId();
        var numberOfPagesAlocated = 0;

        for(var pageId : pagesId) {
            var pageFoundInPrincipalMemory = processManager.canAccessPageInPrincipalMemory(pageId);

            if(pageFoundInPrincipalMemory)
                numberOfPagesAlocated++;
        }

        var message = numberOfPagesAlocated != 4
            ? "Process don't has 4 pages alocated in principal memory"
            : "Process has 4 pages alocated in principal memory";

        System.out.println(message);
    }

    private void validateIfPrincipalMemoryIsFull() {
        var principalMemoryIsFull = processManager.principalMemoryIsFull();

        var message = principalMemoryIsFull
            ? "Principal memory is full"
            : "Principal memory isn't full";

        System.out.println(message);
    }

    private void runSpecificProcess(Process process) {
        processManager.runSpecificProcess(process);

        showCurrentProcess();
    }

    private void runNextProcessByPreemption() {
        processManager.runNextProcessByPreemption();

        showCurrentProcess();
    }

    private void createProcess(int priority, int size) {
        var process = new Process(priority, size);
        
        processManager.addProcess(process);
        
        System.out.println("\nCreating a new process with PID: " + process.getPid());
    }

    private void showCurrentProcess() {
        var process = processManager.getCurrentProcessRunning();

        if(process == null) return;

        System.out.println("\nCurrent process running PID: " + process.getPid());
    }

    private int generateRandomPriority() {
        return random.nextInt(19) + 1;
    }

    private int generateRandomProcessSize() {
        return random.nextInt(200) + 1;
    }

    private TypeInterruption getRandomInterruption() {
        var randomNumber = random.nextInt(19) + 1;
        
        if(randomNumber <= 8) {
            return TypeInterruption.PREEMPTION;
        }
        
        return TypeInterruption.OPERATIONAL_SYSTEM_CALL;
    }
}