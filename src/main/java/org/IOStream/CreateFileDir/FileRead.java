package org.IOStream.CreateFileDir;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileRead {
    public static void main(String[] args) {
        readFile("src/main/java/org/IOStream/CreateFileDir/new1.txt");
    }
    public static void readFile(String fileName) {
        File file = new File(fileName);
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            FileReader fileReader = new FileReader(fileName);
            int len = 0;
            while ((len = fileReader.read()) != -1){
                System.out.print((char)len);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void writeFile(String fileName) {
        File file = new File(fileName);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            String content = "Hello World";
            fileOutputStream.write(content.getBytes());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
