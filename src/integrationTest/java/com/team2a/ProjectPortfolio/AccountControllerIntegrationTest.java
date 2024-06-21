package com.team2a.ProjectPortfolio;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2a.ProjectPortfolio.Commons.Account;
import com.team2a.ProjectPortfolio.Commons.Collaborator;
import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Commons.ProjectsToAccounts;
import com.team2a.ProjectPortfolio.Commons.Role;
import com.team2a.ProjectPortfolio.Commons.RoleInProject;
import com.team2a.ProjectPortfolio.Repositories.AccountRepository;
import com.team2a.ProjectPortfolio.Repositories.CollaboratorRepository;
import com.team2a.ProjectPortfolio.Repositories.ProjectRepository;
import com.team2a.ProjectPortfolio.Repositories.ProjectsToAccountsRepository;
import com.team2a.ProjectPortfolio.security.SecurityConfigUtils;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters=false)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class AccountControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private CollaboratorRepository collaboratorRepository;

  @Autowired
  private ProjectRepository projectRepository;

  @Autowired
  private ProjectsToAccountsRepository projectsToAccountsRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private SecurityConfigUtils securityConfigUtils;

  private Account account;

  private Project project;

  @BeforeEach
  public void setUp() {
    accountRepository.deleteAll();
    projectRepository.deleteAll();
    projectsToAccountsRepository.deleteAll();
    account = new Account("username1", "name1", "password1", Role.ROLE_USER);
    collaboratorRepository.deleteAll();
    project = new Project("title", "description", false);
    project = projectRepository.save(project);
    account = accountRepository.saveAndFlush(account);
    collaboratorRepository.saveAndFlush(new Collaborator(account.getName()));
    securityConfigUtils.setAuthentication();
  }

  @Test
  public void deleteAccount() throws Exception {
    assertEquals(1, accountRepository.count());

    mockMvc.perform(delete(Routes.ACCOUNT + "/" + account.getUsername())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    assertEquals(0, accountRepository.count());

    mockMvc.perform(delete(Routes.ACCOUNT + "/" + "username1")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  public void editAccount() throws Exception {
    assertEquals(1, accountRepository.count());
    account.setName("name2");

    mockMvc.perform(put(Routes.ACCOUNT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(account)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username", is("username1")))
        .andExpect(jsonPath("$.name", is("name2")))
        .andExpect(jsonPath("$.password", is("password1")));

    assertEquals(1, accountRepository.count());

    mockMvc.perform(put(Routes.ACCOUNT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new Account("username2", "name", "password", Role.ROLE_USER))))
        .andExpect(status().isNotFound());

    mockMvc.perform(put(Routes.ACCOUNT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new Account("username1", null, "password", Role.ROLE_USER))))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void getAccountById() throws Exception {
    mockMvc.perform(get(Routes.ACCOUNT + "/public/" + account.getUsername())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username", is("username1")))
        .andExpect(jsonPath("$.name", is("name1")))
        .andExpect(jsonPath("$.password", is("password1")));

    mockMvc.perform(get(Routes.ACCOUNT + "/public/" + "username2")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  public void addRole() throws Exception {
    assertEquals(0, projectsToAccountsRepository.count());

    UUID id = UUID.randomUUID();
    while(id.equals(project.getProjectId())) {
      id = UUID.randomUUID();
    }

    mockMvc.perform(post(Routes.ACCOUNT + "/" + account.getUsername() + "/" + project.getProjectId())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    mockMvc.perform(post(Routes.ACCOUNT + "/" + account.getUsername() + "/" + project.getProjectId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString("CONTENT_CREATOR")))
        .andExpect(status().isOk());

    assertEquals(1, projectsToAccountsRepository.count());

    mockMvc.perform(post(Routes.ACCOUNT + "/" + "username2" + "/" + project.getProjectId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString("CONTENT_CREATOR")))
        .andExpect(status().isNotFound());

    mockMvc.perform(post(Routes.ACCOUNT + "/" + account.getUsername() + "/" + id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString("CONTENT_CREATOR")))
        .andExpect(status().isNotFound());

    mockMvc.perform(post(Routes.ACCOUNT + "/" + account.getUsername() + "/" + project.getProjectId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString("CONTENT_CREATOR")))
        .andExpect(status().isConflict());
  }

  @Test
  void deleteRole() throws Exception {
    assertEquals(0, projectsToAccountsRepository.count());
    ProjectsToAccounts pta = new ProjectsToAccounts(RoleInProject.CONTENT_CREATOR, account, project);
    projectsToAccountsRepository.saveAndFlush(pta);
    assertEquals(1, projectsToAccountsRepository.count());

    UUID id = UUID.randomUUID();
    while(id.equals(project.getProjectId())) {
      id = UUID.randomUUID();
    }

    mockMvc.perform(delete(Routes.ACCOUNT + "/" + "username2" + "/" + project.getProjectId())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());

    mockMvc.perform(delete(Routes.ACCOUNT + "/" + account.getUsername() + "/" + id)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());

    mockMvc.perform(delete(Routes.ACCOUNT + "/" + account.getUsername() + "/" + project.getProjectId())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());


    assertEquals(0, projectsToAccountsRepository.count());
  }

}
