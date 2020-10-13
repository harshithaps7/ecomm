package com.example.demo;

import java.lang.reflect.Field;

public class TestUtils {
    public static void InjectObjects(Object target, String fieldName, Object toInject) {
        boolean wasPrivate = false;
        Field f = null;
        try {
            f = target.getClass().getDeclaredField(fieldName);
            if (!f.isAccessible()) {
                f.setAccessible(true);
                wasPrivate = true;
            }
            f.set(target, toInject);
            if (wasPrivate) {
                f.setAccessible(false);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
