package model.projects;

import java.util.ArrayList;
import java.util.Date;

import model.projects.builders.ProjectBuilder;
import model.projects.forms.ProjectAssignForm;
import model.projects.forms.ProjectCreationForm;
import model.projects.forms.ProjectUpdateForm;
import model.users.Developer;

public class ProjectManager implements ProjectDAO {

	private ArrayList<Project> projectList;

	/**
	 * Create and add a new project to the list.
	 * @param form The filled in form with the details about the project to be created.
	 */
	public void createProject(ProjectCreationForm form) {
		ProjectTeam team = new ProjectTeam();
		team.addMember(form.getLeadDeveloper(), Role.LEAD);
		Project p = (new ProjectBuilder())
						.setName(form.getName())
						.setCreationDate(new Date())
						.setStartDate(form.getStartDate())
						.setDescription(form.getDescription())
						.setTeam(team)
						.setVersion(new Version(1, 0, 0))
						.setBudgetEstimate(form.getBudgetEstimate())
						.getProject();
		projectList.add(p);
	}

	/**
	 * 
	 * @param form
	 */
	@Override
	public void updateProject(ProjectUpdateForm form) {
		Project project = form.getProject();
		for (Project p : projectList) {
			if (p == project) {
				p.setBudgetEstimate(form.getBudgetEstimate());
				p.setDescription(form.getDescription());
				p.setName(form.getName());
				p.setStartDate(form.getStartDate());
				p.setVersion(form.getVersion());
			}
		}
	}

	/**
	 * 
	 * @param project
	 */
	@Override
	public void deleteProject(Project project) {
		for (int i = 0; i < projectList.size(); i++) {
			if (projectList.get(i) == project) 
				projectList.remove(i);
		}
	}

	/**
	 * 
	 * @param form
	 */
	@Override
	public void assignToProject(ProjectAssignForm form) {
		Project project = form.getProject();
		for (Project p : projectList) {
			if (p == project) 
				p.getTeam().addMember(form.getDeveloper(), form.getRole());
		}
	}

	@Override
	public ArrayList<Project> getProjects() {
		return projectList;
	}

	@Override
	public ArrayList<Project> getProjectsForDeveloper(Developer dev) {
		ArrayList<Project> projs = new ArrayList<Project>();
		for (Project p : projectList) {
			if (p.getTeam().getLeadDeveloper() == dev) 
				projs.add(p);
		}
		return projs;
	}
}