package com.team2a.ProjectPortfolio.security;

public class Permissions {

    public final static String ADMIN_ONLY = "hasRole('ADMIN')";
    private final static String ADMIN_ALLOW = "hasRole('ADMIN') or ";
    public final static String PM_ONLY = ADMIN_ALLOW + "hasRole('PM')";

    public final static String USER_SPECIFIC = ADMIN_ALLOW +
        "@customSecurityService.userSpecific(authentication, #username)";

    public final static String PM_IN_PROJECT = ADMIN_ALLOW +
        "@customSecurityService.pmInProject(authentication, #projectId)";

    public final static String EDITOR_IN_PROJECT = ADMIN_ALLOW +
        "@customSecurityService.editorInProject(authentication, #projectId)";

    public final static String USER_IN_PROJECT = ADMIN_ALLOW +
        "@customSecurityService.belongsToProjectBoolean(authentication, #projectId)";

    public final static String IS_CREATOR_OR_PM_IN_PROJECT = ADMIN_ALLOW +
        "@customSecurityService.isCreatorOrPmInProject(authentication, #requestId, #request.project.projectId)";
}
