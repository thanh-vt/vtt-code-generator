package vn.thanhvt.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import vn.thanhvt.util.ResourceUtil;
import vn.thanhvt.util.StorageUtil;

public class MainController implements Initializable {

    @FXML
    private BorderPane borderPane;

    @FXML
    private ToggleButton darkModeToggler;

    @FXML
    public void selectTab(Event event) throws IOException {
        Tab tab = (Tab) event.getTarget();
        String fxmlName = tab.getId();
        Node node = ResourceUtil.getNodeAndController(fxmlName).getKey();
        tab.setContent(node);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.darkModeToggler.setSelected(StorageUtil.getSetting().isDarkModeEnabled());
    }

    public void toggleDarkMode(ActionEvent event) {
        boolean isToggled = ((ToggleButton) event.getTarget()).isSelected();
        StorageUtil.getSetting().setDarkModeEnabled(isToggled);
        ResourceUtil.applyDarkTheme(this.borderPane);
    }

}
