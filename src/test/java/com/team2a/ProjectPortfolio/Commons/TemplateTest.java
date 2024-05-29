package com.team2a.ProjectPortfolio.Commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TemplateTest {

    @Test
    public void testTemplate() {
        Template template = new Template("test","test","test",5);
        assertEquals("test",template.getTemplateName());
        assertEquals("test",template.getStandardBibtex());
        assertEquals("test",template.getStandardBibtex());
        assertEquals(5,template.getNumberOfCollaborators());

    }
}