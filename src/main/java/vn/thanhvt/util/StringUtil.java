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

    public String camelToPascalCase(String str) {
        return str.substring(0, 1).toUpperCase()
            + str.substring(1);
    }

    public String snakeToPascalCase(String str) {
        String[] parts = str.split("_");

        StringBuilder pascalString = new StringBuilder();

        for (String string : parts) {

            String temp = string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
            pascalString.append(temp);

        }
        return pascalString.toString();
    }

    public String pascalToKebabCase(String str) {
        return str.replaceAll("([a-z])([A-Z]+)", "$1-$2").toLowerCase();
//        StringBuilder res = new StringBuilder();
//        for (int i = 0; i < str.length(); i++) {
//            char ch = str.charAt(i);
//            if (i == 0) {
//                res.append(Character.toLowerCase(ch));
//                continue;
//            }
//            if (Character.isUpperCase(ch)) {
//                res.append("-").append(Character.toLowerCase(ch));
//            } else {
//                res.append(ch);
//            }
//        }
//        return res.toString();
    }

    public String removeEntityDtoSuffix(String classSimpleName) {
        String typescriptClassName = classSimpleName;
        String fieldNameLowerCase = typescriptClassName.toLowerCase();
        if (fieldNameLowerCase.endsWith("entity") && typescriptClassName.length() > 6) {
            typescriptClassName = typescriptClassName.substring(0, typescriptClassName.length() - 6);
        } else if (fieldNameLowerCase.endsWith("dto") && typescriptClassName.length() > 3) {
            typescriptClassName = typescriptClassName.substring(0, typescriptClassName.length() - 3);
        }
        return typescriptClassName;
    }

//    public static void main(String[] args) {
//        String a = "CommentDTO";
//        String b = pascalToKebabCase(a);
//        System.out.println(b);
//    }

}
