package vn.thanhvt.custom;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author pysga
 * @created 5/21/2022 - 10:20 PM
 * @project json-to-code-generator
 * @since 1.0
 **/
@Data
@AllArgsConstructor
public class EntryClassifier {

    private Boolean isPackage;

    private String name;
}
