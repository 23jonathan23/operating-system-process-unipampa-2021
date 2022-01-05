package application.domain;

import java.util.UUID;

public class Page {
    private UUID id;
    public static int sizeInKiloBytes = 4;

    public Page() {
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }
}
