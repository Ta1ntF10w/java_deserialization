package org.IOStream;

import java.io.File;
import java.io.IOException;

public class CreateFIleTest {
    public static void main(String[] args) throws IOException {
        createFile();
    }
    public static void createFile() throws IOException {
        File file = new File("src/main/java/org/IOStream/CreateFileDir/new1.txt");
        file.createNewFile();
    }
}
