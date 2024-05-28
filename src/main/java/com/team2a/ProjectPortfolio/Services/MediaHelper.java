package com.team2a.ProjectPortfolio.Services;

import com.team2a.ProjectPortfolio.CustomExceptions.FileNotSavedException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class MediaHelper {
    public String getFileContents (String filename){
        String filePath = System.getProperty("user.dir") + "/assets" + File.separator;
        Path path = Paths.get(filePath + filename);
        byte[] content = null;
        try {
            content = Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Base64.getEncoder().encodeToString(content);
    }
    public String[] getFiles ()
    {
        //https://www.geeksforgeeks.org/spring-boot-file-handling/
        String folderPath = System.getProperty("user.dir") +"/assets";
        File directory= new File(folderPath);
        return directory.list();
    }
    public void saveFile (String path, MultipartFile file) throws RuntimeException {
        try {
            FileOutputStream fileSave = new FileOutputStream(path);
            fileSave.write(file.getBytes());
        }
        catch (IOException e) {
            throw new FileNotSavedException("Problem when Saving File");
        }
    }
}
