package vn.thanhvt.util;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressIndicator;
import vn.thanhvt.custom.RunnableWithError;

/**
 * @author pysga
 * @created 3/16/2022 - 1:03 AM
 * @project json-to-code-generator
 * @since 1.0
 **/
public class UiUtil {

    public static void loading(
        ProgressIndicator progressIndicator, RunnableWithError runnable, Consumer<Exception> errorHandler) {
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
