package vn.thanhvt.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import lombok.Setter;
import vn.thanhvt.constant.AppConstant;
import vn.thanhvt.constant.JsonNamingStrategy;
import vn.thanhvt.model.DbRowsToCodeSetting;
import vn.thanhvt.util.UiUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

public class ConfigController {

    @FXML
    private DialogPane dialogPane;

    @FXML
    private ComboBox<String> dateFieldFormatComboBox;

    @FXML
    private CheckBox ignoreNullCheckbox;

    @FXML
    private ChoiceBox<JsonNamingStrategy> jsonFieldNamingChoiceBox;

    private DbRowsToCodeSetting currentDbRowsToCodeSetting;

    @Setter
    private BiConsumer<ButtonBar.ButtonData, DbRowsToCodeSetting> onActionHandler;

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
        this.jsonFieldNamingChoiceBox.setItems(FXCollections.observableArrayList(JsonNamingStrategy.values()));
        this.resetDefault();
        final Button okBtn = (Button) this.dialogPane.lookupButton(ButtonType.OK);
        okBtn.addEventFilter(ActionEvent.ACTION, event -> {
            this.applySetting();
            this.onActionHandler.accept(ButtonBar.ButtonData.OK_DONE, this.currentDbRowsToCodeSetting);
            event.consume();
        });
        final Button cancelBtn = (Button) this.dialogPane.lookupButton(ButtonType.CANCEL);
        cancelBtn.addEventFilter(ActionEvent.ACTION, event -> {
            this.onActionHandler.accept(ButtonBar.ButtonData.CANCEL_CLOSE, this.currentDbRowsToCodeSetting);
            event.consume();
        });
        final Button applyBtn = (Button) this.dialogPane.lookupButton(ButtonType.APPLY);
        applyBtn.addEventFilter(ActionEvent.ACTION, event -> {
            this.applySetting();
            this.onActionHandler.accept(ButtonBar.ButtonData.APPLY, this.currentDbRowsToCodeSetting);
            event.consume();
        });
    }

    public void applySetting() {
        this.currentDbRowsToCodeSetting = DbRowsToCodeSetting.from(
                this.jsonFieldNamingChoiceBox.getValue(),
                this.ignoreNullCheckbox.isSelected(),
                this.dateFieldFormatComboBox.getEditor().getText(),
                this.getDfSet()
        );
        UiUtil.showInfo("Applied");
    }

    private Set<String> getDfSet() {
        return new HashSet<>(this.dateFieldFormatComboBox.getItems());
    }

    public void addDateFormat() {
        TextField editor = this.dateFieldFormatComboBox.getEditor();
        SelectionModel<String> selected = this.dateFieldFormatComboBox.getSelectionModel();
        if (editor.getText().isEmpty()) {
            UiUtil.showWarning("Cannot add blank value");
        } else {
            String value = editor.getText();
            ObservableList<String> currentValues = this.dateFieldFormatComboBox.getItems();
            if (currentValues.contains(value)) {
                UiUtil.showWarning("Value existed");
            } else {
                currentValues.add(value);
                selected.clearSelection();
                editor.setText("");
            }
        }
    }

    public void removeDateFormat() {
        TextField editor = this.dateFieldFormatComboBox.getEditor();
        SelectionModel<String> selected = this.dateFieldFormatComboBox.getSelectionModel();
        if (editor.getText().isEmpty()) {
            UiUtil.showWarning("Cannot remove blank value");
        } else {
            String value = editor.getText();
            ObservableList<String> currentValues = this.dateFieldFormatComboBox.getItems();
            if (currentValues.contains(value)) {
                currentValues.remove(value);
                selected.clearSelection();
                editor.setText("");
            } else {
                UiUtil.showWarning("Value currently not in list");
            }
        }
    }

    public void initSetting(DbRowsToCodeSetting dbRowsToCodeSetting) {
        this.currentDbRowsToCodeSetting = dbRowsToCodeSetting;
        this.jsonFieldNamingChoiceBox.setValue(this.currentDbRowsToCodeSetting.getJsonNamingStrategy());
        this.ignoreNullCheckbox.setSelected(this.currentDbRowsToCodeSetting.isIgnoreNull());
        Set<String> items = new HashSet<>(this.currentDbRowsToCodeSetting.getSdfPool().keySet());
        this.dateFieldFormatComboBox.setItems(FXCollections.observableArrayList(items));
        this.dateFieldFormatComboBox.getSelectionModel().select(this.currentDbRowsToCodeSetting.getDateFormat());
    }

    public void resetDefault() {
        this.jsonFieldNamingChoiceBox.setValue(JsonNamingStrategy.SNAKE_CASE);
        this.ignoreNullCheckbox.setSelected(true);
        this.dateFieldFormatComboBox.setItems(FXCollections.observableArrayList(AppConstant.DEFAULT_DATE_FORMAT_LIST));
        this.dateFieldFormatComboBox.getSelectionModel().select("");
    }

}
