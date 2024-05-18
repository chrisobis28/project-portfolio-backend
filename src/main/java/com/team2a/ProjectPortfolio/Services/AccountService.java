package com.team2a.ProjectPortfolio.Services;

import com.team2a.ProjectPortfolio.Commons.Account;
import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Commons.ProjectsToAccounts;
import com.team2a.ProjectPortfolio.CustomExceptions.AccountNotFoundException;
import com.team2a.ProjectPortfolio.CustomExceptions.DuplicatedUsernameException;
import com.team2a.ProjectPortfolio.CustomExceptions.FieldNullException;
import com.team2a.ProjectPortfolio.CustomExceptions.IdIsNullException;
import com.team2a.ProjectPortfolio.CustomExceptions.NotFoundException;
import com.team2a.ProjectPortfolio.CustomExceptions.ProjectNotFoundException;
import com.team2a.ProjectPortfolio.Repositories.AccountRepository;
import com.team2a.ProjectPortfolio.Repositories.ProjectRepository;
import com.team2a.ProjectPortfolio.Repositories.ProjectsToAccountsRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final ProjectRepository projectRepository;
    private final ProjectsToAccountsRepository projectsToAccountsRepository;

    /**
     * Constructor for the Account Service
     * @param accountRepository - the Account Repository
     * @param projectRepository - the Project Repository
     * @param projectsToAccountsRepository - the Projects to Accounts Repository
     */
    @Autowired
    public AccountService (AccountRepository accountRepository,
                           ProjectRepository projectRepository,
                           ProjectsToAccountsRepository projectsToAccountsRepository) {
        this.accountRepository = accountRepository;
        this.projectRepository = projectRepository;
        this.projectsToAccountsRepository = projectsToAccountsRepository;
    }

    /**
     * Creates an Account in the database
     * @param account - the Account to create
     * @return - the Account that was created
     * @throws RuntimeException - Duplicated username or the id is null
     */
    public Account createAccount (Account account) throws RuntimeException {
        nullFieldChecker(account);
        Optional<Account> o = accountRepository.findById(account.getUsername());
        if(o.isPresent()) {
            throw new DuplicatedUsernameException("An account with the username "
                + account.getUsername() + " already exists.");
        }
        return accountRepository.save(account);
    }

    /**
     * Edits an Account in the database
     * @param account - the Account that needs to be edited
     * @return - the Account with the modifications applied
     * @throws RuntimeException - Account was not found or the id is null
     */
    public Account editAccount (Account account) throws RuntimeException {
        nullFieldChecker(account);
        Optional<Account> o = accountRepository.findById(account.getUsername());
        if(o.isEmpty()) {
            throw new AccountNotFoundException("There is no account with username " + account.getUsername() + ".");
        }
        return accountRepository.save(account);
    }

    /**
     * Gets an Account with the specified id
     * @param username - the id of the Account to be retrieved
     * @return - the Account desired
     * @throws RuntimeException - the id is null
     */
    public Account getAccountById (String username) throws RuntimeException {
        return checkAccountExistence(username);
    }

    /**
     * Deletes an account from the database
     * @param username - the id of the Account to be deleted
     * @throws RuntimeException - the id is null
     */
    public void deleteAccount (String username) throws RuntimeException {
        Account account = checkAccountExistence(username);
        accountRepository.deleteById(account.getUsername());
    }

    /**
     * Helper method to check whether an Account exists
     * @param username - the id of the Account to be localized
     * @return - the Account desired, provided it exists
     * @throws RuntimeException - Account was not found or the id is null
     */
    public Account checkAccountExistence (String username) throws RuntimeException {
        if(username == null) {
            throw new IdIsNullException("Null id not accepted.");
        }
        Optional<Account> o = accountRepository.findById(username);
        if(o.isEmpty()) {
            throw new AccountNotFoundException("There is no account with username " + username + ".");
        }
        return o.get();
    }

    /**
     * Helper method to check the fields of an Account for nullability
     * @param account - the Account to verify
     * @throws RuntimeException - A field is null
     */
    public void nullFieldChecker (Account account) throws RuntimeException {
        if(account.getUsername() == null || account.getName() == null || account.getPassword() == null
            || account.getIsPM() == null || account.getIsAdministrator() == null) {
            throw new FieldNullException("Null fields are not valid.");
        }
    }

    /**
     * Adds a role to an Account associated to a Project.
     * @param username - the username of the Account
     * @param projectId - the id of the Project
     * @param role - the role given
     */
    public void addRole (String username, UUID projectId, String role) {
        Optional<Account> optionalAccount = accountRepository.findById(username);
        if(optionalAccount.isEmpty()) {
            throw new AccountNotFoundException("");
        }
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        if(optionalProject.isEmpty()) {
            throw new ProjectNotFoundException("");
        }
        if(projectsToAccountsRepository.findAll().stream()
            .filter(x -> x.getProject().equals(optionalProject.get()) && x.getAccount().equals(optionalAccount.get()))
            .toList().size() > 0) {
            throw new DuplicatedUsernameException("");
        }
        ProjectsToAccounts pta = new ProjectsToAccounts(role, optionalAccount.get(), optionalProject.get());
        projectsToAccountsRepository.save(pta);
    }

    /**
     * Deletes the role from the table
     * @param username - the username of the Account
     * @param projectId - the id of the Project
     */
    public void deleteRole (String username, UUID projectId) {
        List<ProjectsToAccounts>
            list = projectsToAccountsRepository.findAll().stream().filter(x -> x.getAccount().getUsername().equals(username))
            .filter(x -> x.getProject().getProjectId().equals(projectId)).toList();
        if(list.isEmpty()) {
            throw new NotFoundException();
        }
        projectsToAccountsRepository.deleteById(list.get(0).getPtaId());
    }
}
