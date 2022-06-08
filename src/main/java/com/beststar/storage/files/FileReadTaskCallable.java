package com.beststar.storage.files;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

public class FileReadTaskCallable implements Callable<String> {
    private String filePath;
    private long readLineNumber;
    
    public FileReadTaskCallable(String filePath, long readLineNumber) {
        this.filePath = filePath;
        this.readLineNumber = readLineNumber;
    }

    @Override
    public String call() throws Exception {
        String line = "";
        try {
            Path path = Paths.get(filePath);
            try(BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
                Stream<String> lines = reader.lines().skip(readLineNumber - 1);
                line = lines.findFirst().get();
            }  
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return line;
    }
}
