package vn.thanhvt.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import lombok.experimental.UtilityClass;
import vn.thanhvt.App;

@UtilityClass
public class ResourceUtil {

    private final String TEMPLATE_ROOT = "/templates/";

    public Scene initScene(String templateRelativePath, int width, int height, Consumer<Scene> sceneCustomizer) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(TEMPLATE_ROOT + templateRelativePath + ".fxml"));
        Parent rootNode = fxmlLoader.load();
        Scene scene = new Scene(rootNode, width, height);
//        scene.getStylesheets().add("org/kordamp/bootstrapfx/bootstrapfx.css");
        return fxmlLoader.getController();
    }

    public <T> T initScene(String templateRelativePath, int width, int height) throws IOException {
        return initScene(templateRelativePath, width, height, scene -> {});
    }

    public <T> T initScene(String templateRelativePath, int width, int height) throws IOException {
        return initScene(templateRelativePath, width, height, scene -> {});
    }

    public Scene initScene(String templateRelativePath) throws IOException {
        return initScene(templateRelativePath, 1280, 800, scene -> {});
    }

    public InputStream getResource(String resourcePath) {
        return App.class.getResourceAsStream(resourcePath);
    }

}
