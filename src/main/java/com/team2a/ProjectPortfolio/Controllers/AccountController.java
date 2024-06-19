package com.team2a.ProjectPortfolio.Controllers;

import static com.team2a.ProjectPortfolio.Routes.hostLink;
import static com.team2a.ProjectPortfolio.security.Permissions.ADMIN_ONLY;
import static com.team2a.ProjectPortfolio.security.Permissions.PM_IN_PROJECT;
import static com.team2a.ProjectPortfolio.security.Permissions.USER_SPECIFIC;

import com.team2a.ProjectPortfolio.Commons.Account;
import com.team2a.ProjectPortfolio.Commons.RoleInProject;
import com.team2a.ProjectPortfolio.CustomExceptions.AccountNotFoundException;
import com.team2a.ProjectPortfolio.CustomExceptions.DuplicatedUsernameException;
import com.team2a.ProjectPortfolio.CustomExceptions.NotFoundException;
import com.team2a.ProjectPortfolio.CustomExceptions.ProjectNotFoundException;
import com.team2a.ProjectPortfolio.Routes;
import com.team2a.ProjectPortfolio.Services.AccountService;
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
@CrossOrigin(hostLink)
public class AccountController {

    private final AccountService accountService;

    /**
     * Constructor for the Account Controller
     * @param accountService - the Account Service
     */
    @Autowired
    public AccountController (AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Edits an already existing Account
     * @param account - the Account to be modified
     * @return - the Account with the necessary modifications
     */
    @PutMapping("")
    @PreAuthorize(ADMIN_ONLY)
    public ResponseEntity<Account> editAccount (@Valid @RequestBody Account account) {
        try {
            return ResponseEntity.ok(accountService.editAccount(account));
        }
        catch(AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
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
        try {
            accountService.deleteAccount(username);
            return ResponseEntity.status(HttpStatus.OK).body("Success.");
        }
        catch(AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
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
        try {
            accountService.addRole(username, projectId, role);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        catch (AccountNotFoundException | ProjectNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        catch (DuplicatedUsernameException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
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
        try {
            accountService.deleteRole(username, projectId);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
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
}
