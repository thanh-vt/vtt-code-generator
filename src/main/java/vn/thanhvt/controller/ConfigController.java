package vn.thanhvt.controller;

import java.util.Arrays;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;
import vn.thanhvt.constant.JsonNamingStrategy;
import vn.thanhvt.service.ConfigService;

public class ConfigController {

    @FXML
    private ComboBox<String> dateFieldFormatComboBox;

    @FXML
    private CheckBox ignoreNullCheckbox;

    @FXML
    private ChoiceBox<JsonNamingStrategy> jsonFieldNamingChoiceBox;

    private final ConfigService configService = new ConfigService();

    @FXML
    private void initialize() {
        this.jsonFieldNamingChoiceBox.setConverter(new StringConverter<JsonNamingStrategy>() {

            @Override
            public String toString(JsonNamingStrategy object) {
                return object.getTitle();
            }

            @Override
            public JsonNamingStrategy fromString(String string) {
                return Arrays.stream(JsonNamingStrategy.values())
                    .filter(e -> e.getTitle().equals(string))
                    .findFirst()
                    .orElse(JsonNamingStrategy.CAMEL_CASE);
            }
        });
    }

}
