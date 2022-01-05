package application.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TablePages {
    private int limitPages;
    private List<UUID> pageIds;

    public TablePages(int limitPages) {
        this.pageIds = new ArrayList<>();
        this.limitPages = limitPages;
    }

    public void addPageId(UUID pageId) {
        if(pageIds.size() < limitPages) {
            pageIds.add(pageId);
        }
    }

    public UUID getPageId(int index) {
        return pageIds.get(index);
    }

    public int getTotalPages() {
        return pageIds.size();
    }

    public List<UUID> getPagesId() {
        return pageIds;
    }
}
