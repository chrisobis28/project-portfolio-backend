package com.team2a.ProjectPortfolio;

import com.team2a.ProjectPortfolio.Commons.*;
import com.team2a.ProjectPortfolio.Repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CollaboratorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CollaboratorRepository collaboratorRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectsToCollaboratorsRepository projectsToCollaboratorsRepository;

    private UUID projectId;
    private Collaborator collaborator1;
    private Collaborator collaborator2;
    @BeforeEach
    public void setup() {
        projectsToCollaboratorsRepository.deleteAll();
        collaboratorRepository.deleteAll();
        projectRepository.deleteAll();

        Project project = new Project("Test Project", "Description", "Bibtex", false);
        project = projectRepository.saveAndFlush(project);
        projectId = project.getProjectId();

        collaborator1 = new Collaborator("Test1");
        collaborator2 = new Collaborator("Test2");
        Collaborator collaborator3 = new Collaborator("Test3");

        collaborator1 = collaboratorRepository.saveAndFlush(collaborator1);
        collaborator2 = collaboratorRepository.saveAndFlush(collaborator2);
        collaborator3 = collaboratorRepository.saveAndFlush(collaborator3);

        String role = "Role";
        projectsToCollaboratorsRepository.saveAndFlush(new ProjectsToCollaborators(project, collaborator2,role));
        projectsToCollaboratorsRepository.saveAndFlush(new ProjectsToCollaborators(project, collaborator3,role));

    }

    @Test
    public void getCollaboratorsByProjectId() throws Exception {
        mockMvc.perform(get(Routes.COLLABORATOR + "/" + projectId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Test2")))
                .andExpect(jsonPath("$[1].name", is("Test3")));
    }
    @Test
    public void addCollaboratorToProject() throws Exception {
        assertThat(projectsToCollaboratorsRepository.findAllByCollaboratorCollaboratorId(collaborator1.getCollaboratorId()).size()).isEqualTo(0);
        mockMvc.perform(post(Routes.COLLABORATOR + "/" + projectId +"/" +collaborator1.getCollaboratorId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("Backend"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(collaborator1.getName() )))
                .andExpect(jsonPath("$.collaboratorId", is(collaborator1.getCollaboratorId().toString())));
        assertThat(projectsToCollaboratorsRepository.findAllByCollaboratorCollaboratorId(collaborator1.getCollaboratorId()).size()).isEqualTo(1);
    }
    @Test
    public void editCollaboratorOfProject() throws Exception {
        assertThat(collaboratorRepository.findAll().size()).isEqualTo(3);
        mockMvc.perform(put(Routes.COLLABORATOR +"/"+ collaborator1.getCollaboratorId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("New Name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath( "$.name", is("New Name")));
        assertThat(collaboratorRepository.findAll().size()).isEqualTo(3);
    }
    @Test
    public void deleteCollaborator() throws Exception {
        assertThat(collaboratorRepository.findAll().size()).isEqualTo(3);
        assertThat(projectsToCollaboratorsRepository.findAllByCollaboratorCollaboratorId(collaborator2.getCollaboratorId()).size()).isEqualTo(1);
        assertThat(projectsToCollaboratorsRepository.findAll().size()).isEqualTo(2);
        mockMvc.perform(delete(Routes.COLLABORATOR +"/"+ collaborator2.getCollaboratorId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath( "$", is("Deleted collaborator")));
        assertThat(projectsToCollaboratorsRepository.findAll().size()).isEqualTo(1);
        assertThat(projectsToCollaboratorsRepository.findAllByCollaboratorCollaboratorId(collaborator2.getCollaboratorId()).size()).isEqualTo(0);
        assertThat(collaboratorRepository.findAll().size()).isEqualTo(2);
    }
    @Test
    public void deleteCollaboratorFromProject() throws Exception {
        assertThat(projectsToCollaboratorsRepository.findAllByCollaboratorCollaboratorId(collaborator2.getCollaboratorId()).size()).isEqualTo(1);
        mockMvc.perform(delete(Routes.COLLABORATOR +"/"+ projectId+"/"+collaborator2.getCollaboratorId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath( "$", is("Deleted collaborator")));
       assertThat(projectsToCollaboratorsRepository.findAllByCollaboratorCollaboratorId(collaborator2.getCollaboratorId()).size()).isEqualTo(0);
    }


    @Test
    public void getCollaboratorsByProjectIdNotFound() throws Exception {
        mockMvc.perform(get(Routes.COLLABORATOR + "/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    @Test
    public void addCollaboratorToProjectNotFound() throws Exception {
        assertThat(projectsToCollaboratorsRepository.findAllByCollaboratorCollaboratorId(UUID.randomUUID()).size()).isEqualTo(0);
        mockMvc.perform(post(Routes.COLLABORATOR + "/" + UUID.randomUUID()+"/"+UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("Backend"))
                .andExpect(status().isNotFound());
        assertThat(projectsToCollaboratorsRepository.findAllByCollaboratorCollaboratorId(collaborator1.getCollaboratorId()).size()).isEqualTo(0);
    }
    @Test
    public void addNewCollaboratorToProjectNotFound() throws Exception {
        assertThat(collaboratorRepository.findAll().size()).isEqualTo(3);
        assertThat(collaboratorRepository.findAllByName("newName").size()).isEqualTo(0);
        mockMvc.perform(post(Routes.COLLABORATOR + "/" + UUID.randomUUID()+"/"+UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("Backend"))
                .andExpect(status().isNotFound());
        assertThat(collaboratorRepository.findAllByName("newName").size()).isEqualTo(0);
        assertThat(collaboratorRepository.findAll().size()).isEqualTo(3);
    }
    @Test
    public void editCollaboratorOfProjectNotFound() throws Exception {
        assertThat(collaboratorRepository.findAll().size()).isEqualTo(3);
        mockMvc.perform(put(Routes.COLLABORATOR +"/"+ UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("New Name"))
                .andExpect(status().isNotFound());
        assertThat(collaboratorRepository.findAll().size()).isEqualTo(3);
    }
    @Test
    public void deleteCollaboratorNotFound() throws Exception {
        assertThat(collaboratorRepository.findAll().size()).isEqualTo(3);
        assertThat(projectsToCollaboratorsRepository.findAllByCollaboratorCollaboratorId(collaborator2.getCollaboratorId()).size()).isEqualTo(1);
        mockMvc.perform(delete(Routes.COLLABORATOR +"/"+ UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        assertThat(projectsToCollaboratorsRepository.findAllByCollaboratorCollaboratorId(collaborator2.getCollaboratorId()).size()).isEqualTo(1);
        assertThat(collaboratorRepository.findAll().size()).isEqualTo(3);
    }
    @Test
    public void deleteCollaboratorFromProjectNotFound() throws Exception {
        assertThat(projectsToCollaboratorsRepository.findAllByCollaboratorCollaboratorId(collaborator2.getCollaboratorId()).size()).isEqualTo(1);
        mockMvc.perform(delete(Routes.COLLABORATOR +"/"+ UUID.randomUUID()+"/"+UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        assertThat(projectsToCollaboratorsRepository.findAllByCollaboratorCollaboratorId(collaborator2.getCollaboratorId()).size()).isEqualTo(1);
    }
    @Test
    public void deleteCollaboratorExceptionNotFound() throws Exception {
        assertThat(collaboratorRepository.findAll().size()).isEqualTo(3);
        mockMvc.perform(delete(Routes.COLLABORATOR +"/"+ UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        assertThat(collaboratorRepository.findAll().size()).isEqualTo(3);
    }
}
