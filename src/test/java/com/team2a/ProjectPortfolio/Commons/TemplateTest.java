package com.team2a.ProjectPortfolio.Commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TemplateTest {

    @Test
    public void testTemplate() {
        Template template = new Template("test","test",5);
        assertEquals("test",template.getTemplateName());
        assertEquals(5,template.getNumberOfCollaborators());

    }
}