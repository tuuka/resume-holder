package my.webapp.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import my.webapp.util.DateUtil;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
public class Organization implements Serializable {
    private static final long serialVersionUID = 1L;
    private Link homePage;

    public static Organization EMPTY = new Organization();
    //    private final Map<ContactType, String> contacts = new HashMap<>();
    private List<Position> positions;

    private Organization() {
    }

    public Organization(String name, String url, Position... positions) {
        this(new Link(name, url), Arrays.asList(positions));
    }

    public Organization(Link homePage, List<Position> positions) {
        this.homePage = homePage;
        this.positions = new ArrayList<>(positions);
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
        return "Organization{" +
                "homePage=" + homePage +
                ", positions=" + positions +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Organization)) return false;
        Organization that = (Organization) o;
        return Objects.equals(homePage, that.homePage) &&
                Objects.equals(positions, that.positions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(homePage, positions);
    }


    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Position implements Serializable{
        private static final long serialVersionUID = 1L;
        private String title, description;

        @JsonSerialize(converter = DateUtil.LocalDateToStringConverter.class)
        @JsonDeserialize(converter = DateUtil.StringToLocalDateConverter.class)
        @XmlJavaTypeAdapter(DateUtil.LocalDateJaxbAdapter.class)
        private LocalDate startDate, endDate;

        public Position() {
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
            this(DateUtil.parse(startDate), DateUtil.NOW, title, description);
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

        public Position(LocalDate startDate, LocalDate endDate,
                        String title, String description) {
            Objects.requireNonNull(startDate, "Start Date must not be null!");
            Objects.requireNonNull(endDate, "Finish Date must not be null!");
            Objects.requireNonNull(title, "Position title must not be null!");
            this.title = title;
            this.startDate = startDate.withDayOfMonth(1);
            this.endDate = endDate.withDayOfMonth(1);
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
