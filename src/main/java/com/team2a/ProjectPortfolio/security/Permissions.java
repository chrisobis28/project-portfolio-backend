package com.team2a.ProjectPortfolio.security;

public class Permissions {

    private final static String ADMIN_ALLOW = "hasRole('ADMIN') or ";
    public final static String PM_ONLY = ADMIN_ALLOW + "hasRole('PM')";

    public static final String PM_IN_PROJECT = ADMIN_ALLOW +
        "@customSecurityService.pmInProject(authentication, #projectId)";

    public static final String EDITOR_IN_PROJECT = ADMIN_ALLOW +
        "@customSecurityService.editorInProject(authentication, #projectId)";
}
