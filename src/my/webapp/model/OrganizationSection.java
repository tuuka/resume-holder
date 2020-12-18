package my.webapp.model;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class OrganizationSection extends Section {
    private static final long serialVersionUID = 1L;
    private final List<Organization> items;

    public OrganizationSection(List<Organization> items) {
        Objects.requireNonNull(items, "organizations must not be null");
        this.items = items;
    }

    public OrganizationSection(Organization... items) {
        this(Arrays.asList(items));
    }

    public List<Organization> getOrganizations() {
        return this.items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrganizationSection that = (OrganizationSection) o;
        return items.equals(that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(items);
    }

    @Override
    public String toString() {
        return items.stream()
                .collect(StringBuilder::new,
                        StringBuilder::append,
                        StringBuilder::append).toString();
    }
}
