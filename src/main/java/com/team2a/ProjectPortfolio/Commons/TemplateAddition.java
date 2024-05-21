package com.team2a.ProjectPortfolio.Commons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "TEMPLATE_ADDITION")
public class TemplateAddition {

    @Id
    @Column(name="TEMPLATE_ADDITION_ID", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private UUID templateAdditionId;

    @Column(name="NAME")
    @Getter
    @Setter
    private String templateAdditionName;

    @Column(name="ISMEDIA")
    @Getter
    @Setter
    private boolean isMedia;

    @ManyToOne
    @JoinColumn(name="TEMPLATE_NAME")
    @Getter
    @Setter
    @JsonIgnore
    private Template template;

    public TemplateAddition(String templateAdditionName, boolean isMedia, Template template) {
        this.templateAdditionName = templateAdditionName;
        this.isMedia = isMedia;
        this.template = template;
    }
}
