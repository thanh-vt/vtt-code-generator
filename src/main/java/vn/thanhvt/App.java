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
        Scene scene = ResourceUtil.initScene("main");
        stage.getIcons().add(new Image(ResourceUtil.getResource("/images/Spr_B2W2_Alder.png")));
        stage.setScene(scene);
        stage.setWidth(1280);
        stage.setHeight(800);
        stage.setMinWidth(800);
        stage.setMinHeight(640);
        stage.setTitle("JSON TO INIT CODE GENERATOR");
        stage.show();
        mainStage = stage;
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