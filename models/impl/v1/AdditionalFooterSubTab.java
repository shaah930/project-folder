package dsm.foundation.core.models.impl.v1;

import java.util.List;


public class AdditionalFooterSubTab {
    private final String title;
    private final List<KhsFooterAdditionalItem> items;

    public AdditionalFooterSubTab(String title, List<KhsFooterAdditionalItem> items) {
        this.title = title;
        this.items = items;
    }

    public String getTitle() {
        return title;
    }

    public List<KhsFooterAdditionalItem> getItems() {
        return items;
    }
}
