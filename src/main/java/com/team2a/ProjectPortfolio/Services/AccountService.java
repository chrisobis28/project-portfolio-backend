package com.team2a.ProjectPortfolio.Services;

import com.team2a.ProjectPortfolio.Commons.Account;
import com.team2a.ProjectPortfolio.CustomExceptions.AccountNotFoundException;
import com.team2a.ProjectPortfolio.CustomExceptions.DuplicatedUsernameException;
import com.team2a.ProjectPortfolio.CustomExceptions.FieldNullException;
import com.team2a.ProjectPortfolio.CustomExceptions.IdIsNullException;
import com.team2a.ProjectPortfolio.Repositories.AccountRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    /**
     * Constructor for the Account Service
     * @param accountRepository - the Account Repository
     */
    @Autowired
    public AccountService (AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
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
}
