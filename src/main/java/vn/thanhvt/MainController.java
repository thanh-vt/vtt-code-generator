package vn.thanhvt;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Locale;
import java.util.Map;
import javax.naming.OperationNotSupportedException;

public class MainController {

    private static final String DEFAULT_FILE_LABEL = "No file selected";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final FileChooser fileChooser = new FileChooser();

    private final MainService mainService = new MainService();

    @FXML
    private Button browseFileBtn;

    @FXML
    private Button clearFileBtn;


    @FXML
    private Label fileLabel;

    @FXML
    private Tooltip fileLabelTooltip;

    @FXML
    private CheckBox isSpringCheckbox;

    @FXML
    private Button loadBtn;

    @FXML
    private Button unloadBtn;

    @FXML
    private TextField classNameInput;

    @FXML
    private TextArea inputTextArea;

    @FXML
    private TextArea outputTextArea;

    private File selectedFile;

    @FXML
    public void initialize() {
        this.fileChooser.setTitle("Select jar file");
        this.fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        this.fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All file", "*.jar", "*.war"),
                new FileChooser.ExtensionFilter("Java Archive", "*.jar"),
                new FileChooser.ExtensionFilter("Web Archive", "*.war")
        );
        this.clearFile(null);
    }

    @FXML
    void clearFile(ActionEvent event) {
        this.fileLabel.setText(DEFAULT_FILE_LABEL);
        this.fileLabelTooltip.setText(DEFAULT_FILE_LABEL);
        this.selectedFile = null;
    }

    @FXML
    public void openFileBrowser(ActionEvent event) {
        File selectedFile = fileChooser.showOpenDialog(App.mainStage);
        if (selectedFile != null) {
            this.fileLabel.setText(selectedFile.getAbsolutePath());
            this.fileLabelTooltip.setText(selectedFile.getAbsolutePath());
            System.out.printf("File loaded: %s%n", selectedFile.getAbsolutePath());
            this.selectedFile = selectedFile;
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
    void loadClasses(ActionEvent event) {
        try {
            this.mainService.applyConfig(this.selectedFile, this.isSpringCheckbox.isSelected());
            this.setLoaded(true);
        } catch (Exception e) {
            Util.showError(e);
            this.setLoaded(false);
        }
    }

    @FXML
    void unloadClasses(ActionEvent event) {
        this.mainService.clearConfig();
        this.setLoaded(false);
    }

    void setLoaded(boolean isLoaded) {
        this.isSpringCheckbox.setDisable(isLoaded);
        this.loadBtn.setDisable(isLoaded);
        this.unloadBtn.setDisable(!isLoaded);
        this.browseFileBtn.setDisable(isLoaded);
        this.clearFileBtn.setDisable(isLoaded);
    }

    @FXML
    void generate(ActionEvent event) {
        try {
            String className = this.classNameInput.getText();
            if (className == null || className.trim().isEmpty()) {
                throw new RuntimeException("Classname required!");
            }
            Class<?> clazz = this.mainService.getCurrentClassLoader().loadClass(className);
            String inputText = this.inputTextArea.getText();
            Map<String, ?> map = this.objectMapper.readValue(inputText, Map.class);
            String classSimpleName = clazz.getSimpleName();
            String varName = classSimpleName.substring(0, 1).toLowerCase(Locale.ROOT) + classSimpleName.substring(1);
            StringBuilder outputBuilder = new StringBuilder(classSimpleName)
                    .append(" ").append(varName)
                    .append(" = new ").append(classSimpleName)
                    .append("();\n");
            Map<String, Method> methodMap = new HashMap<>();
            for (Method method: clazz.getDeclaredMethods()) {
                methodMap.put(method.getName(), method);
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            for (Map.Entry<String, ?> entry : map.entrySet()) {
                String pascalFieldName = Util.snakeToPascalCase(entry.getKey());
                String getterName = "get" + pascalFieldName;
                Method getter = methodMap.get(getterName);
                if (getter == null) {
                    continue;
                }
                Class<?> fieldType = getter.getReturnType();
                String val;
                if (entry.getValue() == null) {
                    val = "null";
                } else if (fieldType == String.class) {
                    val = "\"" + entry.getValue() + "\"";
                } else if (Number.class.isAssignableFrom(fieldType)) {
                    if (fieldType == Double.class) {
                        val = entry.getValue() + "d";
                    } else if (fieldType == Float.class) {
                        val = entry.getValue() + "f";
                    } else if (fieldType == Long.class) {
                        val = entry.getValue() + "l";
                    } else if (fieldType == BigDecimal.class) {
                        val = "new BigDecimal(\"" + entry.getValue() + "\")";
                    } else {
                        val = String.valueOf(entry.getValue());
                    }
                } else if (fieldType == Date.class) {
                    Date date = sdf.parse(String.valueOf(entry.getValue()));
                    val = "new Date(" + date.getTime() + "l) // " + entry.getValue();
                } else {
                    throw new OperationNotSupportedException(String.format("Type %s not supported", fieldType.getName()));
                }
                String setterName = "set" + pascalFieldName;
                outputBuilder.append(varName)
                        .append(".").append(setterName)
                        .append("(").append(val).append(")").append(";\n");
            }
            this.outputTextArea.setText(outputBuilder.toString());
        } catch (Exception e) {
            Util.showError(e);
        }
    }

}
