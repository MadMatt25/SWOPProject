package model.projects;

import model.projects.builders.ProjectBuilder;
import model.projects.builders.SubsystemBuilder;
import model.projects.forms.*;
import model.users.Developer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProjectManager {

	private ArrayList<Project> projectList;

	public ProjectManager() {
		projectList = new ArrayList<Project>();
	}
	
	/**
	 * Create and add a new project to the list.
	 * @param form The filled in form with the details about the project to be created.
	 */
	public Project createProject(ProjectCreationForm form) {
		if (form == null) throw new IllegalArgumentException("ProjectCreationForm can not be null!");

		ProjectTeam team = new ProjectTeam();
		team.addMember(form.getLeadDeveloper(), Role.LEAD);
		return createProject(form.getName(), form.getDescription(), new Date(), form.getStartDate(), form.getBudgetEstimate(), team, new Version(1, 0, 0));
	}

	public Project createProject(String name, String description, Date creationDate, Date startDate, double budgetEstimate, ProjectTeam team, Version version) {
		if (version == null)
			version = new Version(1, 0, 0);
		if (team == null)
			team = new ProjectTeam();
		
		Project p = (new ProjectBuilder())
				.setName(name)
				.setCreationDate(creationDate)
				.setStartDate(startDate)
				.setDescription(description)
				.setTeam(team)
				.setVersion(version)
				.setBudgetEstimate(budgetEstimate)
				.getProject();
		projectList.add(p);
		
		return p;
	}

	/**
	 * Fork an existing project and add it to the projects list.
	 * @param form The ProjectForkForm containing all the details about the project to be forked.
     */
	public Project createFork(ProjectForkForm form) {
		if (form == null) throw new IllegalArgumentException("ProjectForkForm can not be null!");

		return createFork(form.getProject(), form.getBudgetEstimate(), form.getVersion(), form.getStartDate());
	}

	public Project createFork(Project project, double budgetEstimate, Version version, Date startDate) {
		Project fork = new Project(project);
		fork.setBudgetEstimate(budgetEstimate);
		fork.setVersion(version);
		fork.setStartDate(startDate);
		projectList.add(fork);
		return fork;
	}

	/**
	 * Method to update a project.
	 * @param form The ProjectUpdateForm containing all the details about the project to update.
	 */
	public Project updateProject(ProjectUpdateForm form) {
		if (form == null) throw new IllegalArgumentException("ProjectUpdateForm can not be null!");

		return updateProject(form.getProject(), form.getName(), form.getDescription(), form.getBudgetEstimate(), form.getStartDate());
	}

	public Project updateProject(Project project, String name, String description, double budgetEstimate, Date startDate) {
		for (Project p : projectList) {
			if (p == project) {
				p.setBudgetEstimate(budgetEstimate);
				p.setDescription(description);
				p.setName(name);
				p.setStartDate(startDate);
			}
		}
		return project;
	}

	/**
	 * Method to delete a project.
	 * @param form The ProjectDeleteForm containing all the details about the project to delete.
	 */
	public void deleteProject(ProjectDeleteForm form) {
		if (form == null) throw new IllegalArgumentException("ProjectDeleteForm can not be null!");

		deleteProject(form.getProject());
	}

	public void deleteProject(Project project) {
		for (int i = 0; i < projectList.size(); i++) {
			if (projectList.get(i) == project)
				projectList.remove(i);
		}
	}

	/**
	 * Method to assign a developer to a project.
	 * @param form The ProjectAssignForm containing all the details about the assignment.
	 */
	public void assignToProject(ProjectAssignForm form) {
		if (form == null) throw new IllegalArgumentException("ProjectAssignForm can not be null!");

		assignToProject(form.getProject(), form.getDeveloper(), form.getRole());
	}

	public void assignToProject(Project project, Developer dev, Role role) {
		for (Project p : projectList) {
			if (p == project)
				p.getTeam().addMember(dev, role);
		}
	}

	/**
	 * Method to get all the projects in the system.
	 * @return List containing all the projects in the system.
     */
	public List<Project> getProjects() {
		ArrayList<Project> projects = new ArrayList<Project>();
		for (Project p : projectList)
			projects.add(p);
		return projects;
	}

	/**
	 * Method to get all the projects for which a given developer is lead.
	 * @param dev The developer for who to find the projects he/she leads.
	 * @return List containing all the projects for which the given developer is lead.
     */
	public List<Project> getProjectsForLeadDeveloper(Developer dev) {
		if (dev == null) throw new IllegalArgumentException("Developer can not be null!");

		ArrayList<Project> projs = new ArrayList<Project>();
		for (Project p : projectList) {
			if (p.getTeam().getLeadDeveloper() == dev) 
				projs.add(p);
		}
		return projs;
	}

	/**
	 * Method to create a subsystem.
	 * @param form The SubsystemCreationForm containing all the data needed to create the subsystem.
     */
	public void createSubsystem(SubsystemCreationForm form) {
		if (form == null) throw new IllegalArgumentException("SubsystemCreationForm can not be null!");

		createSubsystem(form.getName(), form.getDescription(), form.getProject(), form.getParent(), new Version(1, 0, 0));
	}
	
	public Subsystem createSubsystem(String name, String description, Project project, System parent, Version version) {
		if (version == null)
			version = new Version(1, 0, 0);
		
		Subsystem sub = (new SubsystemBuilder())
				.setDescription(description)
				.setName(name)
				.setProject(project)
				.setVersion(version)
				.setParent(parent)
				.getSubsystem();
		parent.addSubsystem(sub);
		return sub;
	}

	/**
	 * Method to get the subsystem in BugTrap with the given name.
	 * @param name The name for which to search.
	 * @return Subsystem with the given name.
     */
	public Subsystem getSubsystemWithName(String name) {
		if (name == null) throw new IllegalArgumentException("Subsystem name can not be null!");

		for (Project p : projectList) {
			for (Subsystem s : p.getAllDirectOrIndirectSubsystems()) {
				if (s.getName().equals(name))
					return s;
			}
		}
		return null;
	}
}