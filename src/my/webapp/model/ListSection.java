package my.webapp.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

//@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ListSection extends Section {
    private static final long serialVersionUID = 1L;
    private final List<String> items;
    public static final ListSection EMPTY = new ListSection();

    public ListSection(){
        this("");
    }

    public ListSection(String... items) { this(Arrays.asList(items)); }

    public ListSection(List<String> items) {
        Objects.requireNonNull(items, "items must not be null");
        this.items = new ArrayList<>(items);
    }

    public List<String> getItems() {
        return this.items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListSection that = (ListSection) o;
        return items.equals(that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(items);
    }

    @Override
    public String toString() {
        return items.toString();
    }
}
