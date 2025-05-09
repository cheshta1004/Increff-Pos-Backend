package com.increff.pos.util;

import com.increff.pos.exception.ApiException;
import java.lang.reflect.Field;

public class ConvertUtil {
    public static <T> void mapProperties(Object source, T target) throws ApiException {
        Class<?> sourceClass = source.getClass();
        Class<?> targetClass = target.getClass();
        for (Field sourceField : sourceClass.getDeclaredFields()) {
            try {
                sourceField.setAccessible(true);
                String fieldName = sourceField.getName();
                Field targetField = targetClass.getDeclaredField(fieldName);
                targetField.setAccessible(true);
                if (sourceField.getType().equals(targetField.getType())) {
                    targetField.set(target, sourceField.get(source));
                } else {
                    throw new ApiException("Field type mismatch for field: " + sourceField.getName());
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new ApiException("Error mapping field: " + sourceField.getName());
            }
        }
    }
}
