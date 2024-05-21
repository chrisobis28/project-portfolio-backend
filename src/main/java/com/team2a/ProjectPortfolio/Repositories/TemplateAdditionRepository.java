package com.team2a.ProjectPortfolio.Repositories;

import com.team2a.ProjectPortfolio.Commons.TemplateAddition;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateAdditionRepository extends JpaRepository<TemplateAddition, UUID> {

    @Query("SELECT ta FROM TemplateAddition ta WHERE ta.template.templateName = :templateName")
    List<TemplateAddition> findAllByTemplate_TemplateName (String templateName);
}
