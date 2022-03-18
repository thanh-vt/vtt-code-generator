package vn.thanhvt.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.naming.OperationNotSupportedException;
import org.springframework.boot.loader.archive.JarFileArchive;
import vn.thanhvt.custom.JarLoader;
import vn.thanhvt.custom.SimpleJarLoader;
import vn.thanhvt.custom.WarLoader;
import vn.thanhvt.model.Setting;

public class MainService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ClassLoader currentClassLoader;

    public ClassLoader getCurrentClassLoader() {
        return currentClassLoader != null ? currentClassLoader : this.getClass().getClassLoader();
    }

    public Set<String> applyConfig(File f, boolean isSpring) throws Exception {
        Set<String> loadedSourceClassNames = new HashSet<>();
        if (isSpring) {
            if (f.getName().endsWith(".jar")) {
                this.currentClassLoader = new JarLoader(new JarFileArchive(f)).load(loadedSourceClassNames);
            } else if (f.getName().endsWith(".war")) {
                this.currentClassLoader = new WarLoader(new JarFileArchive(f)).load(loadedSourceClassNames);
            } else {
                throw new OperationNotSupportedException("This file extension is not supported");
            }
        } else {
            this.currentClassLoader = new SimpleJarLoader(f).load(loadedSourceClassNames);
        }
        return loadedSourceClassNames;
    }

    public void clearConfig() {
        this.currentClassLoader = null;
    }

    public String generate(String className, String inputText, Setting setting)
            throws OperationNotSupportedException, ClassNotFoundException, JsonProcessingException, ParseException {
        if (className == null || className.trim().isEmpty()) {
            throw new RuntimeException("Classname required!");
        }
        Class<?> clazz = this.getCurrentClassLoader().loadClass(className);
        JsonNode jsonNode = this.objectMapper.readTree(inputText);
        String classSimpleName = clazz.getSimpleName();
        String varBaseName = classSimpleName.substring(0, 1).toLowerCase(Locale.ROOT) + classSimpleName.substring(1);
        if (jsonNode.isObject()) {
            return this.convertObjectNode((ObjectNode) jsonNode, clazz, varBaseName, setting);
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
                    sb.append(this.convertObjectNode((ObjectNode) arrayNode.get(i), clazz, varName, setting));
                    sb.append(varListName).append(".add(").append(varName).append(");\n");
                } else throw new OperationNotSupportedException("Nested array json not supported");
            }
            return sb.toString();
        } else throw new OperationNotSupportedException("Unrecognized JSON type");
    }

    private String convertObjectNode(ObjectNode objectNode, Class<?> clazz, String varName,
                                     Setting setting) throws OperationNotSupportedException, ParseException {

//        Map<String, ?> map = this.objectMapper.readValue(inputText, Map.class);
        String classSimpleName = clazz.getSimpleName();
        StringBuilder outputBuilder = new StringBuilder(classSimpleName)
                .append(" ").append(varName)
                .append(" = new ").append(classSimpleName)
                .append("();\n");
        Map<String, Method> methodMap = new HashMap<>();
        this.generateMethodMap(clazz, methodMap);

        Iterator<Map.Entry<String, JsonNode>> fieldIterator = objectNode.fields();
        while (fieldIterator.hasNext()) {
            Map.Entry<String, JsonNode> entry = fieldIterator.next();
            String pascalFieldName = setting.getJsonNamingStrategy().getConvertFunction().apply(entry.getKey());
            String getterName = "get" + pascalFieldName;
            Method getter = methodMap.get(getterName);
            if (getter == null) {
                continue;
            }
            Class<?> fieldType = getter.getReturnType();
            String val;
            if (entry.getValue() instanceof NullNode) {
                if (setting.isIgnoreNull()) continue;
                val = "null";
            } else if (fieldType == String.class) {
                val = "\"" + entry.getValue().asText() + "\"";
            } else if (Number.class.isAssignableFrom(fieldType)) {
                if (fieldType == Double.class) {
                    val = entry.getValue().asText() + "D";
                } else if (fieldType == Float.class) {
                    val = entry.getValue().asText() + "F";
                } else if (fieldType == Long.class) {
                    val = entry.getValue().asText() + "L";
                } else if (fieldType == BigDecimal.class) {
                    val = "new BigDecimal(\"" + entry.getValue().asText() + "\")";
                } else {
                    val = entry.getValue().asText();
                }
            } else if (fieldType == Date.class) {
                Date date = setting.parseDate(entry.getValue().asText());
                val = "new Date(" + date.getTime() + "L)";
            } else {
                throw new OperationNotSupportedException(String.format("Type %s not supported", fieldType.getName()));
            }
            String setterName = "set" + pascalFieldName;
            outputBuilder.append(varName)
                    .append(".").append(setterName)
                    .append("(").append(val).append(");");
            if (!(entry.getValue() instanceof NullNode) && fieldType == Date.class) {
                outputBuilder.append(" // ").append(entry.getValue());
            }
            outputBuilder.append("\n");
        }
        return outputBuilder.toString();
    }

    private void generateMethodMap(Class<?> clazz, Map<String, Method> methodMap) {
        for (Method method: clazz.getDeclaredMethods()) {
            methodMap.put(method.getName(), method);
        }
        if (clazz.getSuperclass() != null) {
            generateMethodMap(clazz.getSuperclass(), methodMap);
        }
    }

}
