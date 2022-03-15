package vn.thanhvt.constant;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.experimental.UtilityClass;

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
            "yyy-MM-dd'T'HH:mm:ssXXX",
            "yyyy-MM-dd'T'HH:mm:ss.S",
            "dd-MM-yyyy HH:mm:ss",
            "dd-MM-yyyy"
        );
        DEFAULT_DATE_FORMAT_LIST = Collections.unmodifiableList(defaultDfList);
    }
}
