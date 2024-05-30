package com.team2a.ProjectPortfolio.security;

import com.team2a.ProjectPortfolio.Commons.Account;
import com.team2a.ProjectPortfolio.Repositories.AccountRepository;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    /**
     * Load a user by username.
     *
     * @param username the username
     * @return the user details
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername (String username) throws UsernameNotFoundException {
        Account account = accountRepository.findById(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new User(account.getUsername(), account.getPassword(), new ArrayList<>());
    }
}
