package vn.thanhvt.constant;

import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Getter;
import vn.thanhvt.util.StringUtil;

@Getter
@AllArgsConstructor
public enum JsonNamingStrategy {
    CAMEL_CASE("Camel case", StringUtil::camelToPascalCase),
    SNAKE_CASE("Snake case", StringUtil::snakeToPascalCase)
    ;

    private final String title;

    private final Function<String, String> convertFunction;

}
