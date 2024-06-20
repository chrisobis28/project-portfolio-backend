package com.team2a.ProjectPortfolio.WebSocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private ProjectWebSocketHandler projectWebSocketHandler;

    @Autowired
    private AccountWebSocketHandler accountWebSocketHandler;

    @Autowired
    private AccountProjectWebSocketHandler accountProjectWebSocketHandler;

    @Autowired
    private CollaboratorWebSocketHandler collaboratorWebSocketHandler;

    @Autowired
    private CollaboratorProjectWebSocketHandler collaboratorProjectWebSocketHandler;

    @Autowired
    private TagWebSocketHandler tagWebSocketHandler;

    @Autowired
    private TagProjectWebSocketHandler tagProjectWebSocketHandler;

    @Autowired
    private MediaProjectWebSocketHandler mediaProjectWebSocketHandler;

    @Autowired
    private LinkProjectWebSocketHandler linkProjectWebSocketHandler;

    @Override
    public void registerWebSocketHandlers (WebSocketHandlerRegistry registry) {
        registry.addHandler(projectWebSocketHandler, "/topic/projects")
                .setAllowedOrigins("*");
        registry.addHandler(accountWebSocketHandler, "/topic/accounts")
                .setAllowedOrigins("*");
        registry.addHandler(accountProjectWebSocketHandler, "/topic/accounts/project")
                .setAllowedOrigins("*");
        registry.addHandler(collaboratorWebSocketHandler, "/topic/collaborators")
                .setAllowedOrigins("*");
        registry.addHandler(collaboratorProjectWebSocketHandler, "/topic/collaborators/project")
                .setAllowedOrigins("*");
        registry.addHandler(tagWebSocketHandler, "/topic/tags")
                .setAllowedOrigins("*");
        registry.addHandler(tagProjectWebSocketHandler, "/topic/tags/project")
                .setAllowedOrigins("*");
        registry.addHandler(mediaProjectWebSocketHandler, "/topic/media/project")
                .setAllowedOrigins("*");
        registry.addHandler(linkProjectWebSocketHandler, "/topic/link/project")
                .setAllowedOrigins("*");
    }
}
