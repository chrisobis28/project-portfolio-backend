package com.team2a.ProjectPortfolio.Services;

import com.team2a.ProjectPortfolio.Commons.Account;
import com.team2a.ProjectPortfolio.CustomExceptions.DuplicatedUsernameException;
import com.team2a.ProjectPortfolio.CustomExceptions.FieldNullException;
import com.team2a.ProjectPortfolio.Repositories.AccountRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account createAccount(Account account) throws RuntimeException {
        if(account.getUsername() == null || account.getName() == null || account.getPassword() == null
            || account.getIsPM() == null || account.getIsAdministrator() == null) {
            throw new FieldNullException("Null fields are not valid.");
        }
        Optional<Account> o = accountRepository.findById(account.getUsername());
        if(o.isPresent()) {
            throw new DuplicatedUsernameException("An account with the username " + account.getUsername() + " already exists.");
        }
        return accountRepository.save(account);
    }
}
