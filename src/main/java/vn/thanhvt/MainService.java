package vn.thanhvt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.boot.loader.archive.ExplodedArchive;
import org.springframework.boot.loader.archive.JarFileArchive;

import javax.naming.OperationNotSupportedException;
import java.io.File;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ClassLoader currentClassLoader;

    public ClassLoader getCurrentClassLoader() {
        return currentClassLoader != null ? currentClassLoader : this.getClass().getClassLoader();
    }

    public void applyConfig(File f, boolean isSpring) throws Exception {
        if (isSpring) {
            if (f.getName().endsWith(".jar")) {
                this.currentClassLoader = new JarLoader(new JarFileArchive(f)).load();
            } else if (f.getName().endsWith(".war")) {
                this.currentClassLoader = new WarLoader(new ExplodedArchive(f)).load();
            } else {
                throw new OperationNotSupportedException("This file extension is not supported");
            }
        } else {
            this.currentClassLoader = new URLClassLoader(new URL[]{f.toURI().toURL()});
        }
//
//        Class<?> clazz = Class.forName("vn.etc.customs.ektt.dto.KtBangkeNhantuKhobacDto", true, this.currentClassLoader);
//
//        ImmutableSet<ClassInfo> classes = ClassPath.from(this.currentClassLoader).getAllClasses();
//        Iterator<ClassPath.ClassInfo> iterator = classes.stream().iterator();
//        while (iterator.hasNext()) {
//            ClassInfo classInfo = iterator.next();
//            System.out.println(classInfo.getName());
//        }
//        System.out.println(clazz.getSimpleName());
    }

    public void clearConfig() {
        this.currentClassLoader = null;
    }

    public String generate(String className, String inputText) throws OperationNotSupportedException, ParseException, ClassNotFoundException, JsonProcessingException {
        if (className == null || className.trim().isEmpty()) {
            throw new RuntimeException("Classname required!");
        }
        Class<?> clazz = this.getCurrentClassLoader().loadClass(className);
        JsonNode jsonNode = this.objectMapper.readTree(inputText);
        String classSimpleName = clazz.getSimpleName();
        String varBaseName = classSimpleName.substring(0, 1).toLowerCase(Locale.ROOT) + classSimpleName.substring(1);
        if (jsonNode.isObject()) {
            return this.convertObjectNode((ObjectNode) jsonNode, clazz, varBaseName);
        } else if (jsonNode.isArray()) {
            String varListName = varBaseName + "List";
            StringBuilder sb = new StringBuilder();
            sb.append("List<").append(classSimpleName).append("> ")
                    .append(varListName)
                    .append(" = new ArrayList<>();\n");
            ArrayNode arrayNode = (ArrayNode) jsonNode;

            for (int i = 0; i < jsonNode.size(); i++) {
                if (arrayNode.get(i).isObject()) {
                    String varName = varBaseName + (i + 1);
                    sb.append(this.convertObjectNode((ObjectNode) arrayNode.get(i), clazz, varName));
                    sb.append(varListName).append(".add(").append(varName).append(");\n");
                } else throw new OperationNotSupportedException("Nested array json not supported");
            }
            return sb.toString();
        } else throw new OperationNotSupportedException("Unrecognized JSON type");
    }

    private String convertObjectNode(ObjectNode objectNode, Class<?> clazz, String varName) throws ParseException, OperationNotSupportedException {

//        Map<String, ?> map = this.objectMapper.readValue(inputText, Map.class);
        String classSimpleName = clazz.getSimpleName();
        StringBuilder outputBuilder = new StringBuilder(classSimpleName)
                .append(" ").append(varName)
                .append(" = new ").append(classSimpleName)
                .append("();\n");
        Map<String, Method> methodMap = new HashMap<>();
        Util.generateMethodMap(clazz, methodMap);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Iterator<Map.Entry<String, JsonNode>> fieldIterator = objectNode.fields();
        while (fieldIterator.hasNext()) {
            Map.Entry<String, JsonNode> entry = fieldIterator.next();
            String pascalFieldName = Util.snakeToPascalCase(entry.getKey());
            String getterName = "get" + pascalFieldName;
            Method getter = methodMap.get(getterName);
            if (getter == null) {
                continue;
            }
            Class<?> fieldType = getter.getReturnType();
            String val;
            if (entry.getValue() instanceof NullNode) {
                val = "null";
            } else if (fieldType == String.class) {
                val = "\"" + entry.getValue().asText() + "\"";
            } else if (Number.class.isAssignableFrom(fieldType)) {
                if (fieldType == Double.class) {
                    val = entry.getValue().asText() + "d";
                } else if (fieldType == Float.class) {
                    val = entry.getValue().asText() + "f";
                } else if (fieldType == Long.class) {
                    val = entry.getValue().asText() + "l";
                } else if (fieldType == BigDecimal.class) {
                    val = "new BigDecimal(\"" + entry.getValue().asText() + "\")";
                } else {
                    val = entry.getValue().asText();
                }
            } else if (fieldType == Date.class) {
                Date date = sdf.parse(String.valueOf(entry.getValue()));
                val = "new Date(" + date.getTime() + "l)";
            } else {
                throw new OperationNotSupportedException(String.format("Type %s not supported", fieldType.getName()));
            }
            String setterName = "set" + pascalFieldName;
            outputBuilder.append(varName)
                    .append(".").append(setterName)
                    .append("(").append(val).append(");");
            if (entry.getValue() != null && fieldType == Date.class) {
                outputBuilder.append(" // ").append(entry.getValue());
            }
            outputBuilder.append("\n");
        }
        return outputBuilder.toString();
    }

}
