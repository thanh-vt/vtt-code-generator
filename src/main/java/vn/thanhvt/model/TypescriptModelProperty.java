package vn.thanhvt.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.temporal.Temporal;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import vn.thanhvt.constant.TypescriptDataType;
import vn.thanhvt.util.StringUtil;

/**
 * @author pysga
 * @created 5/21/2022 - 12:35 AM
 * @project json-to-code-generator
 * @since 1.0
 **/
@Data
@Builder
public class TypescriptModelProperty {

    private String fieldName;

    private TypescriptDataType dataType;

    private String customDataType;

    private TypescriptCollection collection;

    private TypescriptEnum enumeration;

    private boolean nullable;

    public static TypescriptModelProperty from(String fieldName, Method getter, ClassLoader classLoader)
        throws ClassNotFoundException {
        Class<?> clazz = getter.getReturnType();
        TypescriptDataType dataType = getDataType(clazz);

        String customDataType = null;
        if (TypescriptDataType.NESTED.equals(dataType)) {
            customDataType = StringUtil.removeEntityDtoSuffix(clazz.getSimpleName());
        }
        TypescriptCollection typescriptCollection = null;
        TypescriptEnum typescriptEnum = null;
        if (TypescriptDataType.COLLECTION.equals(dataType)) {
//                    String[] collection= method.getReturnType().toString().split("\\.");
//                    String collectionType = collection[collection.length - 1];
            ParameterizedType collectionType = (ParameterizedType) getter.getGenericReturnType();
            Type paramType = collectionType.getActualTypeArguments()[0];
            Class<?> collectionContentClass = classLoader.loadClass(paramType.getTypeName());

            TypescriptDataType collectionContentType = TypescriptModelProperty.getDataType(collectionContentClass);
            String customCollectionContentType = null;
            if (TypescriptDataType.COLLECTION.equals(collectionContentType)) {
                collectionContentType = TypescriptDataType.ANY;
            } else if (TypescriptDataType.NESTED.equals(collectionContentType)) {
                customCollectionContentType = StringUtil.removeEntityDtoSuffix(collectionContentClass.getSimpleName());
            }
            typescriptCollection = TypescriptCollection.builder()
                .contentType(collectionContentType)
                .customContentType(customCollectionContentType)
                .nestedCollection(null)
                .build();
//            String collectionContent = getter.getGenericReturnType().getTypeName().split("<")[1].split(">")[0];
//            property.setCollectionContentType(collectionContent);
        } else if (TypescriptDataType.ENUM.equals(dataType)) {
            Class<?> enumValueClass;
            Method enumValueGetter;
            TypescriptDataType enumValueType;
            String customEnumValueType = null;
            String enumName = StringUtil.removeEntityDtoSuffix(clazz.getSimpleName());
            Map<String, String> values = new HashMap<>();
            try {
                enumValueGetter = clazz.getDeclaredMethod("getValue");
                enumValueClass = enumValueGetter.getReturnType();
                enumValueType = TypescriptModelProperty.getDataType(enumValueClass);
                if (TypescriptDataType.STRING.equals(enumValueType)) {
                    for (Object obj : clazz.getEnumConstants()) {
                        try {
                            String name = ((Enum<?>) obj).name();
                            String val = (String) enumValueGetter.invoke(obj);
                            values.put(name, val);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                    typescriptEnum = TypescriptEnum.builder()
                        .name(enumName)
                        .valueType(enumValueType)
                        .customValueType(customEnumValueType)
                        .values(values)
                        .build();
                }
                if (TypescriptDataType.NUMBER.equals(enumValueType)) {
                    for (Object obj : clazz.getEnumConstants()) {
                        try {
                            String name = ((Enum<?>) obj).name();
                            Number val = (Number) enumValueGetter.invoke(obj);
                            values.put(name, val.toString());
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                    typescriptEnum = TypescriptEnum.builder()
                        .name(enumName)
                        .valueType(enumValueType)
                        .customValueType(customEnumValueType)
                        .values(values)
                        .build();
                }
            } catch (NoSuchMethodException e) {
                enumValueClass = String.class;
                enumValueType = TypescriptDataType.STRING;
                for (Object obj : clazz.getEnumConstants()) {
                    String name = ((Enum<?>) obj).name();
                    values.put(name, name);
                }
                typescriptEnum = TypescriptEnum.builder()
                    .name(enumName)
                    .valueType(enumValueType)
                    .customValueType(customEnumValueType)
                    .values(values)
                    .build();
            }


        }
        return TypescriptModelProperty
            .builder()
            .fieldName(fieldName)
            .dataType(dataType)
            .customDataType(customDataType)
            .collection(typescriptCollection)
            .enumeration(typescriptEnum)
            .nullable(!clazz.isPrimitive())
            .build();
    }

    public static TypescriptDataType getDataType(Class<?> clazz) {
        if (String.class.equals(clazz)) {
            return TypescriptDataType.STRING;
        } else if (Number.class.isAssignableFrom(clazz)) {
            return TypescriptDataType.NUMBER;
        } else if (Boolean.class.isAssignableFrom(clazz)) {
            return TypescriptDataType.BOOLEAN;
        } else if (Temporal.class.isAssignableFrom(clazz) || Date.class.isAssignableFrom(clazz)) {
            return TypescriptDataType.DATE;
        } else if (clazz.isArray() || Collection.class.isAssignableFrom(clazz)) {
            return TypescriptDataType.COLLECTION;
        } else if (clazz.isEnum()) {
            return TypescriptDataType.ENUM;
        } else {
            return TypescriptDataType.NESTED;
        }
    }

    @Data
    @Builder
    public static class TypescriptCollection {

        private TypescriptDataType contentType;

        private String customContentType;

        private TypescriptCollection nestedCollection;

    }

    @Data
    @Builder
    public static class TypescriptEnum {

        private String name;

        private TypescriptDataType valueType;

        private String customValueType;

        private Map<String, String> values;

    }

}
