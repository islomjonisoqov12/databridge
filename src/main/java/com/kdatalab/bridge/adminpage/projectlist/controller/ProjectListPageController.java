package com.kdatalab.bridge.adminpage.projectlist.controller;

import com.kdatalab.bridge.adminpage.projectlist.projection.Project;
import com.kdatalab.bridge.adminpage.projectlist.service.ProjectListService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@Controller
@RequestMapping (value = "/admin")
@RequiredArgsConstructor
public class ProjectListPageController {

    private final ProjectListService projectListService;

    /**
     * This method is used to get the project list for project list in admin page
     * @param type
     * @param projectType
     * @param model
     * @return List of projects by user
     */
    @RequestMapping (value = "/project-list", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String projectList(@RequestParam(value = "type", required = false) String type,
                              // type = '1' =>only need check project, type<>'1'=> all project
                              @RequestParam(value = "projectType", required = false) String projectType,
                              Model model) {

        List<Project> projectList = projectListService.getProjectList(type, projectType);
        model.addAttribute("projectList", projectList);

        return "admin/project-list";
    }

    /**
     * This method return list of project types for using in project list in admin page
     *
     * @return List of project types
     */
    @ResponseBody
    @RequestMapping (value = "/project-types", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<String> projectTypes() {
        List<String> projectTypes = projectListService.getProjectTypes();
        return projectTypes;
    }
}
