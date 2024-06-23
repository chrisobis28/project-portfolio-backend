package com.team2a.ProjectPortfolio.Commons;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RequestTest {

    @Test
    void testConstructor() {
        Request r = new Request("newTitle", "newDescription", true, new Account(), new Project());
        assertEquals(r.getNewTitle(), "newTitle");
        assertEquals(r.getNewDescription(), "newDescription");
        assertTrue(r.getIsCounterOffer());
    }

//    @Test
//    void testSetLinksChanged() {
//        UUID id1 = UUID.randomUUID();
//        Request r = new Request("newTitle", "newDescription", true, new Account(), new Project());
//        Link l = new Link("name", "url");
//
//        List<Link> links = new ArrayList<>();
//        links.add(l);
//
//        r.setLinksChanged(links);
//
//        assertEquals(links, r.getLinks());
//    }
//
//    @Test
//    void testSetCollaboratorsChanged() {
//        UUID id1 = UUID.randomUUID();
//        Request r = new Request("newTitle", "newDescription", true, new Account(), new Project());
//
//        Collaborator c = new Collaborator("name");
//
//        r.setCollaboratorsChanged(List.of(c));
//
//        assertEquals(r.getCollaborators(), List.of(c));
//    }
//
//    @Test
//    void testSetTagsChanged() {
//        UUID id1 = UUID.randomUUID();
//        Request r = new Request("newTitle", "newDescription", true, new Account(), new Project());
//
//        Tag t = new Tag("name", "color");
//
//        r.setTagsChanged(List.of(t));
//        assertEquals(r.getTags(), List.of(t));
//    }
//
//    @Test
//    void testSetMediaChanged() {
//        UUID id1 = UUID.randomUUID();
//        Request r = new Request("newTitle", "newDescription", true, new Account(), new Project());
//
//        Media m = new Media("lol", "path");
//
//        r.setMediaChanged(List.of(m));
//        assertEquals(r.getMedia(), List.of(m));
//    }

    @Test
    void testGetLinksEmpty() {
        UUID id1 = UUID.randomUUID();
        Request r = new Request("newTitle", "newDescription", true, new Account(), new Project());


        assertEquals(new ArrayList<>(), r.getLinks());
    }

    @Test
    void testGetLinks() {
        Request r = new Request();
        Link m = new Link();
        RequestLinkProject rmp = new RequestLinkProject(new Request(), m, false);
        r.setRequestLinkProjects(List.of(rmp));
        assertEquals(r.getLinks(), List.of(m));
    }

    @Test
    void testGetCollaboratorsEmpty() {
        UUID id1 = UUID.randomUUID();
        Request r = new Request("newTitle", "newDescription", true, new Account(), new Project());


        assertEquals(new ArrayList<>(), r.getCollaborators());
    }

    @Test
    void testGetCollaborators() {
        Request r = new Request();
        Collaborator m = new Collaborator();
        RequestCollaboratorsProjects rmp = new RequestCollaboratorsProjects( m, new Request(),false);
        r.setRequestCollaboratorsProjects(List.of(rmp));
        assertEquals(r.getCollaborators(), List.of(m));
    }

    @Test
    void testGetTagsEmpty() {
        UUID id1 = UUID.randomUUID();
        Request r = new Request("newTitle", "newDescription", true, new Account(), new Project());


        assertEquals(new ArrayList<>(), r.getTags());
    }

    @Test
    void testGetTags() {
        Request r = new Request();
        Tag m = new Tag();
        RequestTagProject rmp = new RequestTagProject(new Request(),m, false);
        r.setRequestTagProjects(List.of(rmp));
        assertEquals(r.getTags(), List.of(m));
    }

    @Test
    void testGetMediaEmpty() {
        UUID id1 = UUID.randomUUID();
        Request r = new Request("newTitle", "newDescription", true, new Account(), new Project());


        assertEquals(new ArrayList<>(), r.getMedia());
    }

    @Test
    void testGetMedia() {
        Request r = new Request();
        Media m = new Media();
        RequestMediaProject rmp = new RequestMediaProject(new Request(),m, false);
        r.setRequestMediaProjects(List.of(rmp));
        assertEquals(r.getMedia(), List.of(m));
    }

}