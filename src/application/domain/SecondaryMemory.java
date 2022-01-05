package application.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SecondaryMemory {
    private List<Page> pages;
    
    public SecondaryMemory() {
        this.pages = new ArrayList<>();
    }

    public void addPage(Page page) {
        this.pages.add(page);
    }

    public void removePage(Page page) {
        this.pages.remove(page);
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
}
