package application.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PrincipalMemory {
    private int sizeInKiloBytes;
    private int currentUsingSizeInKiloBytes = 0;
    private List<Page> pages;
    private int limitPagesInMemory = 20;

    public PrincipalMemory(int sizeInKiloBytes) {
        this.pages = new ArrayList<>();
        
        this.sizeInKiloBytes = sizeInKiloBytes;
    }

    public Page getPageById(UUID id) {
        var page = this.pages
            .stream()
            .filter(p -> p.getId().equals(id))
            .findFirst();

        if(page.isPresent()) {
            return page.get();
        }

        return null;
    }

    public Page getFirstPage() {
        return this.pages.get(0);
    }

    public void allocateMemory(int sizeInKiloBytes) {
        if(currentUsingSizeInKiloBytes < sizeInKiloBytes) 
            currentUsingSizeInKiloBytes += sizeInKiloBytes;
    }

    public void deallocateMemory(int sizeInKiloBytes) {
        if(currentUsingSizeInKiloBytes >= sizeInKiloBytes) 
            currentUsingSizeInKiloBytes -= sizeInKiloBytes;
    }

    public void addPage(Page page) {
        if(this.pages.size() < limitPagesInMemory) {
            this.pages.add(page);
            this.allocateMemory(Page.sizeInKiloBytes);
        }
    }

    public void removePage(Page page) {
        this.pages.remove(page);
        this.deallocateMemory(Page.sizeInKiloBytes);
    }

    public boolean pageIsInMemory(UUID pageId) {
        for (var page : pages) {
            if(page.getId().equals(pageId))
                return true;
        }

        return false;
    }

    public boolean memoryIsFull() {
        return this.currentUsingSizeInKiloBytes == this.limitPagesInMemory;
    }
}
