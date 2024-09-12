package pg.works.junit.spec.runner.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ReflectUtils {
    private ReflectUtils() {
        //no init
    }

    public static Stream<Method> filterClassMethods(Class clazz, Predicate<Method> filter) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(filter);
    }

    public static Method getClassMethod(Class clazz, String method) {
        return filterClassMethods(clazz, m -> Objects.equals(method, m.getName()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("method doesn't exist"));
    }

    public static Object executeMethod(Class clazz, Method clazzMethod, Object... args) throws Exception {
        clazzMethod.setAccessible(true);
        boolean isStaticMethod = Modifier.isStatic(clazzMethod.getModifiers());
        Object receiver;
        if (isStaticMethod) {
            receiver = clazz;
        } else {
            receiver = newInstance(clazz);
        }
        return clazzMethod.invoke(receiver, args);
    }

    public static Object newInstance(Class clazz) {
        Object receiver;
        try {
            var constructor = clazz.getConstructor();
            constructor.setAccessible(true);
            receiver = constructor.newInstance();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
        return receiver;
    }


}
