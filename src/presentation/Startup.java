package presentation;

import application.OperationalSystem;

public class Startup {
    public static void main(String[] args) {
        var operationalSystem = new OperationalSystem("Windows", "11.0");

        operationalSystem.initialize();
    }
}
