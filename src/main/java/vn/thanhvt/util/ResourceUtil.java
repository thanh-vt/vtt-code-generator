package vn.thanhvt.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import lombok.experimental.UtilityClass;
import vn.thanhvt.App;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

@UtilityClass
public class ResourceUtil {

    private final String TEMPLATE_ROOT = "/templates/";

    public <T> Scene initScene(String templateRelativePath, int width, int height, Consumer<T> controllerCustomizer) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(TEMPLATE_ROOT + templateRelativePath + ".fxml"));
        Parent rootNode = fxmlLoader.load();
        Scene scene;
        if (width > 0 && height > 0) {
            scene = new Scene(rootNode, width, height);
        } else {
            scene = new Scene(rootNode);
        }
//        scene.getStylesheets().add("org/kordamp/bootstrapfx/bootstrapfx.css");
        controllerCustomizer.accept(fxmlLoader.getController());
        return scene;
    }

    public <T> Scene initScene(String templateRelativePath, Consumer<T> controllerCustomizer) throws IOException {
        return initScene(templateRelativePath, -1, -1, controllerCustomizer);
    }

    public <T> Scene initScene(String templateRelativePath, int width, int height) throws IOException {
        return initScene(templateRelativePath, width, height, scene -> {
        });
    }

    public Scene initScene(String templateRelativePath) throws IOException {
        return initScene(templateRelativePath, -1, -1, scene -> {
        });
    }

    public <T, C> Dialog<T> initDialog(String templateRelativePath, BiConsumer<Dialog<T>, C> controllerAction) throws IOException, InstantiationException, IllegalAccessException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(TEMPLATE_ROOT + templateRelativePath + ".fxml"));
        Dialog<T> dialog = new Dialog<>();
//        scene.getStylesheets().add("org/kordamp/bootstrapfx/bootstrapfx.css");
//        C controller = controllerAction.apply(dialog);
//        fxmlLoader.setController(controller);
        DialogPane rootNode = fxmlLoader.load();
        rootNode.setExpandableContent(null);
        controllerAction.accept(dialog, fxmlLoader.getController());
        dialog.setDialogPane(rootNode);

        return dialog;
    }

    public URL getResourceUrl(String templateRelativePath) {
        return App.class.getResource(TEMPLATE_ROOT + templateRelativePath + ".fxml");
    }

    public InputStream getResource(String resourcePath) {
        return App.class.getResourceAsStream(resourcePath);
    }

}
