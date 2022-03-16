package vn.thanhvt.util;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressIndicator;
import lombok.experimental.UtilityClass;
import vn.thanhvt.custom.RunnableWithError;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

/**
 * @author pysga
 * @created 3/16/2022 - 1:03 AM
 * @project json-to-code-generator
 * @since 1.0
 **/
@UtilityClass
public class UiUtil {

    public void loading(
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

    public void showError(Exception e) {
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
            alert.setWidth(800);
            alert.setHeight(600);
            alert.showAndWait();
        });
    }

    public void showDialog(String msg, String title, Alert.AlertType type) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            // Header Text: null
            alert.setHeaderText(null);
            alert.setContentText(msg);
            alert.setWidth(800);
            alert.setHeight(600);
            alert.showAndWait();
        });
    }

    public void showInfo(String msg) {
        showDialog(msg, "Information", Alert.AlertType.INFORMATION);
    }

    public void showWarning(String msg) {
        showDialog(msg, "Warning", Alert.AlertType.WARNING);
    }

}
