package my.webapp.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Organization implements Serializable {
    private static final long serialVersionUID = 1L;
    private Link homePage;

    public static Organization EMPTY = new Organization();
    //    private final Map<ContactType, String> contacts = new HashMap<>();
    private List<Position> positions = new ArrayList<>();

    public static DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static LocalDate convertStringToDate(String s) {
        return LocalDate.parse(s, dateFormatter);
    }

    public static String convertDateToString(LocalDate d) {
        return d.format(dateFormatter);
    }

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

    public void addPosition(Position position) {
        if (!positions.contains(position)) positions.add(position);
    }

    public void addPosition(String name, String startDate, String finishDate) {
        Position p = new Position(name, startDate, finishDate);
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
                                        .append(String.format("%-15s с %s по %s",
                                                item.getNameOfPosition(),
                                                item.getStartDateString(),
                                                item.getFinishDateString())),
                                StringBuilder::append).toString()
        );
    }

    public static class Position {
        private String nameOfPosition;
        private LocalDate startDate, finishDate;

        public Position(String nameOfPosition) {
            this.nameOfPosition = nameOfPosition;
        }

        public Position(String nameOfPosition,
                        String startDate,
                        String finishDate) {
            this.nameOfPosition = nameOfPosition;
            this.startDate = startDate == null ? null :
                    convertStringToDate(startDate);
            this.finishDate = finishDate == null ? null :
                    convertStringToDate(finishDate);
        }

        public String getNameOfPosition() {
            return nameOfPosition;
        }

        public void setNameOfPosition(String nameOfPosition) {
            this.nameOfPosition = nameOfPosition;
        }

        public void setStartDate(LocalDate startDate) {
            this.startDate = startDate;
        }

        public void setStartDateFromString(String startDate) {
            this.startDate = convertStringToDate(startDate);
        }

        public String getStartDateString() {
            return convertDateToString(startDate);
        }

        public String getFinishDateString() {
            return convertDateToString(finishDate);
        }

        public void setFinishDate(LocalDate finishDate) {
            this.finishDate = finishDate;
        }

        public void setFinishDateFromString(String finishDate) {
            this.finishDate = convertStringToDate(finishDate);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Position)) return false;
            Position position = (Position) o;
            return getNameOfPosition().equals(position.getNameOfPosition()) &&
                    startDate.equals(position.startDate) &&
                    finishDate.equals(position.finishDate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(getNameOfPosition(), startDate, finishDate);
        }

        @Override
        public String toString() {
            return "Position{" +
                    "name='" + nameOfPosition + '\'' +
                    ", startDate=" + getStartDateString() +
                    ", finishDate=" + getFinishDateString() +
                    '}';
        }
    }
}
