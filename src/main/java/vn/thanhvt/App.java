package vn.thanhvt;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import vn.thanhvt.util.ResourceUtil;
import vn.thanhvt.util.StorageUtil;

/**
 * JavaFX App
 */
public class App extends Application {

    public static Stage mainStage;

    @Override
    public void start(Stage stage) throws IOException {
        mainStage = stage;
        StorageUtil.initSetting();
        Scene scene = ResourceUtil.initScene("main");
        ResourceUtil.applyDarkTheme(scene.getRoot());
        mainStage.getIcons().add(new Image(ResourceUtil.getResource("/images/Spr_B2W2_Alder.png")));
        mainStage.setScene(scene);
        mainStage.setWidth(1280);
        mainStage.setHeight(800);
        mainStage.setMinWidth(800);
        mainStage.setMinHeight(640);
        mainStage.setTitle("VTT CODE GENERATOR");
        mainStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        StorageUtil.saveSetting();
    }

    public static void main(String[] args) {
        launch();
    }

}