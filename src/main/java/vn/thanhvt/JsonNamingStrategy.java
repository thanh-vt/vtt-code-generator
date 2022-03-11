package vn.thanhvt;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.function.Function;

public enum JsonNamingStrategy {
//    CAMEL_CASE("Camel case", Util::camelToPascalCase, DateTimeFormatter.ISO_OFFSET_DATE_TIME),
//    SNAKE_CASE("Snake case", Util::snakeToPascalCase, DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"))
//    ;
    CAMEL_CASE("Camel case", Util::camelToPascalCase, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")),
    SNAKE_CASE("Snake case", Util::snakeToPascalCase, new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"))
    ;

    private final String title;

    private final Function<String, String> convertFunction;

    private final SimpleDateFormat sdf;

    JsonNamingStrategy(String title, Function<String, String> convertFunction, SimpleDateFormat sdf) {
        this.title = title;
        this.convertFunction = convertFunction;
        this.sdf = sdf;
    }

    public String getTitle() {
        return title;
    }

    public Function<String, String> getConvertFunction() {
        return convertFunction;
    }

    public Date getDate(String input) throws ParseException {
//        TemporalAccessor accessor = this.sdf.parse(input);
//        return new Date(Instant.from(accessor).toEpochMilli());
        return this.sdf.parse(input);
    }

    public static void main(String[] args) throws ParseException {
        Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse("2022-03-10T07:23:29.000+00:00");
        System.out.println(date);
    }
}
