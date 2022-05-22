package vn.thanhvt.model;

import lombok.Builder;
import lombok.Data;
import vn.thanhvt.constant.AppConstant;
import vn.thanhvt.constant.JsonNamingStrategy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author pysga
 * @created 3/16/2022 - 1:21 AM
 * @project json-to-code-generator
 * @since 1.0
 **/
@Data
@Builder
public class DbRowsToCodeSetting {

    private final JsonNamingStrategy jsonNamingStrategy;

    private final String dateFormat;

    private SimpleDateFormat cachedSdf;

    private Map<String, SimpleDateFormat> sdfPool;

    private final boolean ignoreNull;

    public static DbRowsToCodeSetting from(JsonNamingStrategy jsonNamingStrategy, boolean ignoreNull, String dateFormat, Set<String> dfList) {
        Map<String, SimpleDateFormat> sdfPool = new LinkedHashMap<>();
        for (String df : dfList) {
            SimpleDateFormat sdf = new SimpleDateFormat(df);
            sdf.setLenient(false);
            sdfPool.put(df, sdf);
        }
        return DbRowsToCodeSetting.builder()
                .jsonNamingStrategy(jsonNamingStrategy)
                .ignoreNull(ignoreNull)
                .sdfPool(sdfPool)
                .dateFormat(dateFormat)
                .build();
    }

    public static DbRowsToCodeSetting getDefaultSetting() {
        Map<String, SimpleDateFormat> sdfPool = new LinkedHashMap<>();
        for (String df : AppConstant.DEFAULT_DATE_FORMAT_LIST) {
            SimpleDateFormat sdf = new SimpleDateFormat(df);
            sdf.setLenient(false);
            sdfPool.put(df, sdf);
        }
        return DbRowsToCodeSetting.builder()
                .jsonNamingStrategy(JsonNamingStrategy.SNAKE_CASE)
                .ignoreNull(true)
                .sdfPool(sdfPool)
                .build();
    }

    public Date parseDate(String inputTxt) throws ParseException {
        Iterator<SimpleDateFormat> sdfIterator = this.sdfPool.values().iterator();
        ParseException lastEx = null;
        if (this.cachedSdf != null) {
            try {
                return this.tryParse(inputTxt, sdfIterator.next());
            } catch (ParseException e) {
                lastEx = e;
            }
        }
        while (sdfIterator.hasNext()) {
            try {
                SimpleDateFormat nextSdf = sdfIterator.next();
                if (nextSdf == this.cachedSdf) continue;
                this.cachedSdf = nextSdf;
                return this.tryParse(inputTxt, this.cachedSdf);
            } catch (ParseException e) {
                lastEx = e;
            }
        }
        if (lastEx != null) throw lastEx;
        throw new RuntimeException("No formatters found");
    }

    private Date tryParse(String inputTxt, SimpleDateFormat sdf) throws ParseException {
        return sdf.parse(inputTxt);
    }

}
