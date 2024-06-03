package com.team2a.ProjectPortfolio.security;
import com.team2a.ProjectPortfolio.Commons.Account;
import com.team2a.ProjectPortfolio.Commons.RoleInProject;
import com.team2a.ProjectPortfolio.Services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.UUID;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CustomSecurityService {

    @Autowired
    private ProjectService projectService;

    /**
     * Checks if the user belongs to the project
     * @param authentication the authentication object
     * @param projectId the id of the project
     * @return the role of the user in the project
     */
    private RoleInProject belongsToProject (Authentication authentication, UUID projectId) {

        Account account = (Account) authentication.getPrincipal();

        return projectService.userBelongsToProject(account.getUsername(), projectId);
    }

    /**
     * Checks if the user is a PM in the project
     * @param authentication the authentication object
     * @param projectId the id of the project
     * @return true if the user is a PM in the project
     * @throws ResponseStatusException(403) if the user does not have the required permissions
     */
    public boolean pmInProject (Authentication authentication, UUID projectId) {
        RoleInProject role = belongsToProject(authentication, projectId);
        if(!role.equals(RoleInProject.PM)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have the required permissions");
        }
        return true;
    }

    /**
     * Checks if the user is a PM or an editor in the project
     * @param authentication the authentication object
     * @param projectId the id of the project
     * @return true if the user is a PM or an editor in the project
     * @throws ResponseStatusException(403) if the user does not have the required permissions
     */
    public boolean editorInProject (Authentication authentication, UUID projectId) {
        RoleInProject role = belongsToProject(authentication, projectId);
        if(!(role.equals(RoleInProject.PM) || role.equals(RoleInProject.EDITOR))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have the required permissions");
        }
        return true;
    }
}
