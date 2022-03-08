package vn.thanhvt;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Locale;
import java.util.Map;

public class MainController {

    private static final String DEFAULT_FILE_LABEL = "No file selected";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final FileChooser fileChooser = new FileChooser();

    private JarProcessor jarProcessor;

    @FXML
    private Label fileLabel;

    @FXML
    private Tooltip fileLabelTooltip;

    @FXML
    private TextField classNameInput;

    @FXML
    private TextArea inputTextArea;

    @FXML
    private TextArea outputTextArea;

    @FXML
    public void initialize() {
        this.fileChooser.setTitle("Select jar file");
        this.fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        this.fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All file", "*.*"),
                new FileChooser.ExtensionFilter("Java Archive", "*.jar"),
                new FileChooser.ExtensionFilter("Web Archive", "*.war")
        );
        this.clearFile(null);
    }

    @FXML
    void clearFile(ActionEvent event) {
        this.fileLabel.setText(DEFAULT_FILE_LABEL);
        this.fileLabelTooltip.setText(DEFAULT_FILE_LABEL);
        if (this.jarProcessor != null) {
            this.jarProcessor = null;
        }
    }

    @FXML
    public void openFileBrowser(ActionEvent event) {
        File selectedFile = fileChooser.showOpenDialog(App.mainStage);
        if (selectedFile != null) {
            try {
                this.jarProcessor = new JarProcessor(selectedFile);
                this.fileLabel.setText(selectedFile.getAbsolutePath());
                this.fileLabelTooltip.setText(selectedFile.getAbsolutePath());
                System.out.printf("Jar file loaded: %s%n", selectedFile.getAbsolutePath());
            } catch (Exception e) {
                Util.showError(e);
            }
        }

    }

    @FXML
    void hideTooltip(MouseEvent event) {
        this.fileLabelTooltip.hide();
    }

    @FXML
    void showTooltip(MouseEvent event) {
        this.fileLabelTooltip.show(App.mainStage);
    }

    @FXML
    void generate(ActionEvent event) {
        try {
            String className = this.classNameInput.getText();
            if (className == null || className.trim().isEmpty()) {
                throw new RuntimeException("Classname required!");
            }
            if (this.jarProcessor != null) {
                Class<?> clazz = this.jarProcessor.getAvailableClasses().get(className);
                System.out.println(clazz.getDeclaredField("maHq"));
            }
            String inputText = this.inputTextArea.getText();
            Map<String, ?> map = this.objectMapper.readValue(inputText, Map.class);
            System.out.println(map);
            String varName = className.substring(0, 1).toLowerCase(Locale.ROOT) + className.substring(1);
            StringBuilder outputBuilder = new StringBuilder(className)
                    .append(" ").append(varName)
                    .append(" = new ").append(className)
                    .append("();\n");

            for (Map.Entry<String, ?> entry : map.entrySet()) {
                outputBuilder.append(varName)
                        .append(".set").append(Util.snakeToPascalCase(entry.getKey()))
                        .append("(").append(entry.getValue()).append(");\n");
            }
            this.outputTextArea.setText(outputBuilder.toString());
        } catch (Exception e) {
            Util.showError(e);
        }
    }

}
