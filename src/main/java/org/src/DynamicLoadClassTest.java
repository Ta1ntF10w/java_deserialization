package org.src;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DynamicLoadClassTest {
    public static void main(String[] args) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Class c = ClassLoader.class;
        Method method = c.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
        method.setAccessible(true);
        byte[] code = Files.readAllBytes(Paths.get("D:\\evilCode\\Test.class"));
        Class aClass = (Class) method.invoke(ClassLoader.getSystemClassLoader(), "org.src.Test", code, 0, code.length);
        aClass.newInstance();
    }
}
