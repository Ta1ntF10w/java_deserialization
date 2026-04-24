package org.src;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.HashMap;

public class DeSerializationTest {
    public static void main(String[] args) throws Exception {
        Object o = deSerialize();

    }
    public static Object deSerialize() throws Exception {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("ser.bin"));
        return ois.readObject();
    }

}
