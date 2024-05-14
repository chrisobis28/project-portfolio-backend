package com.team2a.ProjectPortfolio.Services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.team2a.ProjectPortfolio.Commons.Account;
import com.team2a.ProjectPortfolio.CustomExceptions.AccountNotFoundException;
import com.team2a.ProjectPortfolio.CustomExceptions.DuplicatedUsernameException;
import com.team2a.ProjectPortfolio.CustomExceptions.FieldNullException;
import com.team2a.ProjectPortfolio.CustomExceptions.IdIsNullException;
import com.team2a.ProjectPortfolio.Repositories.AccountRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

  @Mock
  private AccountRepository accountRepository;

  private AccountService accountService;

  @BeforeEach
  void setUp() {
    accountRepository = Mockito.mock(AccountRepository.class);
    accountService = new AccountService(accountRepository);
  }

  @Test
  void testCreateAccountNullField() {
    Account account = new Account("username", "name", null, false, false);
    assertThrows(FieldNullException.class, () -> accountService.createAccount(account));
    verify(accountRepository, never()).save(any(Account.class));
  }

  @Test
  void testCreateAccountDuplicatedUsernameException() {
    Account account = new Account("username", "name", "password", false, false);
    when(accountRepository.findById("username")).thenReturn(Optional.of(account));
    assertThrows(DuplicatedUsernameException.class, () -> accountService.createAccount(account));
    verify(accountRepository, never()).save(any(Account.class));
  }

  @Test
  void testCreateAccountSuccess() {
    Account account = new Account("username", "name", "password", false, false);
    when(accountRepository.findById("username")).thenReturn(Optional.empty());
    when(accountRepository.save(account)).thenReturn(account);
    Account retrieved_account = accountService.createAccount(account);
    assertEquals(retrieved_account, account);
    verify(accountRepository, times(1)).save(account);
  }

  @Test
  void testEditAccountNullField() {
    Account account = new Account("username", "name", null, false, false);
    assertThrows(FieldNullException.class, () -> accountService.editAccount(account));
    verify(accountRepository, never()).save(any(Account.class));
  }

  @Test
  void testEditAccountAccountNotFoundException() {
    Account account = new Account("username", "name", "password", false, false);
    when(accountRepository.findById("username")).thenReturn(Optional.empty());
    assertThrows(AccountNotFoundException.class, () -> accountService.editAccount(account));
    verify(accountRepository, never()).save(any(Account.class));
  }

  @Test
  void testEditAccountSuccess() {
    Account account = new Account("username", "name", "password", false, false);
    when(accountRepository.findById("username")).thenReturn(Optional.of(account));
    when(accountRepository.save(account)).thenReturn(account);
    Account retrieved_account = accountService.editAccount(account);
    assertEquals(retrieved_account, account);
    verify(accountRepository, times(1)).save(account);
  }

  @Test
  void testGetAccountByIdNullIdException() {
    assertThrows(IdIsNullException.class, () -> accountService.getAccountById(null));
    verify(accountRepository, never()).findById(any());
  }

  @Test
  void testGetAccountByIdAccountNotFoundException() {
    when(accountRepository.findById("username")).thenReturn(Optional.empty());
    assertThrows(AccountNotFoundException.class, () -> accountService.getAccountById("username"));
  }

  @Test
  void testGetAccountByIdSuccess() {
    Account account = new Account("username", "name", "password", false, false);
    when(accountRepository.findById("username")).thenReturn(Optional.of(account));
    Account retrieved_account = accountService.getAccountById("username");
    assertEquals(retrieved_account, account);
    verify(accountRepository, times(1)).findById("username");
  }

  @Test
  void testDeleteAccountNullException() {
    assertThrows(IdIsNullException.class, () -> accountService.deleteAccount(null));
    verify(accountRepository, never()).deleteById(any());
  }

  @Test
  void testDeleteAccountByIdAccountNotFoundException() {
    when(accountRepository.findById("username")).thenReturn(Optional.empty());
    assertThrows(AccountNotFoundException.class, () -> accountService.deleteAccount("username"));
    verify(accountRepository, never()).deleteById("username");
  }

  @Test
  void testDeleteAccountSuccess() {
    Account account = new Account("username", "name", "password", false, false);
    when(accountRepository.findById("username")).thenReturn(Optional.of(account));
    doNothing().when(accountRepository).deleteById("username");
    accountService.deleteAccount("username");
    verify(accountRepository, times(1)).deleteById("username");
  }
}
