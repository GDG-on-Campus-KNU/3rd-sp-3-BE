package gdsc.comunity.util;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

public class TestUtil {
    public static void setField(Object object, String fieldName, Object value) {
        Field field;
        try {
            field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getTestDate() {
        return LocalDateTime.now();
    }

    public static void setDate(Object object) {
        // set createdDate, modifiedDate
        setField(object, "createdDate", getTestDate());
        setField(object, "modifiedDate", getTestDate());
    }
}
