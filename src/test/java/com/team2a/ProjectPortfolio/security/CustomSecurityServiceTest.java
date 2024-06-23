package com.team2a.ProjectPortfolio.security;

import com.team2a.ProjectPortfolio.Commons.Account;
import com.team2a.ProjectPortfolio.Commons.Request;
import com.team2a.ProjectPortfolio.Commons.RoleInProject;
import com.team2a.ProjectPortfolio.Services.ProjectService;
import com.team2a.ProjectPortfolio.Services.RequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomSecurityServiceTest {

    @Mock
    private ProjectService projectService;

    @Mock
    private RequestService requestService;

    @Mock
    private Authentication authentication;

    @Mock
    private Account account;


    @Mock
    private Request request;

    @InjectMocks
    private CustomSecurityService customSecurityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        requestService = mock(RequestService.class);
        account = mock(Account.class);
        request = mock(Request.class);
        when(authentication.getPrincipal()).thenReturn(account);
        when(account.getUsername()).thenReturn("testUser");

    }

    @Test
    void pmInProject_withPmRole_shouldReturnTrue() {
        UUID projectId = UUID.randomUUID();
        when(projectService.userBelongsToProject("testUser", projectId)).thenReturn(RoleInProject.PM);

        assertTrue(customSecurityService.pmInProject(authentication, projectId));
    }

    @Test
    void pmInProject_withoutPmRole_shouldThrowException() {
        UUID projectId = UUID.randomUUID();
        when(projectService.userBelongsToProject("testUser", projectId)).thenReturn(RoleInProject.EDITOR);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            customSecurityService.pmInProject(authentication, projectId);
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("You do not have the required permissions", exception.getReason());
    }

    @Test
    void editorInProject_withPmRole_shouldReturnTrue() {
        UUID projectId = UUID.randomUUID();
        when(projectService.userBelongsToProject("testUser", projectId)).thenReturn(RoleInProject.PM);

        assertTrue(customSecurityService.editorInProject(authentication, projectId));
    }

    @Test
    void editorInProject_withEditorRole_shouldReturnTrue() {
        UUID projectId = UUID.randomUUID();
        when(projectService.userBelongsToProject("testUser", projectId)).thenReturn(RoleInProject.EDITOR);

        assertTrue(customSecurityService.editorInProject(authentication, projectId));
    }

    @Test
    void editorInProject_withoutPmOrEditorRole_shouldThrowException() {
        UUID projectId = UUID.randomUUID();
        when(projectService.userBelongsToProject("testUser", projectId)).thenReturn(RoleInProject.CONTENT_CREATOR);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            customSecurityService.editorInProject(authentication, projectId);
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("You do not have the required permissions", exception.getReason());
    }

    @Test
    void userSpecific_withMatchingUsername_shouldReturnTrue() {
        assertTrue(customSecurityService.userSpecific(authentication, "testUser"));
    }

    @Test
    void userSpecific_withoutMatchingUsername_shouldThrowException() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            customSecurityService.userSpecific(authentication, "otherUser");
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("You do not have the required permissions", exception.getReason());
    }

    @Test
    void testIsCreatorOrPmInProject_WhenUserIsCreator() {
        UUID requestId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        Request r = new Request();
        Account account = new Account();
        account.setUsername("testUser");
        r.setAccount(account);

        when(requestService.getRequestById(requestId)).thenReturn(r);

        customSecurityService.setRequestService(requestService);

        boolean result = customSecurityService.isCreatorOrPmInProject(authentication, requestId, projectId);

        assertTrue(result);
    }

    @Test
    void testIsCreatorOrPmInProject_WhenUserIsPmInProject() {
        UUID requestId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        Account creatorAccount = mock(Account.class);

        when(creatorAccount.getUsername()).thenReturn("otherUser");
        when(projectService.userBelongsToProject("testUser", projectId)).thenReturn(RoleInProject.PM);

        Request r = new Request();
        Account account = new Account();
        account.setUsername("testUser");
        r.setAccount(account);

        when(requestService.getRequestById(requestId)).thenReturn(r);

        customSecurityService.setRequestService(requestService);


        boolean result = customSecurityService.isCreatorOrPmInProject(authentication, requestId, projectId);

        assertTrue(result);
    }

    @Test
    void testIsCreatorOrPmInProject_WhenUserIsNotCreatorOrPmInProject() {
        UUID requestId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        Account creatorAccount = mock(Account.class);

        when(requestService.getRequestById(requestId)).thenReturn(request);
        when(request.getAccount()).thenReturn(creatorAccount);
        when(creatorAccount.getUsername()).thenReturn("otherUser");
        when(projectService.userBelongsToProject("testUser", projectId)).thenReturn(RoleInProject.CONTENT_CREATOR);

        customSecurityService.setRequestService(requestService);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
            customSecurityService.isCreatorOrPmInProject(authentication, requestId, projectId));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("403 FORBIDDEN \"You do not have the required permissions\"", exception.getMessage());
    }

    @Test
    void testBelongsProjectBoolean () {
        when(authentication.getPrincipal()).thenReturn(account);
        when(projectService.userBelongsToProject(any(), any())).thenReturn(RoleInProject.CONTENT_CREATOR);
        assertEquals(customSecurityService.belongsToProjectBoolean(authentication, UUID.randomUUID()), true);
    }

    @Test
    void testBelongsProjectBoolean2 () {
        when(authentication.getPrincipal()).thenReturn(account);
        when(projectService.userBelongsToProject(any(), any())).thenReturn(RoleInProject.PM);
        assertEquals(customSecurityService.belongsToProjectBoolean(authentication, UUID.randomUUID()), true);
    }

    @Test
    void testBelongsProjectBoolean3 () {
        when(authentication.getPrincipal()).thenReturn(account);
        when(projectService.userBelongsToProject(any(), any())).thenReturn(RoleInProject.EDITOR);
        assertEquals(customSecurityService.belongsToProjectBoolean(authentication, UUID.randomUUID()), true);
    }

    @Test
    void testIsCreatorOrPm () {
        Request r = new Request();
        r.setAccount(account);
        when(authentication.getPrincipal()).thenReturn(account);
        customSecurityService.setRequestService(requestService);
        when(requestService.getRequestById(any())).thenReturn(r);
        assertTrue(customSecurityService.isCreatorOrPmInProject(authentication, UUID.randomUUID(),
                UUID.randomUUID()));

    }

}
