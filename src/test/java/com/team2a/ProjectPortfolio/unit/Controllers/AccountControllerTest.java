package com.team2a.ProjectPortfolio.unit.Controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.team2a.ProjectPortfolio.Commons.Account;
import com.team2a.ProjectPortfolio.Controllers.AccountController;
import com.team2a.ProjectPortfolio.CustomExceptions.AccountNotFoundException;
import com.team2a.ProjectPortfolio.CustomExceptions.DuplicatedUsernameException;
import com.team2a.ProjectPortfolio.CustomExceptions.FieldNullException;
import com.team2a.ProjectPortfolio.CustomExceptions.IdIsNullException;
import com.team2a.ProjectPortfolio.Services.AccountService;
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
  void testCreateAccountFieldNullException() {
    when(accountService.createAccount(any(Account.class))).thenThrow(new FieldNullException(""));
    Account account = new Account("username", "name", "password", false, false);
    ResponseEntity<Account> re = accountController.createAccount(account);
    assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
    assertNull(re.getBody());
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
  void testEditAccountFieldNullException() {
    when(accountService.editAccount(any(Account.class))).thenThrow(new FieldNullException(""));
    Account account = new Account("username", "name", "password", false, false);
    ResponseEntity<Account> re = accountController.editAccount(account);
    assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
    assertNull(re.getBody());
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
  void testGetAccountByIdFieldNullException() {
    when(accountService.getAccountById(any(String.class))).thenThrow(new IdIsNullException(""));
    ResponseEntity<Account> re = accountController.getAccountById("mock");
    assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
    assertNull(re.getBody());
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
  void testDeleteAccountByIdFieldNullException() {
    doThrow(new IdIsNullException("Null id not accepted.")).when(accountService).deleteAccount(any());
    ResponseEntity<String> re = accountController.deleteAccount(null);
    assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
    assertEquals("Null id not accepted.", re.getBody());
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

}
