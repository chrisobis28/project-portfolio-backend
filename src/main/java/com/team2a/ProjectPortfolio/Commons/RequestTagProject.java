package com.team2a.ProjectPortfolio.Commons;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "REQUESTTAGPROJECT")
public class RequestTagProject {
    @Id
    @Column(name="REQUESTTAGPROJECT_ID", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private UUID requestTagProjectID;

    @Column(name="IS_REMOVE")
    @Getter
    @Setter
    private boolean isRemove;
}
