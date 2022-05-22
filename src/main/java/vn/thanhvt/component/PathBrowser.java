package vn.thanhvt.component;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import vn.thanhvt.util.ResourceUtil;
import vn.thanhvt.util.StorageUtil;

/**
 * @author pysga
 * @created 5/22/2022 - 3:21 PM
 * @project json-to-code-generator
 * @since 1.0
 **/
public class PathBrowser extends HBox implements Initializable {

    private final DirectoryChooser directoryChooser = new DirectoryChooser();

    private File selected;

    @FXML
    private TextField outPath;

    @SneakyThrows
    public PathBrowser() {
        ResourceUtil.initCustomComponent("path_browser",  this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.selected = new File(StorageUtil.getSetting().getLastOutDir());
        if (this.selected.exists()) {
            this.directoryChooser.setInitialDirectory(selected);
        } else {
            this.directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        }
        this.directoryChooser.setTitle("Choose output path");
    }

    public void browse(ActionEvent event) {
        this.selected = new File(StorageUtil.getSetting().getLastOutDir());
        if (this.selected.exists()) {
            this.directoryChooser.setInitialDirectory(selected);
        } else {
            this.directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        }
        this.directoryChooser.setTitle("Choose output path");
        this.selected = this.directoryChooser.showDialog(new Stage());
        if (this.selected != null) {
            this.outPath.setText(selected.getAbsolutePath());
        }
    }

    public String getPath() {
        return this.outPath.getText();
    }

}
