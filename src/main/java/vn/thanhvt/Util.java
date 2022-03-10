package vn.thanhvt;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressIndicator;
import jdk.nashorn.internal.runtime.linker.Bootstrap;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class Util {

    private static final String TEMPLATE_ROOT = "/templates/";

    public static String camelToPascalCase(String str) {
        return str.substring(0, 1).toUpperCase()
                + str.substring(1);
    }

    public static String snakeToPascalCase(String str) {
        String[] parts = str.split("_");

        StringBuilder pascalString = new StringBuilder();

        for (String string : parts) {

            String temp = string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
            pascalString.append(temp);

        }
        return pascalString.toString();
    }

    public static void generateMethodMap(Class<?> clazz, Map<String, Method> methodMap) {
        for (Method method: clazz.getDeclaredMethods()) {
            methodMap.put(method.getName(), method);
        }
        if (clazz.getSuperclass() != null) {
            generateMethodMap(clazz.getSuperclass(), methodMap);
        }
    }

    public static Scene initScene(String templateRelativePath) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(TEMPLATE_ROOT + templateRelativePath + ".fxml"));
        Parent rootNode = fxmlLoader.load();
        Scene scene = new Scene(rootNode, 1280, 800);
        scene.getStylesheets().add("org/kordamp/bootstrapfx/bootstrapfx.css");
        return scene;
    }

    public static InputStream getResource(String resourcePath) {
        return App.class.getResourceAsStream(resourcePath);
    }

    public static void loading(ProgressIndicator progressIndicator, RunnableWithError runnable, Consumer<Exception> errorHandler) {
        Timer timer = new Timer();
        System.out.println("Start time: " + new Date());
        progressIndicator.setVisible(true);
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                try {
                    runnable.run();
                } catch (Exception e) {
                    errorHandler.accept(e);
                } finally {
                    progressIndicator.setVisible(false);
                }
            }
        }, 500);
    }

    public static void showError(Exception e) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");

            // Header Text: null
            alert.setHeaderText("Error: " + e.getMessage());
            e.printStackTrace();
            StringBuilder sb = new StringBuilder();
            int max = 0;
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                sb.append(stackTraceElement.getFileName())
                        .append(" (line ").append(stackTraceElement.getLineNumber())
                        .append("): ").append(stackTraceElement.getClassName())
                        .append(".").append(stackTraceElement.getMethodName())
                        .append("\n");
                if (max >= 10) {
                    sb.append("...");
                    break;
                }
                max++;
            }
            alert.setContentText(sb.toString());
            alert.setWidth(600);
            alert.setHeight(400);
            alert.showAndWait();
        });
    }

}
