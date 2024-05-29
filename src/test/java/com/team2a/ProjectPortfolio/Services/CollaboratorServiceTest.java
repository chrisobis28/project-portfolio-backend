package com.team2a.ProjectPortfolio.Services;

import com.team2a.ProjectPortfolio.Commons.Collaborator;
import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Commons.ProjectsToCollaborators;
import com.team2a.ProjectPortfolio.Repositories.CollaboratorRepository;
import com.team2a.ProjectPortfolio.Repositories.ProjectRepository;
import com.team2a.ProjectPortfolio.Repositories.ProjectsToCollaboratorsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CollaboratorServiceTest {
    @Mock
    private ProjectsToCollaboratorsRepository ptc;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private CollaboratorRepository cr;
    @InjectMocks
    private CollaboratorService cs;

    @BeforeEach
    void setUp () {
        cr = Mockito.mock(CollaboratorRepository.class);
        cs = new CollaboratorService(ptc, cr, projectRepository);
    }

    @Test
    void testGetCollaboratorsByProjectIdSuccess () {
        UUID projectId = UUID.randomUUID();
        Project project = new Project("Test", "Test", "Test", false);
        Collaborator collaborator = new Collaborator("Filip");
        String role = "Role";
        ProjectsToCollaborators projectsToCollaborators = new ProjectsToCollaborators(project, collaborator,role);
        List<ProjectsToCollaborators> projectsToCollaboratorsList = new ArrayList<>();
        projectsToCollaboratorsList.add(projectsToCollaborators);
        when(projectRepository.findById(projectId)).thenReturn(java.util.Optional.of(project));
        when(ptc.findAllByProjectProjectId(projectId)).thenReturn(projectsToCollaboratorsList);
        List<Collaborator> actualResponse = cs.getCollaboratorsByProjectId(projectId);
        assertEquals(1, actualResponse.size());
        assertEquals("Filip", actualResponse.get(0).getName());
    }

    @Test
    void testGetCollaboratorsByProjectIdNotFound () {
        UUID projectId = UUID.randomUUID();
        assertThrows(EntityNotFoundException.class, () -> cs.getCollaboratorsByProjectId(projectId));
    }

    @Test
    void testAddCollaboratorSuccess () {
        UUID projectId = UUID.randomUUID();
        UUID collaboratorId = UUID.randomUUID();
        String role = "Role";
        Project project = new Project("Test", "Test", "Test", false);
        Collaborator collaborator = new Collaborator("Test");
        collaborator.setCollaboratorId(collaboratorId);
        when(projectRepository.findById(projectId)).thenReturn(java.util.Optional.of(project));
        when(cr.findById(collaboratorId)).thenReturn(java.util.Optional.of(collaborator));
        Collaborator actualResponse = cs.addCollaboratorToProject(projectId, collaboratorId,role);
        assertEquals(collaboratorId, actualResponse.getCollaboratorId());
    }

    @Test
    void testAddCollaboratorNotFound () {
        UUID projectId = UUID.randomUUID();
        UUID collaboratorId = UUID.randomUUID();
        String role = "Role";
        assertThrows(EntityNotFoundException.class, () -> cs.addCollaboratorToProject(projectId, collaboratorId,role));
    }

    @Test
    void testEditCollaboratorSuccess () {
        UUID collaboratorId = UUID.randomUUID();
        String collaboratorName = "Andrei";
        Collaborator collaborator = new Collaborator("Filip");
        when(cr.findById(collaboratorId)).thenReturn(java.util.Optional.of(collaborator));
        when(cr.save(any())).thenReturn(collaborator);
        Collaborator actualResponse = cs.editCollaboratorOfProject(collaboratorId, collaboratorName);
        assertEquals(collaboratorName, actualResponse.getName());
    }

    @Test
    void testEditCollaboratorNotFound () {
        UUID collaboratorId = UUID.randomUUID();
        String collaboratorName = "Test";
        assertThrows(EntityNotFoundException.class, () -> cs.editCollaboratorOfProject(collaboratorId, collaboratorName));
    }

    @Test
    void testDeleteCollaboratorSuccess () {
        UUID collaboratorId = UUID.randomUUID();
        Collaborator collaborator = new Collaborator("Filip");
        when(cr.findById(collaboratorId)).thenReturn(java.util.Optional.of(collaborator));
        String response = cs.deleteCollaborator(collaboratorId);
        assertEquals("Deleted collaborator", response);
        verify(cr, times(1)).delete(collaborator);
    }

    @Test
    void testDeleteCollaboratorNotFound () {
        UUID collaboratorId = UUID.randomUUID();
        assertThrows(EntityNotFoundException.class, () -> cs.deleteCollaborator(collaboratorId));
    }


    @Test
    void testDeleteCollaboratorFromProjectSuccess () {
        UUID projectId = UUID.randomUUID();
        UUID collaboratorId = UUID.randomUUID();
        Collaborator collaborator = new Collaborator("Filip");
        Project project = new Project("Test", "Test", "Test", false);
        when(cr.findById(collaboratorId)).thenReturn(java.util.Optional.of(collaborator));
        when(projectRepository.findById(projectId)).thenReturn(java.util.Optional.of(project));
        when(ptc.findAllByProjectProjectIdAndCollaboratorCollaboratorId(projectId, collaboratorId)).
                thenReturn(new ArrayList<>());
        String response = cs.deleteCollaboratorFromProject(projectId, collaboratorId);
        assertEquals("Deleted collaborator", response);
        verify(ptc, times(1)).deleteAll(anyList());
    }


    @Test
    void testDeleteCollaboratorFromProjectNotFound () {
        UUID projectId = UUID.randomUUID();
        UUID collaboratorId = UUID.randomUUID();
        assertThrows(EntityNotFoundException.class, () -> cs.deleteCollaboratorFromProject(projectId, collaboratorId));
    }

    @Test
    void testAddCollaboratorFound () {
        Collaborator c1 = new Collaborator("coll1");
        when(cr.findAllByName("coll1")).thenReturn(List.of(c1));
        assertEquals(cs.addCollaborator("coll1"), c1);
    }

    @Test
    void addCollaboratorNotFound () {
        when(cr.findAllByName("coll1")).thenReturn(List.of());
        //verify(cr).save(new Collaborator("coll1"));
        Collaborator c1 = new Collaborator("coll1");
        when(cr.save(any())).thenReturn(c1);
        assertEquals(cs.addCollaborator("coll1"), c1);
    }

}