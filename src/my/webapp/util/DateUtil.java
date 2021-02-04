package my.webapp.util;

import com.fasterxml.jackson.databind.util.StdConverter;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    public static final LocalDate NOW = LocalDate.of(3000, 1, 1);

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MM/yyyy");

    public static LocalDate of(int year, Month month) {
        return LocalDate.of(year, month, 1);
    }

    public static String format(LocalDate date) {
        if (date == null) return "";
        return date.format(DATE_FORMATTER);
    }

    public static LocalDate parse(String date) {
        if (date == null || date.equals("")) date = format(NOW);
        YearMonth yearMonth = YearMonth.parse(date, DATE_FORMATTER);
        return LocalDate.of(yearMonth.getYear(),
                yearMonth.getMonth(), 1);
    }



/*  Converters to serialize/deserialize LocalDate into/from Jackson Json
    without JavaTimeModule but with field annotation :
     @JsonSerialize(converter = DateUtil.LocalDateToStringConverter.class)
     @JsonDeserialize(converter = DateUtil.StringToLocalDateConverter.class)*/

    public static class LocalDateToStringConverter extends StdConverter<LocalDate, String> {

        @Override
        public String convert(LocalDate value) {
            return value.format(DATE_FORMATTER);
        }
    }

    public static class StringToLocalDateConverter extends StdConverter<String, LocalDate> {

        @Override
        public LocalDate convert(String value) {
            return parse(value);
        }
    }


    public static class LocalDateJaxbAdapter extends XmlAdapter<String, LocalDate> {
        @Override
        public LocalDate unmarshal(String value) {
            return parse(value);
        }

        @Override
        public String marshal(LocalDate value) {
            return value.format(DATE_FORMATTER);
        }
    }

}
