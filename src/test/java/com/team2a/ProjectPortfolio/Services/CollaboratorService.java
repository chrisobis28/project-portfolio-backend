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
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CollaboratorService {
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
        cs = new CollaboratorService(ptc, cr, projectRepository);
    }

    @Test
    void testGetCollaboratorsByProjectIdSuccess () {
        UUID projectId = UUID.randomUUID();
        Project project = new Project("Test", "Test", "Test", false);
        Collaborator collaborator = new Collaborator("Filip");
        ProjectsToCollaborators projectsToCollaborators = new ProjectsToCollaborators(project, collaborator);
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
        String collaboratorName = "Filip";
        Project project = new Project("Test", "Test", "Test", false);
        Collaborator collaborator = new Collaborator(collaboratorName);
        when(projectRepository.findById(projectId)).thenReturn(java.util.Optional.of(project));
        when(cr.findAllByName(collaboratorName)).thenReturn(new ArrayList<>());
        when(cr.save(any())).thenReturn(collaborator);
        Collaborator actualResponse = cs.addCollaboratorToProject(projectId, collaboratorName);
        assertEquals(collaboratorName, actualResponse.getName());
    }

    @Test
    void testAddCollaboratorSuccessMultiple () {
        UUID projectId = UUID.randomUUID();
        String collaboratorName = "Filip";
        Project project = new Project("Test", "Test", "Test", false);
        Collaborator collaborator1 = new Collaborator(collaboratorName);
        Collaborator collaborator2 = new Collaborator(collaboratorName);
        List<Collaborator> collaboratorList = new ArrayList<>();
        collaboratorList.add(collaborator1);
        collaboratorList.add(collaborator2);
        when(projectRepository.findById(projectId)).thenReturn(java.util.Optional.of(project));
        when(cr.findAllByName(collaboratorName)).thenReturn(collaboratorList);
        Collaborator actualResponse = cs.addCollaboratorToProject(projectId, collaboratorName);
        assertEquals(collaboratorName, actualResponse.getName());
    }

    @Test
    void testAddCollaboratorSuccessSingle () {
        UUID projectId = UUID.randomUUID();
        String collaboratorName = "Filip";
        Project project = new Project("Test", "Test", "Test", false);
        Collaborator collaborator = new Collaborator(collaboratorName);
        when(projectRepository.findById(projectId)).thenReturn(java.util.Optional.of(project));
        when(cr.findAllByName(collaboratorName)).thenReturn(List.of(collaborator));
        Collaborator actualResponse = cs.addCollaboratorToProject(projectId, collaboratorName);
        assertEquals(collaboratorName, actualResponse.getName());
    }

    @Test
    void testAddCollaboratorNotFound () {
        UUID projectId = UUID.randomUUID();
        String collaboratorName = "Test";
        assertThrows(EntityNotFoundException.class, () -> cs.addCollaboratorToProject(projectId, collaboratorName));
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
}