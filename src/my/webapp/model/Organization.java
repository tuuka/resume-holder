package my.webapp.model;

import my.webapp.util.DateUtil;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

public class Organization implements Serializable {
    private static final long serialVersionUID = 1L;
    private Link homePage;

    public static Organization EMPTY = new Organization();
    //    private final Map<ContactType, String> contacts = new HashMap<>();
    private List<Position> positions = new ArrayList<>();

    private Organization() {
    }

    public Organization(String name, String url, Position... positions) {
        this(new Link(name, url), Arrays.asList(positions));
    }

    public Organization(Link homePage, List<Position> positions) {
        this.homePage = homePage;
        this.positions = positions;
    }

    public Link getHomePage() {
        return homePage;
    }

    public List<Position> getPositions() {
        return positions;
    }

    /**
     * Create Position object with {@code title}, {@code description},
     * {@code startDate} and {@code endDate} (String in format of
     * {@code MM/yyyy}).
     *
     * @param startDate   start date of Position
     * @param endDate  finish date of Position
     * @param title       position title
     * @param description position description
     * @throws NullPointerException if {@code startDate} or {@code endDate}
     *                              {@code title} is {@code null}
     */
    public void addPosition(String startDate, String endDate,
                            String title, String description) {
        Position p = new Position(startDate, endDate, title, description);
        if (!positions.contains(p)) positions.add(p);
    }

    @Override
    public String toString() {
        return String.format("\n\t\t\t'%s'" +
                        "\n\t\t\t\tcontacts:%s\n\t\t\t\tpositions:%s",
                homePage.getName(), homePage.getUrl(),
//                contacts.keySet().stream()
//                        .collect(StringBuilder::new,
//                            (sb,item)-> sb.append("\n\t\t\t\t\t")
//                                .append(item.returnContact(contacts.get(item))),
//                            StringBuilder::append).toString(),
                positions.stream()
                        .collect(StringBuilder::new,
                                (sb, item) -> sb.append("\n\t\t\t\t\t")
                                .append(item),
                                StringBuilder::append).toString()
        );
    }

    public static class Position implements Serializable{
        private String title, description;
        private LocalDate startDate, endDate;

        public Position() {
        }

        /**
         * Create Position object with {@code title}, {@code description},
         * {@code startDate} and {@code endDate} (String in format of
         * {@code MM/yyyy}).
         *
         * @param startDate   start date of Position
         * @param endDate  finish date of Position
         * @param title       position title
         * @param description position description
         * @throws NullPointerException if {@code startDate} or {@code endDate}
         *                              {@code title} is {@code null}
         */
        public Position(String startDate, String endDate,
                        String title, String description) {
            this(DateUtil.parse(startDate), DateUtil.parse(endDate), title, description);
        }

        /**
         * Create Position object with {@code title}, {@code description} and
         * {@code startDate} in format of {@code MM/yyyy}. {@code endDate} is
         * setting automatically
         *
         * @param startDate   start date of Position
         * @param title       position title
         * @param description position description
         * @throws NullPointerException if {@code startDate} or
         *                              {@code title} is {@code null}
         */
        public Position(String startDate,
                        String title, String description) {
            this(DateUtil.parse(startDate), null, title, description);
        }


        public Position(LocalDate startDate, LocalDate endDate,
                        String title, String description) {
            Objects.requireNonNull(startDate, "Start Date must not be null!");
            Objects.requireNonNull(endDate, "Finish Date must not be null!");
            Objects.requireNonNull(title, "Position title must not be null!");
            this.title = title;
            this.startDate = startDate;
            this.endDate = endDate;
            this.description = description == null ? "" : description;
        }


        public String getTitle() { return title; }

        public String getDescription() { return description; }

        public LocalDate getStartDate() { return startDate; }

        public LocalDate getEndDate() { return endDate; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Position)) return false;
            Position position = (Position) o;
            return title.equals(position.title) &&
                    Objects.equals(description, position.description) &&
                    startDate.equals(position.startDate) &&
                    endDate.equals(position.endDate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(title, description, startDate, endDate);
        }

        @Override
        public String toString() {
            return String.format("С %s по %s %15s (%s)", startDate, endDate, title, description);
        }
    }
}
