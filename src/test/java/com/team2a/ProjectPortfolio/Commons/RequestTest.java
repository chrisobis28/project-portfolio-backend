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
        assertTrue(r.isCounterOffer());
    }

    @Test
    void testSetLinksChanged() {
        UUID id1 = UUID.randomUUID();
        Request r = new Request("newTitle", "newDescription", true, new Account(), new Project());
        Link l = new Link("name", "url");

        List<Link> links = new ArrayList<>();
        links.add(l);

        r.setLinksChanged(links);

        assertEquals(links, r.getLinks());
    }

    @Test
    void testSetCollaboratorsChanged() {
        UUID id1 = UUID.randomUUID();
        Request r = new Request("newTitle", "newDescription", true, new Account(), new Project());

        Collaborator c = new Collaborator("name");

        r.setCollaboratorsChanged(List.of(c));

        assertEquals(r.getCollaborators(), List.of(c));
    }

    @Test
    void testSetTagsChanged() {
        UUID id1 = UUID.randomUUID();
        Request r = new Request("newTitle", "newDescription", true, new Account(), new Project());

        Tag t = new Tag("name", "color");

        r.setTagsChanged(List.of(t));
        assertEquals(r.getTags(), List.of(t));
    }

    @Test
    void testSetMediaChanged() {
        UUID id1 = UUID.randomUUID();
        Request r = new Request("newTitle", "newDescription", true, new Account(), new Project());

        Media m = new Media("lol", "path");

        r.setMediaChanged(List.of(m));
        assertEquals(r.getMedia(), List.of(m));
    }

    @Test
    void testGetLinksEmpty() {
        UUID id1 = UUID.randomUUID();
        Request r = new Request("newTitle", "newDescription", true, new Account(), new Project());


        assertEquals(new ArrayList<>(), r.getLinks());
    }

    @Test
    void testGetCollaboratorsEmpty() {
        UUID id1 = UUID.randomUUID();
        Request r = new Request("newTitle", "newDescription", true, new Account(), new Project());


        assertEquals(new ArrayList<>(), r.getCollaborators());
    }

    @Test
    void testGetTagsEmpty() {
        UUID id1 = UUID.randomUUID();
        Request r = new Request("newTitle", "newDescription", true, new Account(), new Project());


        assertEquals(new ArrayList<>(), r.getTags());
    }

    @Test
    void testGetMediaEmpty() {
        UUID id1 = UUID.randomUUID();
        Request r = new Request("newTitle", "newDescription", true, new Account(), new Project());


        assertEquals(new ArrayList<>(), r.getMedia());
    }

}