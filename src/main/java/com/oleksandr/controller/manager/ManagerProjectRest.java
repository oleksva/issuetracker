package com.oleksandr.controller.manager;

import com.fasterxml.jackson.annotation.JsonView;
import com.oleksandr.dao.ProjectDao;
import com.oleksandr.entity.Employee;
import com.oleksandr.entity.Project;
import com.oleksandr.entity.json.Views;
import com.oleksandr.service.reports.data.project.Statistic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by nuts on 25.01.17.
 */
@RestController
public class ManagerProjectRest {

    private final ProjectDao projectService;

    @Autowired
    public ManagerProjectRest(ProjectDao projectService) {
        this.projectService = projectService;
    }

    @JsonView(Views.Summary.class)
    @RequestMapping(value = "/manager/projectStatistic")
    public Statistic getProjectStatistic(@RequestParam String idProject) {
        try {
            Statistic statistic = new Statistic();
            long idPr = Long.parseLong(idProject);
            statistic.setStatistic(projectService.getStatistic(idPr));
            return statistic;
        } catch (NumberFormatException ignore) {
            return null;
        }
    }


    @JsonView(Views.Summary.class)
    @RequestMapping(value = "/manager/project")
    public Project getProject(@RequestParam("projectId") String idS, @ModelAttribute("employee") Employee employee) {
        try {
            long id = Long.parseLong(idS);
            Project project = projectService.getById(id);
            if(project.getManager().getEmployeeId() == employee.getEmployeeId()) {
                return project;
            } else {
                throw new AccessDeniedException("You do not have access to this page");
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @JsonView(Views.ProjectNameAndID.class)
    @RequestMapping(value = "/manager/projectList")
    public List<Project> getProjects(@ModelAttribute("employee") Employee employee) {
        return projectService.getAllNameAndIdByManagerId(employee.getEmployeeId());
    }
}