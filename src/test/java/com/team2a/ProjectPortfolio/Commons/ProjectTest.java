package com.team2a.ProjectPortfolio.Commons;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProjectTest {

    @Test
    void testConstructor() {
        UUID id1 = UUID.randomUUID();
        Project p = new Project("title", "description", true);
        assertEquals("title", p.getTitle());
        assertEquals("description", p.getDescription());
        assertTrue(p.getArchived());
        assertEquals(new ArrayList<>(), p.getMedia());
        assertEquals(new ArrayList<>(), p.getProjectsToAccounts());
        assertEquals(new ArrayList<>(), p.getProjectsToCollaborators());
        assertEquals(new ArrayList<>(), p.getTagsToProjects());
        assertEquals(new ArrayList<>(), p.getLinks());
        assertEquals(new ArrayList<>(), p.getRequests());
    }

    @Test
    void testConstructorWithTemplate() {
        Template template = new Template();
        Project p = new Project("title", "description", true, template);
        assertEquals("title", p.getTitle());
        assertEquals("description", p.getDescription());
        assertTrue(p.getArchived());
        assertEquals(template, p.getTemplate());
    }

    @Test
    void testSettersAndGetters() {
        Project p = new Project();
        UUID projectId = UUID.randomUUID();
        p.setProjectId(projectId);
        assertEquals(projectId, p.getProjectId());

        p.setTitle("new title");
        assertEquals("new title", p.getTitle());

        p.setDescription("new description");
        assertEquals("new description", p.getDescription());

        p.setArchived(false);
        assertFalse(p.getArchived());

        Template template = new Template();
        p.setTemplate(template);
        assertEquals(template, p.getTemplate());

        List<Media> mediaList = new ArrayList<>();
        mediaList.add(new Media());
        p.setMedia(mediaList);
        assertEquals(mediaList, p.getMedia());

        List<ProjectsToAccounts> projectsToAccountsList = new ArrayList<>();
        projectsToAccountsList.add(new ProjectsToAccounts());
        p.setProjectsToAccounts(projectsToAccountsList);
        assertEquals(projectsToAccountsList, p.getProjectsToAccounts());

        List<ProjectsToCollaborators> projectsToCollaboratorsList = new ArrayList<>();
        projectsToCollaboratorsList.add(new ProjectsToCollaborators());
        p.setProjectsToCollaborators(projectsToCollaboratorsList);
        assertEquals(projectsToCollaboratorsList, p.getProjectsToCollaborators());

        List<TagsToProject> tagsToProjectsList = new ArrayList<>();
        tagsToProjectsList.add(new TagsToProject());
        p.setTagsToProjects(tagsToProjectsList);
        assertEquals(tagsToProjectsList, p.getTagsToProjects());

        List<Link> linksList = new ArrayList<>();
        linksList.add(new Link());
        p.setLinks(linksList);
        assertEquals(linksList, p.getLinks());

        List<Request> requestsList = new ArrayList<>();
        requestsList.add(new Request());
        p.setRequests(requestsList);
        assertEquals(requestsList, p.getRequests());
    }

    @Test
    void testEqualsAndHashCode() {
        Project p1 = new Project("title1", "description1", false);
        Project p2 = new Project("title1", "description1", false);
        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }
}
