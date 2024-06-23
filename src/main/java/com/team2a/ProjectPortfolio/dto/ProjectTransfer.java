package com.team2a.ProjectPortfolio.dto;

import com.team2a.ProjectPortfolio.Commons.RoleInProject;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Getter;

public class ProjectTransfer {

    @Getter
    @NotNull(message = "Project Id requires specification")
    private UUID projectId;

    @Getter
    @NotNull(message = "Project Name requires specification")
    private String name;

    @Getter
    @NotNull(message = "Role in Project requires specification")
    private RoleInProject roleInProject;

    public ProjectTransfer(UUID projectId, String name, RoleInProject roleInProject) {
        this.projectId = projectId;
        this.name = name;
        this.roleInProject = roleInProject;
    }
}
