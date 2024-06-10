package com.team2a.ProjectPortfolio.Services;

import com.team2a.ProjectPortfolio.Commons.Account;
import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Commons.ProjectsToAccounts;
import com.team2a.ProjectPortfolio.Commons.RoleInProject;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final ProjectRepository projectRepository;
    private final ProjectsToAccountsRepository projectsToAccountsRepository;

    /**
     * Constructor for the AccountService class
     * @param accountRepository - the repository for the Account class
     * @param projectRepository - the repository for the Project class
     * @param projectsToAccountsRepository - the repository for the ProjectsToAccounts class
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
     * Edits an Account in the database
     * @param account - the Account that needs to be edited
     * @return - the Account with the modifications applied
     * @throws RuntimeException - Account was not found or the id is null
     */
    public Account editAccount (Account account) throws RuntimeException {
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
     * @throws RuntimeException - Account was not found
     */
    public Account checkAccountExistence (String username) throws RuntimeException {
        Optional<Account> o = accountRepository.findById(username);
        if(o.isEmpty()) {
            throw new AccountNotFoundException("There is no account with username " + username + ".");
        }
        return o.get();
    }

    /**
     * Helper method to check whether a Project exists
     * @param projectId - the id of the Project to be localized
     * @return - the Project desired, provided it exists
     * @throws RuntimeException - Project was not found
     */
    public Project checkProjectExistence (UUID projectId) throws RuntimeException {
        Optional<Project> o = projectRepository.findById(projectId);
        if(o.isEmpty()) {
            throw new ProjectNotFoundException("There is no project with id " + projectId + ".");
        }
        return o.get();
    }

    /**
     * Adds a role to an Account associated to a Project.
     * @param username - the username of the Account
     * @param projectId - the id of the Project
     * @param role - the role given
     */
    public void addRole (String username, UUID projectId, RoleInProject role) {
        Account optionalAccount = checkAccountExistence(username);
        Project optionalProject = checkProjectExistence(projectId);
        if(projectsToAccountsRepository.findAll().stream()
            .filter(x -> x.getProject().equals(optionalProject) && x.getAccount().equals(optionalAccount))
            .toList().size() > 0) {
            throw new DuplicatedUsernameException("");
        }
        ProjectsToAccounts pta = new ProjectsToAccounts(role, optionalAccount, optionalProject);
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

    /**
     * Updates the role of an Account in a Project
     * @param username - the username of the Account
     * @param projectId - the id of the Project
     * @param role - the new role to be set
     */
    public void updateRole (String username, UUID projectId, RoleInProject role) {
        List<ProjectsToAccounts>
            list = projectsToAccountsRepository.findAll().stream().filter(x -> x.getAccount().getUsername().equals(username))
            .filter(x -> x.getProject().getProjectId().equals(projectId)).toList();
        if(list.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project or account not found.");
        }
        ProjectsToAccounts pta = list.get(0);
        pta.setRole(role);
        projectsToAccountsRepository.save(pta);
    }

    /**
     * Gets the role of an Account in a Project
     * @param username - the username of the Account
     * @param projectId - the id of the Project
     * @return - the role of the Account in the Project
     */
    public String getRole (String username, UUID projectId) {
        List<ProjectsToAccounts>
            list = projectsToAccountsRepository.findAll().stream().filter(x -> x.getAccount().getUsername().equals(username))
            .filter(x -> x.getProject().getProjectId().equals(projectId)).toList();
        if(list.isEmpty()) {
            return "VISITOR";
        }
        return list.get(0).getRole().toString();
    }

    /**
     * Retrieves all projects a user has a role on
     * @param username - the username of the user for search
     * @return - the list of ids of all projects
     */
    public List<UUID> getProjects (String username) {
        return projectsToAccountsRepository.findAll().stream().filter(x -> x.getAccount().getUsername().equals(username))
            .map(x -> x.getProject().getProjectId()).toList();
    }

    /**
     * Retrieves all Accounts on the platform
     * @return - the list of Accounts
     */
    public List<Account> getAccounts () {
        return accountRepository.findAll();
    }

    /**
     * Retrieves all accounts username on the platform with a given name
     * @param name - the name of the Accounts to be searched
     * @return - the list of all account usernames with the given name
     */
    public List<String> getAccountsByName (String name) {
        return accountRepository.findAll().stream().filter(x -> x.getName().equals(name)).map(Account::getUsername).toList();
    }
}
