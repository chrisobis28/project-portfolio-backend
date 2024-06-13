package com.team2a.ProjectPortfolio.Services;

import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

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
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return Base64.getEncoder().encodeToString(content);
    }
    public void saveFile (String path, MultipartFile file) throws RuntimeException {
        try (FileOutputStream fileSave = new FileOutputStream(path)) {
            fileSave.write(file.getBytes());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
