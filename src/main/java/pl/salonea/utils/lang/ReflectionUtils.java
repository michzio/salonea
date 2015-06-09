package pl.salonea.utils.lang;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * Created by michzio on 06/06/15.
 */
public class ReflectionUtils {

    public static List<Field> getAllDeclaredFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            fields = getAllDeclaredFields(fields, type.getSuperclass());
        }

        return fields;
    }

    public static Field findField(Class<?> type, String name) throws NoSuchFieldException {

        if(type == null) throw new NoSuchFieldException();

        try {
            Field field = type.getDeclaredField(name);
            return field;
        } catch (NoSuchFieldException e) {

            if(type.getSuperclass() != null) {
                return findField(type.getSuperclass(), name);
            } else {
                e.printStackTrace();
                throw new NoSuchFieldException();
            }
        }
    }
}
