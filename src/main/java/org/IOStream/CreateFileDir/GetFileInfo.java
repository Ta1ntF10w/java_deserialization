package org.IOStream.CreateFileDir;

import java.io.File;

public class GetFileInfo {
    public static void main(String[] args) {
        File file = new File("src/main/java/org/IOStream/CreateFileDir/new1.txt");
        System.out.println(file.getName());
        System.out.println(file.getAbsolutePath());
        System.out.println(file.length());
        System.out.println(file.getParent());
        System.out.println(file.getParentFile());

    }
}
