package vn.thanhvt;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.function.Function;

public enum JsonNamingStrategy {
    CAMEL_CASE("Camel case", Util::camelToPascalCase, DateTimeFormatter.ISO_OFFSET_DATE_TIME),
    SNAKE_CASE("Snake case", Util::snakeToPascalCase, DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"))
    ;

    private final String title;

    private final Function<String, String> convertFunction;

    private final DateTimeFormatter dtf;

    JsonNamingStrategy(String title, Function<String, String> convertFunction, DateTimeFormatter dtf) {
        this.title = title;
        this.convertFunction = convertFunction;
        this.dtf = dtf;
    }

    public String getTitle() {
        return title;
    }

    public Function<String, String> getConvertFunction() {
        return convertFunction;
    }

    public Date getDate(String input) {
        TemporalAccessor accessor = this.dtf.parse(input);
        return new Date(Instant.from(accessor).toEpochMilli());
    }
}
