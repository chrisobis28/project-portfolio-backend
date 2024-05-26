package com.team2a.ProjectPortfolio.Services;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class FileOutputStreamFactoryTest {

    @Test
    void create() throws FileNotFoundException {
        FileOutputStreamFactory fileOutputStreamFactory = new FileOutputStreamFactory();
        assertNotNull(fileOutputStreamFactory);
        fileOutputStreamFactory.create("path");
    }
}