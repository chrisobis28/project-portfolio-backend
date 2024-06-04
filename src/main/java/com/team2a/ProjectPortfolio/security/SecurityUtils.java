package com.team2a.ProjectPortfolio.security;

import com.team2a.ProjectPortfolio.Commons.Account;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityUtils {

    /**
     * Returns the current user
     * from the security context
     * @return the current user
     */
    public Account getCurrentUser () {
        return (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
