package com.increff.pos.util;

import com.increff.pos.exception.ApiException;
import java.lang.reflect.Field;
import java.util.Objects;
public class ConvertUtil {
    public static <T> void mapProperties(Object source, T target) throws ApiException {
        Class<?> sourceClass = source.getClass();
        Class<?> targetClass = target.getClass();
        
        for (Field sourceField : sourceClass.getDeclaredFields()) {
            try {
                sourceField.setAccessible(true);
                String fieldName = sourceField.getName();
                Field targetField = findFieldInHierarchy(targetClass, fieldName);
                if (!Objects.isNull(targetField)) {
                    targetField.setAccessible(true);
                    if (sourceField.getType().equals(targetField.getType())) {
                        targetField.set(target, sourceField.get(source));
                    } else {
                        throw new ApiException("Field type mismatch for field: " + sourceField.getName());
                    }
                }
            } catch (IllegalAccessException e) {
                throw new ApiException("Error mapping field: " + sourceField.getName());
            }
        }
    }

    private static Field findFieldInHierarchy(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (!Objects.isNull(superClass) && !superClass.equals(Object.class)) {
                return findFieldInHierarchy(superClass, fieldName);
            }
            return null;
        }
    }
}
