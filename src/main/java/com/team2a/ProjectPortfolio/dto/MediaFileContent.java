package com.team2a.ProjectPortfolio.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class MediaFileContent {

    @Getter
    @NotNull (message = "Filename must be specified")
    private String fileName;

    @Getter
    @NotNull (message = "FileContent must be specified")
    private String fileContent;

    @Getter
    @NotNull (message = "FilePath must be specified")
    private String filePath;

    /**
     * The constructor for the mediaFileContentDTO
     * @param fileName the filename
     * @param fileContent the file content
     * @param filePath the file path
     */
    public MediaFileContent (String fileName, String filePath, String fileContent) {
        this.fileName = fileName;
        this.fileContent = fileContent;
        this.filePath = filePath;
    }

    @Override
    public String toString () {
        return "MediaFileContent{" +
                "fileName='" + fileName + '\'' +
                ", fileContent='" + fileContent + '\'' +
                ", filePath='" + filePath + '\'' +
                '}';
    }
}
