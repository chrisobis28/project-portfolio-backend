package com.team2a.ProjectPortfolio.Commons;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "TEMPLATE")
public class Template {

    @Id
    @Column(name="NAME", nullable=false)
    @Getter
    @Setter
    private String templateName;

    @Column(name="DESCRIPTION")
    @Getter
    @Setter
    @NotNull
    private String standardDescription;

    @Column(name="BIBTEX")
    @Getter
    @Setter
    @NotNull
    private String standardBibtex;

    @Column(name="NUMBER_COLLABORATORS")
    @Getter
    @Setter
    @NotNull
    private int numberOfCollaborators;

    @Getter
    @Setter
    @OneToMany
    @OnDelete(action=OnDeleteAction.SET_NULL)
    @JoinColumn(name="TEMPLATE_NAME")
    private List<Project> projects = new ArrayList<>();

    @Getter
    @Setter
    @OneToMany(cascade= CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action= OnDeleteAction.CASCADE)
    @JoinColumn(name="TEMPLATE_NAME")
    private List<TemplateAddition> templateAdditions = new ArrayList<>();

    public Template(String templateName, String standardDescription, String standardBibtex,
                    int numberOfCollaborators) {
        this.templateName = templateName;
        this.standardDescription = standardDescription;
        this.standardBibtex = standardBibtex;
        this.numberOfCollaborators = numberOfCollaborators;
    }
}
