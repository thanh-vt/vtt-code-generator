package vn.thanhvt.model;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import vn.thanhvt.constant.AppConstant;
import vn.thanhvt.constant.JsonNamingStrategy;

/**
 * @author pysga
 * @created 3/16/2022 - 1:21 AM
 * @project json-to-code-generator
 * @since 1.0
 **/
@Data
@Builder
public class Setting {

    private final JsonNamingStrategy jsonNamingStrategy;

    private final String dateFormat;

    private final SimpleDateFormat cachedSdf;

    private List<SimpleDateFormat> sdfPool;

    private final boolean ignoreNull;

    public Setting getSetting(JsonNamingStrategy jsonNamingStrategy, boolean ignoreNull, Set<String> dfList) {
        this.sdfPool = new LinkedList<>();
        for (String df: dfList) {
            this.sdfPool.add(new SimpleDateFormat(df));
        }
        return Setting.builder()
            .jsonNamingStrategy(jsonNamingStrategy)
            .ignoreNull(ignoreNull)
            .build();
    }

    public Setting getDefaultSetting() {
        this.sdfPool = new LinkedList<>();
        for (String df: AppConstant.DEFAULT_DATE_FORMAT_LIST) {
            this.sdfPool.add(new SimpleDateFormat(df));
        }
        return Setting.builder()
            .jsonNamingStrategy(JsonNamingStrategy.SNAKE_CASE)
            .ignoreNull(true)
            .build();
    }

}
