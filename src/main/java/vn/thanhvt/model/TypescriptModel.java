package vn.thanhvt.model;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lombok.Builder;
import lombok.Data;
import vn.thanhvt.util.StringUtil;

/**
 * @author pysga
 * @created 5/22/2022 - 12:26 AM
 * @project json-to-code-generator
 * @since 1.0
 **/
@Data
@Builder
public class TypescriptModel {

    private String javaClassName;

    private String typescriptClassName;

    private List<TypescriptModelProperty> properties;

    public static TypescriptModel fromClass(Class<?> clazz, ClassLoader classLoader) throws ClassNotFoundException {
        List<TypescriptModelProperty> properties = new ArrayList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            // check getter method
            String methodName = method.getName();
            if (methodName.startsWith("get") && methodName.length() > 3 && method.getParameterCount() == 0) {
                String fieldName = methodName.substring(3, 4).toLowerCase(Locale.ROOT) + methodName.substring(4);
                TypescriptModelProperty property = TypescriptModelProperty.from(fieldName, method, classLoader);
                properties.add(property);
            }
        }
        String typescriptClassName = StringUtil.removeEntityDtoSuffix(clazz.getSimpleName());
        return TypescriptModel.builder()
            .javaClassName(clazz.getName())
            .typescriptClassName(typescriptClassName)
            .properties(properties)
            .build();
    }

}
