package vn.thanhvt;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    public static Stage mainStage;

    @Override
    public void start(Stage stage) throws IOException {
        Scene scene = Util.initScene("main");
        stage.getIcons().add(new Image(Util.getResource("/images/Spr_B2W2_Alder.png")));
        stage.setScene(scene);
        stage.setTitle("JSON TO INIT CODE GENERATOR");
        stage.show();
        mainStage = stage;
    }

    public static void main(String[] args) {
        launch();
    }

}