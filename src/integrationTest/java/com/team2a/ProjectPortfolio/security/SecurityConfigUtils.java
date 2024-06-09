package com.team2a.ProjectPortfolio.security;

import com.team2a.ProjectPortfolio.Commons.Account;
import com.team2a.ProjectPortfolio.Commons.Role;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityConfigUtils {

    private Account currentAccount;

    private Account adminAccount = new Account("username","name","Password!1", Role.ROLE_ADMIN);

    /**
     * Use in integration tests to set an admin user
     * in the SecurityContext in order to bypass authorization
     */
    public void setAuthentication () {
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(adminAccount, null, adminAccount.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        currentAccount = adminAccount;
    }

    /**
     * Use in integration tests to set a project manager with a specific username
     * in the SecurityContext
     * @param username
     */
    public void setProjectManagerWithUsername (String username){
        Account projectManagerAccount = new Account(username,"name","Password!1",Role.ROLE_PM);
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(projectManagerAccount, null,projectManagerAccount.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        currentAccount = projectManagerAccount;
    }

    /**
     * Use in integration tests to set a user with a specific username
     * in the SecurityContext
     * @param username
     */
    public void setUserWithUsername (String username){
        Account user = new Account(username,"name","Password!1",Role.ROLE_USER);
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(user, null,user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        currentAccount = user;
    }

    /**
     * Use in integration tests to set a specific account
     * @param account
     */
    public void setCurrentAccount (Account account){
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(account, null,account.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        currentAccount = account;
    }

    /**
     * Get the current account in the SecurityContext
     * @return the current account
     */
    public Account getAccount () {
        return currentAccount;
    }

}