package vn.thanhvt.util;

import lombok.experimental.UtilityClass;

/**
 * @author pysga
 * @created 3/16/2022 - 1:03 AM
 * @project json-to-code-generator
 * @since 1.0
 **/
@UtilityClass
public class StringUtil {

    public  String camelToPascalCase(String str) {
        return str.substring(0, 1).toUpperCase()
            + str.substring(1);
    }

    public  String snakeToPascalCase(String str) {
        String[] parts = str.split("_");

        StringBuilder pascalString = new StringBuilder();

        for (String string : parts) {

            String temp = string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
            pascalString.append(temp);

        }
        return pascalString.toString();
    }
}
