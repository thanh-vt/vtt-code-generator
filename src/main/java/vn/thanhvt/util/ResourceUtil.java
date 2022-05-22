package vn.thanhvt.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.util.Pair;
import lombok.experimental.UtilityClass;
import vn.thanhvt.App;

@UtilityClass
public class ResourceUtil {

    private final String TEMPLATE_ROOT = "/templates/";

    private final String STYLE_ROOT = "/css/";

    public <N extends Node, C> Pair<N, C> getNodeAndController(String templateRelativePath) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(TEMPLATE_ROOT + templateRelativePath + ".fxml"));
        return new Pair<>(fxmlLoader.load(), fxmlLoader.getController());
    }

    public void initCustomComponent(String templateRelativePath, Object root) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(TEMPLATE_ROOT + templateRelativePath + ".fxml"));
        fxmlLoader.setController(root);
        fxmlLoader.setRoot(root);
        fxmlLoader.load();
//        System.out.println(root == fxmlLoader.getController());
    }

    public <C> Scene initScene(String templateRelativePath, int width, int height, Consumer<C> controllerCustomizer) throws IOException {
        Pair<Parent, C> result = getNodeAndController(templateRelativePath);
        Parent rootNode = result.getKey();
        Scene scene;
        if (width > 0 && height > 0) {
            scene = new Scene(rootNode, width, height);
        } else {
            scene = new Scene(rootNode);
        }
//        scene.getStylesheets().add("org/kordamp/bootstrapfx/bootstrapfx.css");
        controllerCustomizer.accept(result.getValue());
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
        dialog.setWidth(480);
        dialog.setHeight(300);
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

    public String getStyleSheet(String styleRelativePath) {
        return Objects.requireNonNull(App.class.getResource(STYLE_ROOT + styleRelativePath + ".css")).toExternalForm();
    }

    public InputStream getResource(String resourcePath) {
        return App.class.getResourceAsStream(resourcePath);
    }

    public void applyDarkTheme(Parent root) {
        String darkThemeSheet = ResourceUtil.getStyleSheet("dark-theme");
        boolean isToggled = StorageUtil.getSetting().getDarkModeEnabled();
        if (isToggled) {
            root.getStylesheets().add(darkThemeSheet);
        } else {
            root.getStylesheets().remove(darkThemeSheet);
        }
    }

}
