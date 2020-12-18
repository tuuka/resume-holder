package my.webapp.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Organization implements Serializable {
    private static final long serialVersionUID = 1L;

    public static SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd/MM/yyyy");

    public static Date convertStringToDate(String s){
        try {
            return dateFormat.parse(s);
        } catch (ParseException e) {
            System.out.printf("Date string must be in %s format!!\n",
                    dateFormat);
            e.printStackTrace();
        }
        return null;
    }

    public static String convertDateToString(Date d){
        return dateFormat.format(d);
    }


    public static Organization EMPTY = new Organization();
    private String organizationName;
    private final Map<ContactType, String> contacts = new HashMap<>();
    private final List<Position> positions = new ArrayList<>();

    private Organization(){ }

    public Organization(String organizationName){
        this.organizationName = organizationName;
    }

    public Organization(String organizationName, Position... positions){
        this.organizationName = organizationName;
        this.positions.addAll(Arrays.asList(positions));
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public Map<ContactType, String> getContacts() {
        return contacts;
    }

    public void setContact(ContactType contactType, String contact) {
        this.contacts.put(contactType, contact);
    }

    public String getContact(ContactType contactType) {
        return this.contacts.get(contactType);
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Organization)) return false;
        Organization that = (Organization) o;
        return getOrganizationName().equals(that.getOrganizationName()) &&
                getContacts().equals(that.getContacts()) &&
                getPositions().equals(that.getPositions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOrganizationName(), getContacts(), getPositions());
    }

    @Override
    public String toString() {
        return String.format("\n\t\t\t'%s'" +
                        "\n\t\t\t\tcontacts:%s\n\t\t\t\tpositions:%s",
                organizationName,
                contacts.keySet().stream()
                        .collect(StringBuilder::new,
                            (sb,item)-> sb.append("\n\t\t\t\t\t")
                                .append(item.returnContact(contacts.get(item))),
                            StringBuilder::append).toString(),
                positions.stream()
                        .collect(StringBuilder::new,
                            (sb,item)-> sb.append("\n\t\t\t\t\t")
                                    .append(String.format("%-15s с %s по %s",
                                            item.getNameOfPosition(),
                                            item.getStartDateString(),
                                            item.getFinishDateString())),
                            StringBuilder::append).toString()

        );
    }

    public static class Position{
        private String nameOfPosition;
        private Date startDate, finishDate;

        public Position(String nameOfPosition){
            this.nameOfPosition = nameOfPosition;
        }

        public Position(String nameOfPosition,
                        String startDate,
                        String finishDate){
            this.nameOfPosition = nameOfPosition;
            this.startDate = startDate == null? null:
                    convertStringToDate(startDate);
            this.finishDate = finishDate == null? null:
                    convertStringToDate(finishDate);        }

        public String getNameOfPosition() {
            return nameOfPosition;
        }

        public void setNameOfPosition(String nameOfPosition) {
            this.nameOfPosition = nameOfPosition;
        }

        public void setStartDate(Date startDate) {
            this.startDate = startDate;
        }

        public void setStartDateFromString(String startDate) {
                this.startDate = convertStringToDate(startDate);
        }

        public String getStartDateString() {
            return convertDateToString(startDate);
        }

        public void setFinishDate(Date finishDate) {
            this.finishDate = finishDate;
        }

        public void setFinishDateFromString(String finishDate) {
                this.finishDate = convertStringToDate(finishDate);
        }

        public String getFinishDateString() {
            return convertDateToString(finishDate);
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
