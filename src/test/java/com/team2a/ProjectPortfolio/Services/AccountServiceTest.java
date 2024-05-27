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
import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Commons.ProjectsToAccounts;
import com.team2a.ProjectPortfolio.CustomExceptions.AccountNotFoundException;
import com.team2a.ProjectPortfolio.CustomExceptions.DuplicatedUsernameException;
import com.team2a.ProjectPortfolio.CustomExceptions.NotFoundException;
import com.team2a.ProjectPortfolio.CustomExceptions.ProjectNotFoundException;
import com.team2a.ProjectPortfolio.Repositories.AccountRepository;
import com.team2a.ProjectPortfolio.Repositories.ProjectRepository;
import com.team2a.ProjectPortfolio.Repositories.ProjectsToAccountsRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

  @Mock
  private ProjectRepository projectRepository;

  @Mock
  private ProjectsToAccountsRepository projectsToAccountsRepository;

  private AccountService accountService;

  @BeforeEach
  void setUp() {
    accountRepository = Mockito.mock(AccountRepository.class);
    projectRepository = Mockito.mock(ProjectRepository.class);
    projectsToAccountsRepository = Mockito.mock(ProjectsToAccountsRepository.class);
    accountService = new AccountService(accountRepository, projectRepository, projectsToAccountsRepository);
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

  @Test
  void testAddRoleAccountNotFound() {
    when(accountRepository.findById("username")).thenReturn(Optional.empty());
    assertThrows(AccountNotFoundException.class, () -> accountService.addRole("username", UUID.randomUUID(), "role"));
  }

  @Test
  void testAddRoleProjectNotFound() {
    UUID id = UUID.randomUUID();
    when(accountRepository.findById("username")).thenReturn(Optional.of(new Account()));
    when(projectRepository.findById(id)).thenReturn(Optional.empty());
    assertThrows(ProjectNotFoundException.class, () -> accountService.addRole("username", id, "role"));
  }

  @Test
  void testAddRoleAlreadyHasRole() {
    UUID id = UUID.randomUUID();
    when(accountRepository.findById("username")).thenReturn(Optional.of(new Account()));
    when(projectRepository.findById(id)).thenReturn(Optional.of(new Project()));
    Project p = new Project();
    Account a = new Account();
    when(projectRepository.findById(id)).thenReturn(Optional.of(p));
    when(accountRepository.findById("username")).thenReturn(Optional.of(a));
    when(projectsToAccountsRepository.findAll()).thenReturn(List.of(new ProjectsToAccounts("role", a, p)));
    assertThrows(DuplicatedUsernameException.class, () -> accountService.addRole("username", id, "role"));
  }

  @Test
  void testAddRoleSuccess() {
    UUID id = UUID.randomUUID();
    when(accountRepository.findById("username")).thenReturn(Optional.of(new Account()));
    when(projectRepository.findById(id)).thenReturn(Optional.of(new Project()));
    when(projectsToAccountsRepository.findAll()).thenReturn(List.of());
    accountService.addRole("username", id, "role");
    verify(projectsToAccountsRepository, times(1)).save(any());
  }

  @Test
  void testDeleteRoleNotFoundProject() {
    UUID id = UUID.randomUUID();
    Project p = new Project();
    p.setProjectId(id);
    Account a = new Account();
    a.setUsername("username");
    ProjectsToAccounts pta = new ProjectsToAccounts("role", a, p);
    when(projectsToAccountsRepository.findAll()).thenReturn(List.of(pta));
    assertThrows(NotFoundException.class, () -> accountService.deleteRole("username1", id));
  }

  @Test
  void testDeleteRoleNotFoundAccount() {
    UUID id = UUID.randomUUID();
    Project p = new Project();
    p.setProjectId(id);
    UUID id2;
    do {
      id2 = UUID.randomUUID();
    } while (id2.equals(id));
    Account a = new Account();
    a.setUsername("username");
    ProjectsToAccounts pta = new ProjectsToAccounts("role", a, p);
    when(projectsToAccountsRepository.findAll()).thenReturn(List.of(pta));
    UUID finalId = id2;
    assertThrows(NotFoundException.class, () -> accountService.deleteRole("username1", finalId));
  }

  @Test
  void testDeleteRoleSuccess() {
    UUID id = UUID.randomUUID();
    Project p = new Project();
    p.setProjectId(id);
    Account a = new Account();
    a.setUsername("username");
    ProjectsToAccounts pta = new ProjectsToAccounts("role", a, p);
    when(projectsToAccountsRepository.findAll()).thenReturn(List.of(pta));
    accountService.deleteRole("username", id);
    verify(projectsToAccountsRepository, times(1)).deleteById(any());
  }
}
