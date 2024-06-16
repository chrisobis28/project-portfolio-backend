package com.team2a.ProjectPortfolio.Controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.team2a.ProjectPortfolio.Commons.Account;
import com.team2a.ProjectPortfolio.Commons.Role;
import com.team2a.ProjectPortfolio.Commons.RoleInProject;
import com.team2a.ProjectPortfolio.CustomExceptions.AccountNotFoundException;
import com.team2a.ProjectPortfolio.CustomExceptions.DuplicatedUsernameException;
import com.team2a.ProjectPortfolio.CustomExceptions.NotFoundException;
import com.team2a.ProjectPortfolio.CustomExceptions.ProjectNotFoundException;
import com.team2a.ProjectPortfolio.Services.AccountService;
import com.team2a.ProjectPortfolio.WebSocket.AccountProjectWebSocketHandler;
import com.team2a.ProjectPortfolio.WebSocket.AccountWebSocketHandler;
import com.team2a.ProjectPortfolio.dto.AccountTransfer;
import com.team2a.ProjectPortfolio.dto.ProjectTransfer;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
public class AccountControllerTest {

  @Mock
  private AccountService accountService;

  @Mock
  private AccountWebSocketHandler accountWebSocketHandler;

  @Mock
  private AccountProjectWebSocketHandler accountProjectWebSocketHandler;
  private AccountController accountController;

  private String username;
  private UUID projectId;
  private RoleInProject role;

  @BeforeEach
  void setUp() {
    accountService = Mockito.mock(AccountService.class);
    accountWebSocketHandler = Mockito.mock(AccountWebSocketHandler.class);
    accountProjectWebSocketHandler = Mockito.mock(AccountProjectWebSocketHandler.class);
    accountController = new AccountController(accountService, accountWebSocketHandler, accountProjectWebSocketHandler);
    username = "testuser";
    projectId = UUID.randomUUID();
    role = RoleInProject.PM;
  }

  @Test
  void testEditAccountSuccess() {
    Account account = new Account("username", "name", "password", Role.ROLE_USER);
    when(accountService.editAccount(any(Account.class))).thenReturn(account);
    ResponseEntity<Account> re = accountController.editAccount(account);
    verify(accountWebSocketHandler, times(1)).broadcast("edit "+ account.getUsername());
    assertEquals(HttpStatus.OK, re.getStatusCode());
    assertEquals(account, re.getBody());
  }

  @Test
  void testGetAccountByIdAccountNotFoundException() {
    when(accountService.getAccountById(any(String.class))).thenThrow(new AccountNotFoundException(""));
    ResponseEntity<Account> re = accountController.getAccountById("mock");
    assertEquals(HttpStatus.NOT_FOUND, re.getStatusCode());
    assertNull(re.getBody());
  }

  @Test
  void testGetAccountByIdSuccess() {
    Account account = new Account("username", "name", "password", Role.ROLE_USER);
    when(accountService.getAccountById("username")).thenReturn(account);
    ResponseEntity<Account> re = accountController.getAccountById("username");
    assertEquals(HttpStatus.OK, re.getStatusCode());
    assertEquals(account, re.getBody());
  }

  @Test
  void testDeleteAccountSuccess() {
    doNothing().when(accountService).deleteAccount("username");
    ResponseEntity<String> re = accountController.deleteAccount("username");
    verify(accountWebSocketHandler, times(1)).broadcast("delete " + "username");
    assertEquals(HttpStatus.OK, re.getStatusCode());
  }

  @Test
  void addRoleSuccessful() {
    UUID id = UUID.randomUUID();
    doNothing().when(accountService).addRole("username", id, RoleInProject.CONTENT_CREATOR);
    ResponseEntity<Void> responseEntity = accountController.addRole("username", id, RoleInProject.CONTENT_CREATOR);
    verify(accountProjectWebSocketHandler, times(1)).broadcast(id.toString() + " add");
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertNull(responseEntity.getBody());
  }

  @Test
  void deleteRoleSuccessful() {
    UUID id = UUID.randomUUID();
    doNothing().when(accountService).deleteRole("username", id);
    ResponseEntity<Void> responseEntity = accountController.deleteRole("username", id);
    verify(accountProjectWebSocketHandler, times(1)).broadcast(id.toString() + " delete " + "username");
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertNull(responseEntity.getBody());
  }

  @Test
  void testUpdateRole() {
    ResponseEntity<Void> response = accountController.updateRole(username, projectId, role);

    verify(accountService).updateRole(username, projectId, role);
    verify(accountProjectWebSocketHandler).broadcast(projectId.toString() + " update " + username);
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void testGetRole() {
    String expectedRole = "Admin";
    when(accountService.getRole(username, projectId)).thenReturn(expectedRole);

    ResponseEntity<String> response = accountController.getRole(username, projectId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expectedRole, response.getBody());
  }

  @Test
  void testGetAccounts() {
    List<AccountTransfer> accounts = List.of(new AccountTransfer(null,null), new AccountTransfer(null,null));
    when(accountService.getAccounts()).thenReturn(accounts);

    ResponseEntity<List<AccountTransfer>> response = accountController.getAccounts();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(accounts, response.getBody());
  }

  @Test
  void testGetProjects() {
    List<ProjectTransfer> projects = List.of(new ProjectTransfer(null,null,null), new ProjectTransfer(null,null,null));
    when(accountService.getProjects(username)).thenReturn(projects);

    ResponseEntity<List<ProjectTransfer>> response = accountController.getProjects(username);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(projects, response.getBody());
  }

  @Test
  void testGetAccountByName() {
    String name = "testname";
    List<String> usernames = List.of("user1", "user2");
    when(accountService.getAccountsByName(name)).thenReturn(usernames);

    ResponseEntity<List<String>> response = accountController.getAccountByName(name);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(usernames, response.getBody());
  }

  @Test
  void testGetAllUsernames() {
    List<String> usernames = List.of("user1", "user2", "user3");
    when(accountService.getAllUsernames()).thenReturn(usernames);

    ResponseEntity<List<String>> response = accountController.getAllUsernames();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(usernames, response.getBody());
  }
}
