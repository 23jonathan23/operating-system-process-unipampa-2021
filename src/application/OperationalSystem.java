package application;

import application.domain.ProcessManager;
import application.domain.TypeInterruption;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import application.domain.Process;

public class OperationalSystem {
    private String name;
    private String version;
    private ProcessManager processManager;
    private Random random;
    private Timer executor;
    private int current_execution = 0;
    private final int MAX_PROCESS = 20;
    private final int EXECUTION_LIMIT = 20;
    private final int TIME_TO_CHANGE_PROCESS = 1000;
    private final int DELAY_TO_CHANGE_PROCESS = 50;

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
            createProcess(generateRamdonPriority());
        }

        var task = new TimerTask(){
            @Override
            public void run(){
                if(++current_execution > EXECUTION_LIMIT) {
                    executor.cancel();

                    System.out.println("Operating system " + name + " " + version +  " finished");

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
    }

    private void runSpecificProcess(Process process) {
        processManager.runSpecificProcess(process);

        showCurrentProcess();
    }

    private void runNextProcessByPreemption() {
        processManager.runNextProcessByPreemption();

        showCurrentProcess();
    }

    private void createProcess(int priority) {
        var process = new Process(priority);
        
        processManager.addProcess(process);
        
        System.out.println("Creating a new process with PID: " + process.getPid());
    }

    private void showCurrentProcess() {
        var process = processManager.getCurrentProcessRunning();

        if(process == null) return;

        System.out.println("Current process running PID: " + process.getPid());
    }

    private int generateRamdonPriority() {
        return random.nextInt(19) + 1;
    }

    private TypeInterruption getRandomInterruption() {
        var randomNumber = random.nextInt(19) + 1;
        
        if(randomNumber <= 8) {
            return TypeInterruption.PREEMPTION;
        }
        
        return TypeInterruption.OPERATIONAL_SYSTEM_CALL;
    }
}