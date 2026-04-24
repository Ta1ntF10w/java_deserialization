package org.src;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectTest {
    public static void main(String[] args) throws ClassNotFoundException, IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class c = Class.forName("java.lang.Runtime");
        Method runtime = c.getMethod("getRuntime");
        Object o1 = runtime.invoke(c);
        Method method = c.getMethod("exec", String.class);
        method.invoke(o1,"calc");
    }
}
