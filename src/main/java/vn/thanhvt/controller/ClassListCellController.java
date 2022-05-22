package vn.thanhvt.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import lombok.Data;

/**
 * @author pysga
 * @created 5/21/2022 - 12:18 AM
 * @project json-to-code-generator
 * @since 1.0
 **/
@Data
public class ClassListCellController {

    @FXML
    private CheckBox classChecked;

    @FXML
    private Label className;

}
