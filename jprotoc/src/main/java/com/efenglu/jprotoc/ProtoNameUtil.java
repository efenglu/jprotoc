package com.efenglu.jprotoc;

import org.apache.commons.lang.StringUtils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ProtoNameUtil {
    private ProtoNameUtil() {
        throw new UnsupportedOperationException();
    }

    public static String getJavaFieldName(String fieldName) {
        String name = underscoreToCamel(fieldName);
        return StringUtils.uncapitalize(name) + "_";
    }

    private static String underscoreToCamel(String fieldName) {
        if (fieldName.contains("_")) {
            return Stream.of(fieldName.split("_"))
                    .map(StringUtils::capitalize)
                    .collect(Collectors.joining());
        } else {
            return fieldName;
        }
    }

    public static String getJavaMethodNameForField(String fieldName) {
        String name = underscoreToCamel(fieldName);
        return StringUtils.capitalize(name);
    }
}
