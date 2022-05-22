package vn.thanhvt.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.util.Pair;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import vn.thanhvt.component.ArchiveClassesLoader;
import vn.thanhvt.model.DbRowsToCodeSetting;
import vn.thanhvt.service.DbRowsToCodeService;
import vn.thanhvt.util.ResourceUtil;
import vn.thanhvt.util.UiUtil;

/**
 * @author pysga
 * @created 5/20/2022 - 11:19 PM
 * @project json-to-code-generator
 * @since 1.0
 **/
public class DbRowsToCodeController implements Initializable {

    private final DbRowsToCodeService dbRowsToCodeService = new DbRowsToCodeService();

    @FXML
    private ArchiveClassesLoader acl;

    @FXML
    private TextField classNameInput;

    @FXML
    private TextArea inputTextArea;

    private AutoCompletionBinding<String> autoCompletionBinding;

    @FXML
    private TextArea outputTextArea;

    private DbRowsToCodeSetting selectedDbRowsToCodeSetting = DbRowsToCodeSetting.getDefaultSetting();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.acl.setLoadClassesCallback(() -> {
            try {
                Pair<File, Boolean> archiveInfo = this.acl.getArchiveInfo();
                Set<String> loadedSourceClassNames = this.dbRowsToCodeService
                    .applyConfig(archiveInfo.getKey(), archiveInfo.getValue());
                this.autoCompletionBinding = TextFields
                    .bindAutoCompletion(this.classNameInput, loadedSourceClassNames);
                this.autoCompletionBinding.setVisibleRowCount(10);
                this.autoCompletionBinding.setMinWidth(500);
            } catch (Exception ex) {
                System.err.println(ex);
            }
        });
        this.acl.setUnloadClassesCallback(() -> {
            this.dbRowsToCodeService.clearConfig();
            this.autoCompletionBinding.dispose();
        });
    }

    @FXML
    void generate(ActionEvent event) {
        try {
            String outputText = this.dbRowsToCodeService.generate(this.classNameInput.getText(),
                this.inputTextArea.getText(),
                this.selectedDbRowsToCodeSetting);
            this.outputTextArea.setText(outputText);
        } catch (Exception e) {
            UiUtil.showError(e);
        }
    }

    @FXML
    void onOpenConfig(ActionEvent event) {
        try {
            Dialog<DbRowsToCodeSetting> settingDialog = ResourceUtil
                .<DbRowsToCodeSetting, ConfigController>initDialog("config", (dialog, controller) -> {
                    controller.setOnActionHandler((buttonData, setting) -> {
                        switch (buttonData) {
                            case OK_DONE:
                                this.selectedDbRowsToCodeSetting = setting;
                                dialog.close();
                                break;
                            case APPLY:
                                this.selectedDbRowsToCodeSetting = setting;
                                break;
                            case CANCEL_CLOSE:
                                dialog.close();
                                break;
                        }
                    });
                    controller.initSetting(this.selectedDbRowsToCodeSetting);
                });
            settingDialog.initModality(Modality.APPLICATION_MODAL);
            settingDialog.setTitle("Config");
            settingDialog.showAndWait();
        } catch (IOException | InstantiationException | IllegalAccessException e) {
            UiUtil.showError(e);
        }
    }

}
