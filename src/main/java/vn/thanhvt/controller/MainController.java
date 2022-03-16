package vn.thanhvt.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import vn.thanhvt.App;
import vn.thanhvt.model.Setting;
import vn.thanhvt.service.MainService;
import vn.thanhvt.util.ResourceUtil;
import vn.thanhvt.util.UiUtil;

import java.io.File;
import java.io.IOException;

public class MainController {

    private static final String DEFAULT_FILE_LABEL = "No file selected";

    private final FileChooser fileChooser = new FileChooser();

    private final MainService mainService = new MainService();

    @FXML
    private Button browseFileBtn;

    @FXML
    private Button clearFileBtn;

    @FXML
    private Label fileLabel;

    @FXML
    private Tooltip fileLabelTooltip;

    @FXML
    private CheckBox isSpringCheckbox;

    @FXML
    private Button loadBtn;

    @FXML
    private Button unloadBtn;

    @FXML
    private ProgressIndicator loadingProgress;

    @FXML
    private TextField classNameInput;

    @FXML
    private TextArea inputTextArea;

    @FXML
    private TextArea outputTextArea;

    private File selectedFile;

    private Setting selectedSetting = Setting.getDefaultSetting();

    @FXML
    public void initialize() {
        this.fileChooser.setTitle("Select jar file");
        this.fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        this.fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All file", "*.jar", "*.war"),
                new FileChooser.ExtensionFilter("Java Archive", "*.jar"),
                new FileChooser.ExtensionFilter("Web Archive", "*.war")
        );
        this.clearFile(null);
    }

    @FXML
    void clearFile(ActionEvent event) {
        this.fileLabel.setText(DEFAULT_FILE_LABEL);
        this.fileLabelTooltip.setText(DEFAULT_FILE_LABEL);
        this.selectedFile = null;
        this.loadBtn.setDisable(true);
        this.unloadBtn.setDisable(true);
    }

    @FXML
    public void openFileBrowser(ActionEvent event) {
        File selectedFile = fileChooser.showOpenDialog(App.mainStage);
        if (selectedFile != null) {
            this.fileLabel.setText(selectedFile.getAbsolutePath());
            this.fileLabelTooltip.setText(selectedFile.getAbsolutePath());
            System.out.printf("File loaded: %s%n", selectedFile.getAbsolutePath());
            this.selectedFile = selectedFile;
        } else {
            this.clearFile(event);
        }
        this.loadBtn.setDisable(selectedFile == null);
        this.unloadBtn.setDisable(true);
    }

    @FXML
    void hideTooltip(MouseEvent event) {
        this.fileLabelTooltip.hide();
    }

    @FXML
    void showTooltip(MouseEvent event) {
        this.fileLabelTooltip.show(App.mainStage);
    }

    @FXML
    void loadClasses(ActionEvent event) {
        UiUtil.loading(this.loadingProgress, () -> {
            this.mainService.applyConfig(this.selectedFile, this.isSpringCheckbox.isSelected());
            this.setLoaded(true);
        }, (e) -> {
            UiUtil.showError(e);
            this.setLoaded(false);
        });
    }

    @FXML
    void unloadClasses(ActionEvent event) {
        UiUtil.loading(this.loadingProgress, () -> {
            this.mainService.clearConfig();
            this.setLoaded(false);
        }, (e) -> {
            UiUtil.showError(e);
            this.setLoaded(false);
        });
    }

    void setLoaded(boolean isLoaded) {
        this.isSpringCheckbox.setDisable(isLoaded);
        this.loadBtn.setDisable(isLoaded);
        this.unloadBtn.setDisable(!isLoaded);
        this.browseFileBtn.setDisable(isLoaded);
        this.clearFileBtn.setDisable(isLoaded);
    }

    @FXML
    void generate(ActionEvent event) {
        try {
            String outputText = this.mainService.generate(this.classNameInput.getText(),
                    this.inputTextArea.getText(),
                    this.selectedSetting);
            this.outputTextArea.setText(outputText);
        } catch (Exception e) {
            UiUtil.showError(e);
        }
    }

    @FXML
    void onOpenConfig(ActionEvent event) {
        try {
            Dialog<Setting> settingDialog = ResourceUtil.<Setting, ConfigController>initDialog("config", (dialog, controller) -> {
                controller.setOnActionHandler((buttonData, setting) -> {
                    switch (buttonData) {
                        case OK_DONE:
                            this.selectedSetting = setting;
                            dialog.close();
                            break;
                        case APPLY:
                            this.selectedSetting = setting;
                            break;
                        case CANCEL_CLOSE:
                            dialog.close();
                            break;
                    }
                });
                controller.initSetting(this.selectedSetting);
            });
            settingDialog.initModality(Modality.APPLICATION_MODAL);
            settingDialog.setTitle("Config");
            settingDialog.showAndWait();
        } catch (IOException | InstantiationException | IllegalAccessException e) {
            UiUtil.showError(e);
        }
    }

}
