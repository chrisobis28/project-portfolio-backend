package com.team2a.ProjectPortfolio.Services;

import com.team2a.ProjectPortfolio.Commons.Collaborator;
import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Commons.ProjectsToCollaborators;
import com.team2a.ProjectPortfolio.Commons.Request;
import com.team2a.ProjectPortfolio.Repositories.*;
import jakarta.persistence.EntityNotFoundException;
import com.team2a.ProjectPortfolio.dto.CollaboratorTransfer;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private RequestCollaboratorsProjectsRepository
            requestCollaboratorsProjectsRepository;
    @InjectMocks
    private CollaboratorService cs;

    @BeforeEach
    void setUp () {
        cr = Mockito.mock(CollaboratorRepository.class);
        requestRepository = Mockito.mock(RequestRepository.class);
        requestCollaboratorsProjectsRepository =
                Mockito.mock(RequestCollaboratorsProjectsRepository.class);
        cs = new CollaboratorService(ptc, cr, projectRepository,
                requestRepository, requestCollaboratorsProjectsRepository);
    }

    @Test
    void testGetCollaboratorsByProjectIdNotFound () {
        UUID projectId = UUID.randomUUID();
        assertThrows(ResponseStatusException.class, () -> cs.getCollaboratorsByProjectId(projectId));
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
        Project project = new Project("Test", "Test", false);
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

    @Test
    void getAllCollaborators () {
        Collaborator c1 = new Collaborator("coll1");
        when(cr.findAll()).thenReturn(List.of(c1));
        assertEquals(cs.getAllCollaborators(), List.of(c1));
    }

    @Test
    void testCreateAndAddCollaboratorToProject_ExistingCollaborator() {
        UUID projectId = UUID.randomUUID();
        String collaboratorName = "John Doe";
        String role = "Developer";

        CollaboratorTransfer collaboratorTransfer = new CollaboratorTransfer(null,collaboratorName, role);

        Project project = new Project();
        project.setProjectId(projectId);

        Collaborator existingCollaborator = new Collaborator();
        existingCollaborator.setCollaboratorId(UUID.randomUUID());
        existingCollaborator.setName(collaboratorName);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(cr.findByName(collaboratorName)).thenReturn(Optional.of(existingCollaborator));
        when(ptc.existsByProjectProjectIdAndCollaboratorCollaboratorId(projectId, existingCollaborator.getCollaboratorId())).thenReturn(true);

        CollaboratorTransfer result = cs.createAndAddCollaboratorToProject(projectId, collaboratorTransfer);

        assertNotNull(result);
        assertEquals(existingCollaborator.getCollaboratorId(), result.getCollaboratorId());
        verify(ptc, times(1)).existsByProjectProjectIdAndCollaboratorCollaboratorId(projectId, existingCollaborator.getCollaboratorId());
        verify(ptc, never()).save(any(ProjectsToCollaborators.class));
    }

    @Test
    void testCreateAndAddCollaboratorToProject_NewCollaborator() {
        UUID projectId = UUID.randomUUID();
        UUID collaboratorId = UUID.randomUUID();
        Collaborator collaborator = new Collaborator();
        collaborator.setCollaboratorId(collaboratorId);
        String collaboratorName = "Jane Smith";
        String role = "Designer";

        CollaboratorTransfer collaboratorTransfer = new CollaboratorTransfer(null, collaboratorName, role);

        Project project = new Project();
        project.setProjectId(projectId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(cr.findByName(collaboratorName)).thenReturn(Optional.empty());
        when(cr.save(any(Collaborator.class))).thenReturn(collaborator);
        CollaboratorTransfer result = cs.createAndAddCollaboratorToProject(projectId, collaboratorTransfer);

        assertNotNull(result);
        assertNotNull(result.getCollaboratorId());
        verify(ptc, times(1)).save(any(ProjectsToCollaborators.class));
    }

    @Test
    void testCreateAndAddCollaboratorToProject_ProjectNotFound() {
        UUID projectId = UUID.randomUUID();
        String collaboratorName = "John Doe";
        String role = "Developer";

        CollaboratorTransfer collaboratorTransfer = new CollaboratorTransfer(null, collaboratorName, role);

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> cs.createAndAddCollaboratorToProject(projectId, collaboratorTransfer));
    }

    @Test
    void testGetCollaboratorsByProjectId() {
        UUID projectId = UUID.randomUUID();
        Project project = new Project();
        project.setProjectId(projectId);

        Collaborator collaborator1 = new Collaborator( "John Doe");
        Collaborator collaborator2 = new Collaborator("Jane Smith");

        List<ProjectsToCollaborators> projectsToCollaboratorsList = new ArrayList<>();
        projectsToCollaboratorsList.add(new ProjectsToCollaborators(project, collaborator1, "Developer"));
        projectsToCollaboratorsList.add(new ProjectsToCollaborators(project, collaborator2, "Designer"));

        when(projectRepository.findById(projectId)).thenReturn(java.util.Optional.of(project));
        when(ptc.findAllByProjectProjectId(projectId)).thenReturn(projectsToCollaboratorsList);

        List<CollaboratorTransfer> result = cs.getCollaboratorsByProjectId(projectId);

        assertNotNull(result);
        assertEquals(2, result.size());
        List<String> collaboratorNames = result.stream().map(CollaboratorTransfer::getName).collect(Collectors.toList());
        assertTrue(collaboratorNames.contains("John Doe"));
        assertTrue(collaboratorNames.contains("Jane Smith"));
    }

    @Test
    void testGetCollaboratorsByProjectId_ProjectNotFound() {
        UUID projectId = UUID.randomUUID();

        when(projectRepository.findById(projectId)).thenReturn(java.util.Optional.empty());

        assertThrows(ResponseStatusException.class, () -> cs.getCollaboratorsByProjectId(projectId));
    }

    @Test
    void testGetCollaboratorsForRequest () {
        Request r = new Request();
        r.setRequestCollaboratorsProjects(new ArrayList<>());
        when(requestRepository.findById(any())).thenReturn(Optional.of(r));
        assertEquals(cs.getCollaboratorsForRequest(UUID.randomUUID()), new ArrayList<>());
    }

    @Test
    void testGetCollaboratorsRequestNotFound () {
        when(requestRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> cs.getCollaboratorsForRequest(UUID.randomUUID()));
    }

    @Test
    void testAddCollaboratorToRequestOk () {
        Request r = new Request();
        Collaborator col = new Collaborator();
        when(requestRepository.findById(any())).thenReturn(Optional.of(r));
        when(cr.findById(any())).thenReturn(Optional.of(col));


        assertEquals(col, cs.addCollaboratorToRequest(UUID.randomUUID(),
                UUID.randomUUID(), false));
        verify(requestCollaboratorsProjectsRepository).save(any());
    }

    @Test
    void testAddCollaboratorToRequestNotFound () {
        when(requestRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> cs.addCollaboratorToRequest(UUID.randomUUID(),
                UUID.randomUUID(), false));
    }

    @Test
    void testAddCollaboratorToRequestNotFound2 () {
        Request r = new Request();
        Collaborator col = new Collaborator();
        when(requestRepository.findById(any())).thenReturn(Optional.of(r));
        when(cr.findById(any())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> cs.addCollaboratorToRequest(UUID.randomUUID(),
                UUID.randomUUID(), false));
    }

}