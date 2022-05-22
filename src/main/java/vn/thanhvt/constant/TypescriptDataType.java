package vn.thanhvt.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author pysga
 * @created 5/21/2022 - 11:43 PM
 * @project json-to-code-generator
 * @since 1.0
 **/
@AllArgsConstructor
public enum TypescriptDataType {
    STRING("string"),
    NUMBER("number"),
    BOOLEAN("boolean"),
    DATE("Date"),
    NESTED(null),
    ANY("any"),
    UNKNOWN("unknown"),
    NEVER("never"),
    COLLECTION(null),
    ENUM(null)
    ;

    @Getter
    private final String declaration;
}
