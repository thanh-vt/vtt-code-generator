package vn.thanhvt.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.util.Pair;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import vn.thanhvt.component.ArchiveClassesLoader;
import vn.thanhvt.component.PathBrowser;
import vn.thanhvt.constant.TypescriptDataType;
import vn.thanhvt.model.TypescriptModel;
import vn.thanhvt.model.TypescriptModelProperty;
import vn.thanhvt.model.TypescriptModelProperty.TypescriptCollection;
import vn.thanhvt.model.TypescriptModelProperty.TypescriptEnum;
import vn.thanhvt.service.JavaToTypescriptModelService;
import vn.thanhvt.util.ResourceUtil;
import vn.thanhvt.util.StringUtil;
import vn.thanhvt.util.UiUtil;

/**
 * @author pysga
 * @created 5/20/2022 - 11:36 PM
 * @project json-to-code-generator
 * @since 1.0
 **/
public class JavaToTypescriptModelController implements Initializable {

    private final JavaToTypescriptModelService javaToTypescriptModelService = new JavaToTypescriptModelService();

    @FXML
    private PathBrowser pathBrowser;

    @FXML
    private TableColumn<TypescriptModelProperty, String> fieldNameColumn;

    @FXML
    private TableColumn<TypescriptModelProperty, TypescriptDataType> dataTypeColumn;

    @FXML
    private TableColumn<TypescriptModelProperty, Boolean> nullableColumn;

    @FXML
    private TableColumn<TypescriptModelProperty, String> customDataTypeColumn;

    @FXML
    private TableView<TypescriptModelProperty> modelAttrsTable;

    @FXML
    private ArchiveClassesLoader acl;

    @FXML
    private TextField basePackageInput;

    private TypescriptModel selectedModel;

    private AutoCompletionBinding<String> autoCompletionBinding;

    @FXML
    private ListView<TypescriptModel> typescriptModelListView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.typescriptModelListView.setCellFactory(param -> new TypescriptModelCell());
        this.acl.setLoadClassesCallback(() -> {
            try {
                Pair<File, Boolean> archiveInfo = this.acl.getArchiveInfo();
                Set<String> loadedPackageNames = this.javaToTypescriptModelService
                    .applyConfig(archiveInfo.getKey(), archiveInfo.getValue());
                this.autoCompletionBinding = TextFields
                    .bindAutoCompletion(this.basePackageInput, loadedPackageNames);
                this.autoCompletionBinding.setVisibleRowCount(10);
                this.autoCompletionBinding.setMinWidth(500);
            } catch (Exception ex) {
                System.err.println(ex);
            }
        });
        this.acl.setUnloadClassesCallback(() -> {
            this.javaToTypescriptModelService.clearConfig();
            this.autoCompletionBinding.dispose();
        });
        // Field name column
        this.fieldNameColumn.setCellValueFactory(new PropertyValueFactory<>("fieldName"));
        this.fieldNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        this.fieldNameColumn.setOnEditCommit(event -> {
            TablePosition<TypescriptModelProperty, String> position = event.getTablePosition();
            String fieldName = event.getNewValue();
            int row = position.getRow();
            TypescriptModelProperty typescriptModelProperty = event.getTableView().getItems().get(row);
            typescriptModelProperty.setFieldName(fieldName);
        });
        this.fieldNameColumn.prefWidthProperty().bind(this.modelAttrsTable.widthProperty().multiply(0.3));
        // Data type column
        this.dataTypeColumn.setCellValueFactory(param -> {
            TypescriptDataType typescriptDataType = param.getValue().getDataType();
            SimpleObjectProperty<TypescriptDataType> dataTypeObjectProperty = new SimpleObjectProperty<>(
                typescriptDataType);
            return dataTypeObjectProperty;
        });
        ObservableList<TypescriptDataType> typescriptDataTypes = FXCollections.observableArrayList(//
            TypescriptDataType.values());
        this.dataTypeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(typescriptDataTypes));
        this.dataTypeColumn.setOnEditCommit(event -> {
            TablePosition<TypescriptModelProperty, TypescriptDataType> position = event.getTablePosition();
            TypescriptDataType typescriptDataType = event.getNewValue();
            int rowIndex = position.getRow();
            TypescriptModelProperty typescriptModelProperty = event.getTableView().getItems().get(rowIndex);
            typescriptModelProperty.setDataType(typescriptDataType);
        });
        this.dataTypeColumn.prefWidthProperty().bind(this.modelAttrsTable.widthProperty().multiply(0.3));
        // Nested data type column
        this.customDataTypeColumn.setCellValueFactory(new PropertyValueFactory<>("customDataType"));
        this.customDataTypeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        this.customDataTypeColumn.setOnEditCommit(event -> {
            TablePosition<TypescriptModelProperty, String> position = event.getTablePosition();
            String customDataType = event.getNewValue();
            int row = position.getRow();
            TypescriptModelProperty typescriptModelProperty = event.getTableView().getItems().get(row);
            typescriptModelProperty.setCustomDataType(customDataType);
        });
        this.customDataTypeColumn.prefWidthProperty().bind(this.modelAttrsTable.widthProperty().multiply(0.3));
        // Nullable column
        this.nullableColumn.setCellValueFactory(
            param -> {
                TypescriptModelProperty property = param.getValue();
                SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(property.isNullable());

                // Chú ý: singleCol.setOnEditCommit(): Không làm việc với
                // CheckBoxTableCell.

                // Khi cột "Nullable?" thay đổi
                booleanProp.addListener((observable, oldValue, newValue) -> property.setNullable(newValue));
                return booleanProp;
            });
        this.nullableColumn.setCellFactory(param -> new CheckBoxTableCell<>());
        this.nullableColumn.prefWidthProperty().bind(this.modelAttrsTable.widthProperty().multiply(0.1));
        this.typescriptModelListView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                this.selectedModel = newValue;
                if (this.selectedModel != null) {
                    List<TypescriptModelProperty> typescriptModelPropertyList = selectedModel.getProperties();
                    this.modelAttrsTable.setItems(FXCollections.observableList(typescriptModelPropertyList));
                } else {
                    this.modelAttrsTable.setItems(FXCollections.emptyObservableList());
                }
            }
        );
    }

    public void onOpenConfig(ActionEvent event) {
    }

    public void generate(ActionEvent event) throws IOException, ClassNotFoundException {
        String basePackage = this.basePackageInput.getText();
        AtomicReference<Exception> lastEx = new AtomicReference<>();
        Set<TypescriptModel> typescriptModelSet = this.javaToTypescriptModelService
            .generate(basePackage)
            .stream()
            .map(e -> {
                try {
                    return TypescriptModel.fromClass(e, this.javaToTypescriptModelService.getCurrentClassLoader());
                } catch (ClassNotFoundException classNotFoundException) {
                    lastEx.set(classNotFoundException);
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .filter(e -> !e.getProperties().isEmpty())
            .collect(Collectors.toSet());
        this.typescriptModelListView.setItems(FXCollections.observableArrayList(typescriptModelSet));
        if (lastEx.get() != null) {
            UiUtil.showError(lastEx.get());
        }
    }

    public void export(ActionEvent event) {
        Path basePath = Paths.get(this.pathBrowser.getPath());
        AtomicInteger total = new AtomicInteger();
        AtomicInteger success = new AtomicInteger();
        AtomicInteger failed = new AtomicInteger();
        AtomicReference<Exception> lastEx = new AtomicReference<>();
        Map<String, TypescriptEnum> enumCollection = new HashMap<>();
        this.typescriptModelListView.getItems()
            .stream()
            .filter(e -> !e.getProperties().isEmpty())
            .forEach(e -> {
                try {
                    String fileName = StringUtil.pascalToKebabCase(e.getTypescriptClassName()) + ".ts";
                    Path path = basePath.resolve(fileName);
                    List<String> lines = this.convertToTypescript(e, "  ", enumCollection);
                    Files.write(path, lines, StandardOpenOption.CREATE, StandardOpenOption.WRITE,
                        StandardOpenOption.TRUNCATE_EXISTING);
                    success.getAndIncrement();
                } catch (Exception ex) {
                    lastEx.set(ex);
                    failed.getAndIncrement();
//                    throw new RuntimeException(ex);
                } finally {
                    total.getAndIncrement();
                }
            });
        for (TypescriptEnum typescriptEnum : enumCollection.values()) {
            this.buildEnumType(typescriptEnum, basePath, total, success, failed, lastEx);
        }
        if (lastEx.get() != null) {
            UiUtil.showError(lastEx.get());
        }
        UiUtil.showInfo(
            String.format("Process %d models, success %d, failed %d", total.get(), success.get(), failed.get()));
    }

    private List<String> convertToTypescript(TypescriptEnum typescriptEnum, String tab) {
        List<String> lines = new ArrayList<>();
        String classDeclarationOpen = "export enum " + typescriptEnum.getName() + " {";
        lines.add(classDeclarationOpen);
        if (TypescriptDataType.STRING.equals(typescriptEnum.getValueType())) {
            int index = 0;
            for (Entry<String, String> entry : typescriptEnum.getValues().entrySet()) {
                lines.add(
                    tab + entry.getKey() + " = '" + entry.getValue() + (index == typescriptEnum.getValues().size() - 1
                        ? "'" : "',"));
                index++;
            }
        } else if (TypescriptDataType.NUMBER.equals(typescriptEnum.getValueType())) {
            int index = 0;
            for (Entry<String, String> entry : typescriptEnum.getValues().entrySet()) {
                lines.add(
                    tab + entry.getKey() + " = " + entry.getValue() + (index == typescriptEnum.getValues().size() - 1
                        ? "" : ","));
                index++;
            }
        }
        String classDeclarationClose = "}";
        lines.add(classDeclarationClose);
        return lines;
    }

    private List<String> convertToTypescript(TypescriptModel typescriptModel, String tab,
        Map<String, TypescriptEnum> enumCollection) {
        Set<String> importLines = new HashSet<>();
        List<String> lines = new ArrayList<>();
        String classDeclarationOpen = "export interface " + typescriptModel.getTypescriptClassName() + " {";
        lines.add(classDeclarationOpen);

        for (TypescriptModelProperty property : typescriptModel.getProperties()) {
            String type = this.buildDataType(property, importLines, enumCollection);
            String sb = tab
                + property.getFieldName()
                + (property.isNullable() ? "?" : "")
                + ": "
                + type
                + (property.isNullable() ? " | null;" : ";");
            lines.add(sb);
        }

        if (importLines.size() > 0) {
            lines.add(0, "");
        }

        String classDeclarationClose = "}";
        lines.add(classDeclarationClose);
        return Stream.concat(importLines.stream(), lines.stream())
            .collect(Collectors.toList());
    }

    public String buildDataType(TypescriptModelProperty property, Set<String> importLines,
        Map<String, TypescriptEnum> enumCollection) {
        String type;
        if (TypescriptDataType.NESTED.equals(property.getDataType())) {
            type = property.getCustomDataType();
            String importLine = "import { " + type + " } from './" + StringUtil.pascalToKebabCase(type) + "';";
            importLines.add(importLine);
        } else if (TypescriptDataType.COLLECTION.equals(property.getDataType())) {
            StringBuilder sb = new StringBuilder();
            this.buildCollectionType(property.getCollection(), sb, importLines);
            type = sb.toString();
        } else if (TypescriptDataType.ENUM.equals(property.getDataType())) {
            if (property.getEnumeration() != null) {
                type = property.getEnumeration().getName();
                enumCollection.put(property.getEnumeration().getName(), property.getEnumeration());
            } else {
                type = TypescriptDataType.ANY.getDeclaration();
            }
        } else {
            type = property.getDataType().getDeclaration();
        }
        return type;
    }

    public void buildEnumType(TypescriptEnum typescriptEnum, Path basePath, AtomicInteger total,
        AtomicInteger success, AtomicInteger failed,
        AtomicReference<Exception> lastEx) {
        try {
            String fileName = StringUtil.pascalToKebabCase(typescriptEnum.getName()) + ".ts";
            Path path = basePath.resolve(fileName);
            List<String> lines = this.convertToTypescript(typescriptEnum, "  ");
            Files.write(path, lines, StandardOpenOption.CREATE, StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING);
            success.getAndIncrement();
        } catch (Exception ex) {
            lastEx.set(ex);
            failed.getAndIncrement();
//                    throw new RuntimeException(ex);
        } finally {
            total.getAndIncrement();
        }
    }

    public void buildCollectionType(TypescriptCollection collection, StringBuilder sb, Set<String> importLines) {
        if (collection.getNestedCollection() == null) {
            String type;
            if (collection.getCustomContentType() == null) {
                type = collection.getContentType().getDeclaration();
            } else {
                type = collection.getCustomContentType();
            }
            sb.append(type);
            String importLine = "import { " + type + " } from './" + StringUtil.pascalToKebabCase(type) + "';";
            importLines.add(importLine);
        } else {
            this.buildCollectionType(collection.getNestedCollection(), sb, importLines);
        }
        sb.append("[]");
    }

    private final DirectoryChooser directoryChooser = new DirectoryChooser();

    @EqualsAndHashCode(callSuper = true)
    @Data
    static class TypescriptModelCell extends ListCell<TypescriptModel> {

        private TypescriptModel typescriptModel;

        @SneakyThrows
        @Override
        protected void updateItem(TypescriptModel item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                this.typescriptModel = item;
                Pair<Parent, ClassListCellController> result = ResourceUtil.getNodeAndController("class_list_cell");
                result.getValue().getClassName().setText(this.typescriptModel.getJavaClassName());
//                result.getValue().getClassChecked().setSelected(!this.typescriptModel.getProperties().isEmpty());
                setGraphic(result.getKey());
            }
        }

    }

}
