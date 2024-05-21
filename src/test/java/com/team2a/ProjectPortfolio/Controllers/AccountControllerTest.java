package com.team2a.ProjectPortfolio.Controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.team2a.ProjectPortfolio.Commons.Account;
import com.team2a.ProjectPortfolio.CustomExceptions.AccountNotFoundException;
import com.team2a.ProjectPortfolio.CustomExceptions.DuplicatedUsernameException;
import com.team2a.ProjectPortfolio.CustomExceptions.NotFoundException;
import com.team2a.ProjectPortfolio.CustomExceptions.ProjectNotFoundException;
import com.team2a.ProjectPortfolio.Services.AccountService;
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

@DataJpaTest
@ExtendWith(MockitoExtension.class)
public class AccountControllerTest {

  @Mock
  private AccountService accountService;
  private AccountController accountController;

  @BeforeEach
  void setUp() {
    accountService = Mockito.mock(AccountService.class);
    accountController = new AccountController(accountService);
  }

  @Test
  void testCreateAccountDuplicatedUsernameException() {
    when(accountService.createAccount(any(Account.class))).thenThrow(new DuplicatedUsernameException(""));
    Account account = new Account("username", "name", "password", false, false);
    ResponseEntity<Account> re = accountController.createAccount(account);
    assertEquals(HttpStatus.FORBIDDEN, re.getStatusCode());
    assertNull(re.getBody());
  }

  @Test
  void testCreateAccountSuccess() {
    Account account = new Account("username", "name", "password", false, false);
    when(accountService.createAccount(account)).thenReturn(account);
    ResponseEntity<Account> re = accountController.createAccount(account);
    assertEquals(HttpStatus.OK, re.getStatusCode());
    assertEquals(account, re.getBody());
  }

  @Test
  void testEditAccountNotFoundException() {
    when(accountService.editAccount(any(Account.class))).thenThrow(new AccountNotFoundException(""));
    Account account = new Account("username", "name", "password", false, false);
    ResponseEntity<Account> re = accountController.editAccount(account);
    assertEquals(HttpStatus.NOT_FOUND, re.getStatusCode());
    assertNull(re.getBody());
  }

  @Test
  void testEditAccountSuccess() {
    Account account = new Account("username", "name", "password", false, false);
    when(accountService.editAccount(any(Account.class))).thenReturn(account);
    ResponseEntity<Account> re = accountController.editAccount(account);
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
    Account account = new Account("username", "name", "password", false, false);
    when(accountService.getAccountById("username")).thenReturn(account);
    ResponseEntity<Account> re = accountController.getAccountById("username");
    assertEquals(HttpStatus.OK, re.getStatusCode());
    assertEquals(account, re.getBody());
  }

  @Test
  void testDeleteAccountByIdAccountNotFoundException() {
    doThrow(new AccountNotFoundException("There is no account with username username.")).when(accountService).deleteAccount("username");
    ResponseEntity<String> re = accountController.deleteAccount("username");
    assertEquals(HttpStatus.NOT_FOUND, re.getStatusCode());
    assertEquals("There is no account with username username.", re.getBody());
  }

  @Test
  void testDeleteAccountSuccess() {
    doNothing().when(accountService).deleteAccount("username");
    ResponseEntity<String> re = accountController.deleteAccount("username");
    assertEquals(HttpStatus.OK, re.getStatusCode());
    assertEquals("Success.", re.getBody());
  }

  @Test
  void addRoleAccountNotFound() {
    UUID id = UUID.randomUUID();
    doThrow(AccountNotFoundException.class).when(accountService).addRole("username", id, "role");
    ResponseEntity<Void> responseEntity = accountController.addRole("username", id, "role");
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    assertNull(responseEntity.getBody());
  }

  @Test
  void addRoleProjectNotFound() {
    UUID id = UUID.randomUUID();
    doThrow(ProjectNotFoundException.class).when(accountService).addRole("username", id, "role");
    ResponseEntity<Void> responseEntity = accountController.addRole("username", id, "role");
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    assertNull(responseEntity.getBody());
  }

  @Test
  void addRoleAlreadyHasRole() {
    UUID id = UUID.randomUUID();
    doThrow(DuplicatedUsernameException.class).when(accountService).addRole("username", id, "role");
    ResponseEntity<Void> responseEntity = accountController.addRole("username", id, "role");
    assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    assertNull(responseEntity.getBody());
  }

  @Test
  void addRoleSuccessful() {
    UUID id = UUID.randomUUID();
    doNothing().when(accountService).addRole("username", id, "role");
    ResponseEntity<Void> responseEntity = accountController.addRole("username", id, "role");
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertNull(responseEntity.getBody());
  }

  @Test
  void deleteRoleNotFound() {
    UUID id = UUID.randomUUID();
    doThrow(NotFoundException.class).when(accountService).deleteRole("username", id);
    ResponseEntity<Void> responseEntity = accountController.deleteRole("username", id);
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    assertNull(responseEntity.getBody());
  }

  @Test
  void deleteRoleSuccessful() {
    UUID id = UUID.randomUUID();
    doNothing().when(accountService).deleteRole("username", id);
    ResponseEntity<Void> responseEntity = accountController.deleteRole("username", id);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertNull(responseEntity.getBody());
  }
}
