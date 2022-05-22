package vn.thanhvt.component;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import lombok.Setter;
import lombok.SneakyThrows;
import vn.thanhvt.App;
import vn.thanhvt.constant.AppConstant;
import vn.thanhvt.util.ResourceUtil;
import vn.thanhvt.util.StorageUtil;
import vn.thanhvt.util.UiUtil;

/**
 * @author pysga
 * @created 5/21/2022 - 6:14 PM
 * @project json-to-code-generator
 * @since 1.0
 **/
public class ArchiveClassesLoader extends VBox implements Initializable {

    private static final String DEFAULT_FILE_LABEL = "No file selected";

    private final FileChooser fileChooser = new FileChooser();

    @SneakyThrows
    public ArchiveClassesLoader() {
        ResourceUtil.initCustomComponent("archive_classes_loader",  this);
    }

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

    private File selectedFile;

    @Setter
    private Runnable loadClassesCallback;

    @Setter
    private Runnable unloadClassesCallback;

    private File currentDirectory;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.currentDirectory = new File(StorageUtil.getSetting().getLastInDir());
        this.fileChooser.setTitle("Select jar file");
        if (this.currentDirectory.exists()) {
            this.fileChooser.setInitialDirectory(this.currentDirectory);
        } else {
            File home = new File(System.getProperty(AppConstant.HOME_DIR));
            this.fileChooser.setInitialDirectory(home);
        }
        this.fileChooser.setInitialDirectory(
            new File(System.getProperty(AppConstant.HOME_DIR))
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
        if (this.currentDirectory.exists()) {
            this.fileChooser.setInitialDirectory(this.currentDirectory);
        } else {
            File home = new File(System.getProperty(AppConstant.HOME_DIR));
            this.fileChooser.setInitialDirectory(home);
        }
        File selectedFile = this.fileChooser.showOpenDialog(App.mainStage);
        if (selectedFile != null) {
            this.fileLabel.setText(selectedFile.getAbsolutePath());
            this.fileLabelTooltip.setText(selectedFile.getAbsolutePath());
            System.out.printf("File loaded: %s%n", selectedFile.getAbsolutePath());
            this.selectedFile = selectedFile;
            this.currentDirectory = selectedFile.getParentFile();
            StorageUtil.getSetting().setLastInDir(this.currentDirectory.getAbsolutePath());
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
            this.loadClassesCallback.run();
            this.setLoaded(true);
        }, (e) -> {
            UiUtil.showError(e);
            this.setLoaded(false);
        });
    }

    @FXML
    void unloadClasses(ActionEvent event) {
        UiUtil.loading(this.loadingProgress, () -> {
            this.unloadClassesCallback.run();
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

    public Pair<File, Boolean> getArchiveInfo() {
        return new Pair<>(this.selectedFile, this.isSpringCheckbox.isSelected());
    }
}
