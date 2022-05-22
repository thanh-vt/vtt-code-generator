package vn.thanhvt.controller;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.ToggleButton;
import vn.thanhvt.App;
import vn.thanhvt.util.ResourceUtil;

public class MainController {

    @FXML
    public void selectTab(Event event) throws IOException {
        Tab tab = (Tab) event.getTarget();
        String fxmlName = tab.getId();
        Node node = ResourceUtil.getNodeAndController(fxmlName).getKey();
        tab.setContent(node);
    }

    public void toggleDarkMode(ActionEvent event) {
        boolean isToggled = ((ToggleButton) event.getTarget()).isSelected();
        String darkThemeSheet = ResourceUtil.getStyleSheet("dark-theme");
        if (isToggled) {
            App.mainStage.getScene().getStylesheets().add(darkThemeSheet);
        } else {
            App.mainStage.getScene().getStylesheets().remove(darkThemeSheet);
        }
    }

}
