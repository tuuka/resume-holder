package my.webapp.model;

import java.util.*;

public class OrganizationSection extends Section {
    private static final long serialVersionUID = 1L;
    public static final OrganizationSection EMPTY = new OrganizationSection();
    private final List<Organization> organizations = new ArrayList<>();

    public OrganizationSection() { }

    public OrganizationSection(Organization... organizations) {
        this(Arrays.asList(organizations));
    }

    public OrganizationSection(List<Organization> organizations) {
        Objects.requireNonNull(organizations, "organizations must not be null");
        this.organizations.addAll(organizations);
    }

    public List<Organization> getOrganizations() {
        return this.organizations;
    }

    public void addOrganization(Organization o) {
        organizations.add(o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrganizationSection that = (OrganizationSection) o;
        return organizations.equals(that.organizations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(organizations);
    }

//    @Override
//    public String toString() {
//        return organizations.stream()
//                .collect(StringBuilder::new,
//                        StringBuilder::append,
//                        StringBuilder::append).toString();
//    }

    @Override
    public String toString() {
        return "OrganizationSection{" +
                "organizations=" + organizations +
                '}';
    }
}
