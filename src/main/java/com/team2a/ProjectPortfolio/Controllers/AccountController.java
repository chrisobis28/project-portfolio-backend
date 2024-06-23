package com.team2a.ProjectPortfolio.Controllers;

import static com.team2a.ProjectPortfolio.security.Permissions.ADMIN_ONLY;
import static com.team2a.ProjectPortfolio.security.Permissions.PM_IN_PROJECT;
import static com.team2a.ProjectPortfolio.security.Permissions.PM_ONLY;
import static com.team2a.ProjectPortfolio.security.Permissions.USER_SPECIFIC;

import com.team2a.ProjectPortfolio.Commons.Account;
import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Commons.RoleInProject;
import com.team2a.ProjectPortfolio.CustomExceptions.AccountNotFoundException;
import com.team2a.ProjectPortfolio.CustomExceptions.DuplicatedUsernameException;
import com.team2a.ProjectPortfolio.CustomExceptions.NotFoundException;
import com.team2a.ProjectPortfolio.CustomExceptions.ProjectNotFoundException;
import com.team2a.ProjectPortfolio.Routes;
import com.team2a.ProjectPortfolio.Services.AccountService;
import com.team2a.ProjectPortfolio.WebSocket.AccountProjectWebSocketHandler;
import com.team2a.ProjectPortfolio.WebSocket.AccountWebSocketHandler;
import com.team2a.ProjectPortfolio.dto.AccountDisplay;
import com.team2a.ProjectPortfolio.dto.AccountTransfer;
import com.team2a.ProjectPortfolio.dto.ProjectTransfer;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Routes.ACCOUNT)
@CrossOrigin("http://localhost:4200")
public class AccountController {

    private final AccountService accountService;

    private final AccountWebSocketHandler accountWebSocketHandler;

    private final AccountProjectWebSocketHandler accountProjectWebSocketHandler;

    /**
     * Constructor for the AccountController
     * @param accountService - the service to be used
     * @param accountWebSocketHandler - the handler for the web socket
     * @param accountProjectWebSocketHandler - the handler for the web socket
     */
    @Autowired
    public AccountController (AccountService accountService,
                              AccountWebSocketHandler accountWebSocketHandler,
                              AccountProjectWebSocketHandler accountProjectWebSocketHandler) {
        this.accountService = accountService;
        this.accountWebSocketHandler = accountWebSocketHandler;
        this.accountProjectWebSocketHandler = accountProjectWebSocketHandler;
    }

    /**
     * Edits an already existing Account
     * @param account - the Account to be modified
     * @return - the Account with the necessary modifications
     */
    @PutMapping("")
    @PreAuthorize(ADMIN_ONLY)
    public ResponseEntity<Account> editAccount (@Valid @RequestBody Account account) {
        Account editedAccount = accountService.editAccount(account);
        accountWebSocketHandler.broadcast("edit " + account.getUsername());
        return ResponseEntity.ok(editedAccount);
    }

    /**
     * Edits only the role of an Account
     * @param accountTransfer - the DTO
     * @return - Void because just the status of OK is needed
     */
    @PutMapping("editRole")
    @PreAuthorize(ADMIN_ONLY)
    public ResponseEntity<Void> editRoleOfAccount (@Valid @RequestBody AccountTransfer accountTransfer) {
        try {
            accountService.editAccount(accountTransfer);
            accountWebSocketHandler.broadcast("edit " + accountTransfer.getUsername());
            return ResponseEntity.ok().build();
        }
        catch(AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Gets an Account from the database based on id
     * @param username - the id of the Account to be searched
     * @return - the Account with the given id, provided it exists
     */
    @GetMapping("/public/{username}")
    public ResponseEntity<Account> getAccountById (@PathVariable("username") String username) {
        try {
            return ResponseEntity.ok(accountService.getAccountById(username));
        }
        catch(AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Deletes an Account from the database
     * @param username - the id of the Account to be deleted
     * @return - the status of the deletion
     */
    @DeleteMapping("/{username}")
    @PreAuthorize(USER_SPECIFIC)
    public ResponseEntity<String> deleteAccount (@PathVariable("username") String username) {
        accountService.deleteAccount(username);
        accountWebSocketHandler.broadcast("delete " + username);
        return ResponseEntity.ok().build();
    }

    /**
     * Adds role to an Account under a certain project
     * @param username - the username of the Account to add a role to
     * @param projectId - the id of the Project
     * @param role - the role to be added
     * @return - the status of the addition
     */
    @PostMapping("/{username}/{projectId}")
    @PreAuthorize(PM_IN_PROJECT)
    public ResponseEntity<Void> addRole (@PathVariable("username") String username,
                                              @PathVariable("projectId") UUID projectId, @RequestBody RoleInProject role) {
        accountService.addRole(username, projectId, role);
        accountProjectWebSocketHandler.broadcast(projectId.toString() + " add " + username);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Delete the role of an Account from a Project
     * @param username - the username of the Account
     * @param projectId - the id of the Project
     * @return - the status of the deletion
     */
    @DeleteMapping("/{username}/{projectId}")
    @PreAuthorize(PM_IN_PROJECT)
    public ResponseEntity<Void> deleteRole (@PathVariable("username") String username,
                                            @PathVariable("projectId") UUID projectId) {
        accountService.deleteRole(username, projectId);
        accountProjectWebSocketHandler.broadcast(projectId.toString() + " delete " + username);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Updates the role of an Account in a Project
     * @param username - the username of the Account
     * @param projectId - the id of the Project
     * @param role - the new role to be assigned
     * @return - the status of the update
     */
    @PutMapping("/{username}/{projectId}")
    @PreAuthorize(PM_IN_PROJECT)
    public ResponseEntity<Void> updateRole (@PathVariable("username") String username,
                                            @PathVariable("projectId") UUID projectId,
                                            @Valid @RequestBody RoleInProject role) {
        accountService.updateRole(username, projectId, role);
        accountProjectWebSocketHandler.broadcast(projectId.toString() + " update " + username);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Gets the role of an Account in a Project
     * @param username - the username of the Account
     * @param projectId - the id of the Project
     * @return - the role of the Account in the Project
     */
    @GetMapping("/public/role/{username}/{projectId}")
    public ResponseEntity<String> getRole (@PathVariable("username") String username,
                                                 @PathVariable("projectId") UUID projectId) {
        return ResponseEntity.ok(accountService.getRole(username, projectId));
    }

    @GetMapping("/public/managed/{username}")
    public ResponseEntity<List<Project>> getProjectsAccountManages (@PathVariable("username") String username) {
        return ResponseEntity.ok(accountService.getProjectsAccountManages(username));
    }

    /**
     * Retrieve all Accounts on the platform for admin purposes
     * @return - the list of Accounts
     */
    @GetMapping("")
    @PreAuthorize(ADMIN_ONLY)
    public ResponseEntity<List<AccountTransfer>> getAccounts () {
        return ResponseEntity.ok(accountService.getAccounts());
    }

    /**
     * Gets the projects an account has a permission on
     * @param username - the username of the account to be searched
     * @return - the list of all project ids
     */
    @GetMapping("/role/{username}")
    @PreAuthorize(ADMIN_ONLY)
    public ResponseEntity<List<ProjectTransfer>> getProjects (@PathVariable("username") String username) {
        return ResponseEntity.ok(accountService.getProjects(username));
    }

    /**
     * Gets the accounts username with the given name
     * @param name - the name of the account to be searched
     * @return - the list of all account usernames with the given name
     */
    @GetMapping("/public/name/{name}")
    public ResponseEntity<List<String>> getAccountByName (@PathVariable("name") String name) {
        return ResponseEntity.ok(accountService.getAccountsByName(name));
    }

    /**
     * Gets all usernames in the database
     * @return - the list of all usernames
     */
    @GetMapping("/usernames")
    @PreAuthorize(PM_ONLY)
    public ResponseEntity<List<String>> getAllUsernames () {
        return ResponseEntity.ok(accountService.getAllUsernames());
    }

    /**
     * Gets all accounts in a project
     * @param projectId - the id of the project
     * @return - the list of all accounts in the project
     */
    @GetMapping("/project/{projectId}")
    @PreAuthorize(PM_IN_PROJECT)
    public ResponseEntity<List<AccountDisplay>> getAccountsInProject (@PathVariable("projectId") UUID projectId) {
        return ResponseEntity.ok(accountService.getAccountsInProject(projectId));
    }
}
