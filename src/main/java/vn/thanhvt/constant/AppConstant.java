package vn.thanhvt.constant;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author pysga
 * @created 3/16/2022 - 2:04 AM
 * @project json-to-code-generator
 * @since 1.0
 **/
@UtilityClass
public class AppConstant {

    public List<String> DEFAULT_DATE_FORMAT_LIST;

    static {
        List<String> defaultDfList = Arrays.asList(
                "yyyy-MM-dd'T'HH:mm:ssXXX",
                "yyyy-MM-dd'T'HH:mm:ss.S",
                "yyyy-MM-dd HH:mm:ss",
                "dd-MM-yyyy HH:mm:ss",
                "dd-MM-yyyy"
        );
        DEFAULT_DATE_FORMAT_LIST = Collections.unmodifiableList(defaultDfList);
    }
}
