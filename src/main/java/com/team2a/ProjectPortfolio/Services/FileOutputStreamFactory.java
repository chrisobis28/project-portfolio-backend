package com.team2a.ProjectPortfolio.Services;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class FileOutputStreamFactory {
    public FileOutputStream create (String path) throws FileNotFoundException {
        return new FileOutputStream(path);
    }
}
