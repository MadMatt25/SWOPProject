package ui;

import controllers.BugReportController;
import controllers.ProjectController;
import controllers.UserController;
import controllers.exceptions.UnauthorizedAccessException;
import model.BugTrap;
import model.bugreports.BugReport;
import model.bugreports.bugtag.BugTag;
import model.bugreports.bugtag.BugTagEnum;
import model.bugreports.comments.Comment;
import model.bugreports.comments.Commentable;
import model.bugreports.filters.FilterType;
import model.bugreports.forms.BugReportAssignForm;
import model.bugreports.forms.BugReportCreationForm;
import model.bugreports.forms.BugReportUpdateForm;
import model.bugreports.forms.CommentCreationForm;
import model.projects.Project;
import model.projects.Role;
import model.projects.Subsystem;
import model.projects.Version;
import model.projects.forms.*;
import model.users.Developer;
import model.users.Issuer;
import model.users.User;
import model.users.UserCategory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

	private static boolean quit;
	private static BugTrap bugTrap;
	private static UserController userController;
	private static ProjectController projectController;
	private static BugReportController bugReportController; 
	private static Scanner input;
	
	public static void main(String[] args) {
		// Initialize BugTrap
		try {
			init();
		} catch (UnauthorizedAccessException e) {
			
		}
		
		// Initialize CLI
		quit = false;
		
		System.out.println("Welcome to BugTrap.");
		System.out.println("");
		
		input = new Scanner(System.in);
		
		while (!quit) {
			User user = userController.getLoggedInUser();
			if (user != null)
				System.out.print("[" + user.getUserName() + "] ");
			String line = input.nextLine();
			processCommand(line);
		}
		
		input.close();
		System.out.println("Goodbye.");
	}
	
	public static void init() throws UnauthorizedAccessException {
		bugTrap = new BugTrap();
		userController = new UserController(bugTrap);
		projectController = new ProjectController(bugTrap);
		bugReportController = new BugReportController(bugTrap);
		
		bugTrap.initialize();
	}

	public static void processCommand(String command) {
		String cmd = command.trim().toLowerCase();
		
		if (cmd.equals("help")) {
			help();
		} else if (cmd.equals("login")) {
			login();
		} else if (cmd.equals("exit")) {
			quit = true;
		} else if (cmd.equals("createproject")) {
			createProject();
		} else if (cmd.equals("updateproject")) {
			updateProject();
		} else if (cmd.equals("deleteproject")) {
			deleteProject();
		} else if (cmd.equals("showproject")) {
			showProject();
		} else if (cmd.equals("createsubsystem")) {
			createSubSystem();
		} else if (cmd.equals("createbugreport")) {
			createBugReport();
		} else if (cmd.equals("inspectbugreport")) {
			inspectBugReport();
		} else if (cmd.equals("createcomment")) {
			createComment();
		} else if (cmd.equals("assignproject")) {
			assignToProject();
		} else if (cmd.equals("assignbugreport")) {
			assignToBugReport();
		} else if (cmd.equals("updatebugreport")) {
			updateBugReport();
		} else
			System.out.println("Command not recognized.");
	}
	
	public static void help() {
		System.out.println("Available commands: ");
		
		System.out.println("exit : End the application.");
		System.out.println("login : Log into the system.");
		System.out.println("createproject : Create a Project.");
		System.out.println("deleteproject : Remove a Project from the system.");
		System.out.println("showproject : Show the details of a Project.");
		System.out.println("createsubsystem : Create a new Subsystem for a Project.");
		System.out.println("createbugreport : Create a new BugReport for a Subsystem.");
		System.out.println("inspectbugreport : Show the details of a BugReport.");
		System.out.println("createcomment : Write a comment about a BugReport or some other comment." );
		System.out.println("assignproject : Assign a Developer to a Project.");
		System.out.println("assignbugreport : Assign a Developer to a BugReport.");
		System.out.println("updatebugreport : Update the details of a BugReport.");
	}
	
	public static void login() {
		boolean valid = false;
		int category = 0;
		List<User> users = new ArrayList<User>();
		User selectedUser = null;
		while (!valid) {
			valid = true;
			System.out.println("Select a user cateogry by entering the number: ");
			System.out.println(" 1. Administrator");
			System.out.println(" 2. Issuer");
			System.out.println(" 3. Developer");
			category = input.nextInt();
			input.nextLine();
					
			switch (category) {
				case 1:
					users = userController.getUserList(UserCategory.ADMIN);
					break;
				case 2:
					users = userController.getUserList(UserCategory.ISSUER);
					break;
				case 3:
					users = userController.getUserList(UserCategory.DEVELOPER);
					break;
				default:
					valid = false;
					continue;
			}
		}
		
		selectedUser = selectUser(users);
		
		String greeting = userController.loginAs(selectedUser);
		System.out.println(greeting);
	}

	public static void createProject() {
		boolean valid = false;
		int selected = 0;
		while (!valid) {
			System.out.println("Please indicate wheter you would like to: ");
			System.out.println(" 1. Fork an existing project");
			System.out.println(" 2. Create a new project");
			selected = input.nextInt();
			input.nextLine();

			if (selected > 0 && selected < 3)
				valid = true;
		}

		if (selected == 1)
			createForkedProject();
		if (selected == 2)
			createNewProject();;
	}

	public static void createForkedProject() {
		ProjectForkForm form;
		Project project;
		try {
			form = projectController.getProjectForkForm(); 
			project = selectProject(projectController.getProjectList());
			form.setProject(project);
		} catch (UnauthorizedAccessException e) {
			System.out.println(e.getMessage());
			return;
		}

		System.out.println("Enter the budget estimate for the project:");
		form.setBudgetEstimate(input.nextDouble());
		input.nextLine();

		boolean valid = false;
		while (!valid) {
			try {
				System.out.println("Enter the start date for the project (dd/mm/yyyy):");
				form.setStartDate((new SimpleDateFormat("dd/MM/yyyy")).parse(input.nextLine()));
				valid = true;
			} catch (Exception e) { }
		}

		valid = false;
		while (!valid) {
			try {
				System.out.println("Enter the version number for the project:");
				String vNumber = input.nextLine();
				int major = Integer.parseInt(vNumber.split(".")[0]);
				int minor = Integer.parseInt(vNumber.split(".")[1]);
				int revision = Integer.parseInt(vNumber.split(".")[2]);
				form.setVersion(new Version(major, minor, revision));
			} catch (Exception e) { }
		}

		projectController.forkProject(form);
		System.out.println("Project is forked.");
	}

	public static void createNewProject() {
		ProjectCreationForm form;
		try {
			 form = projectController.getProjectCreationForm();
		} catch (UnauthorizedAccessException e) {
			System.out.println(e.getMessage());
			return;
		}

		System.out.println("Enter the name of the project:");
		form.setName(input.nextLine());
		System.out.println("Enter the description of the project:");
		form.setDescription(input.nextLine());
		System.out.println("Enter the budget estimate for the project:");
		form.setBudgetEstimate(input.nextDouble());
		input.nextLine();
		
		boolean valid = false;
		while (!valid) {
			try {
				System.out.println("Enter the start date for the project (dd/mm/yyyy):");
				form.setStartDate((new SimpleDateFormat("dd/MM/yyyy")).parse(input.nextLine()));
				valid = true;
			} catch (Exception e) { }
		}
		
		User lead = selectUser(userController.getUserList(UserCategory.DEVELOPER));
		form.setLeadDeveloper((Developer)lead);
		
		projectController.createProject(form);
		System.out.println("Project is created.");
	}
	
	public static void updateProject() {
		ProjectUpdateForm form;
		try {
			 form = projectController.getProjectUpdateForm();
		} catch (UnauthorizedAccessException e) {
			System.out.println(e.getMessage());
			return;
		}
		
		Project project = null;
		try {
			project = selectProject(projectController.getProjectList());
		} catch (UnauthorizedAccessException e1) {
			System.out.println(e1.getMessage());
		}
		form.setProject(project);
		
		System.out.println("Enter the name of the project:");
		form.setName(input.nextLine());
		System.out.println("Enter the description of the project:");
		form.setDescription(input.nextLine());
		System.out.println("Enter the budget estimate for the project:");
		form.setBudgetEstimate(input.nextDouble());
		
		boolean valid = false;
		while (!valid) {
			try {
				System.out.println("Enter the start date for the project (dd/mm/yyyy):");
				form.setStartDate((new SimpleDateFormat("dd/MM/yyyy")).parse(input.nextLine()));
				valid = true;
			} catch (Exception e) { }
		}
		
		User lead = selectUser(userController.getUserList(UserCategory.DEVELOPER));
		form.setLeadDeveloper((Developer)lead);
		
		projectController.updateProject(form);
		System.out.println("Project is updated.");
	}
	
	public static void assignToProject() {
		ProjectAssignForm form = null;
		try {
			form = projectController.getProjectAssignForm();
		} catch (UnauthorizedAccessException e) {
			System.out.println(e.getMessage());
		}
		
		Project project = null;
		try {
			project = selectProject(projectController.getProjectList());
		} catch (UnauthorizedAccessException e) {
			System.out.println(e.getMessage());
		}
		form.setProject(project);
		Developer dev = (Developer) selectUser(userController.getUserList(UserCategory.DEVELOPER));
		form.setDeveloper(dev);
		Role role = selectRole();
		form.setRole(role);
		
		projectController.assignToProject(form);
		System.out.println("Developer is assigned.");
	}

	public static void deleteProject() {
		ProjectDeleteForm form;
		try {
			form = projectController.getProjectDeleteForm();
		} catch (UnauthorizedAccessException e) {
			System.out.println(e.getMessage());
			return;
		}
		
		Project project = null;
		try {
			project = selectProject(projectController.getProjectList());
		} catch (UnauthorizedAccessException e) {
			System.out.println(e.getMessage());
		}
		form.setProject(project);
		
		projectController.deleteProject(form);
		System.out.println("Project is deleted.");
	}
	
	public static void showProject() {
		Project project = null;
		try {
			project = selectProject(projectController.getProjectList());
			System.out.println(" -- " + project.getName() + " -- ");
			System.out.println(" Description: " + project.getDescription());
			System.out.println(" Budget estimate: " + project.getBudgetEstimate());
			System.out.println(" Creation date: " + project.getCreationDate().toString());
			System.out.println(" Start date: " + project.getStartDate().toString());
			System.out.println(" Version: " + project.getVersion());
			
			for (Subsystem system : project.getAllDirectOrIndirectSubsystems()) {
				System.out.println(" -- " + system.getName() + " -- ");
				System.out.println(" Description: " + system.getDescription());
				System.out.println(" Version: " + system.getVersion());
			}
		} catch (UnauthorizedAccessException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void createSubSystem() {
		SubsystemCreationForm form;  
		try {  
			form = projectController.getSubsystemCreationForm();  
			ArrayList<model.projects.System> allSystems = new ArrayList<model.projects.System>();  
			for (Project project : projectController.getProjectList()) {  
				allSystems.add(project);  
				for (model.projects.System sys : project.getAllDirectOrIndirectSubsystems()) {   
					allSystems.add(sys);  
				}  
			}  
		    		  
			model.projects.System selectedSystem = selectSystem(allSystems);  
			form.setParent(selectedSystem);  
				  
			System.out.println("Enter the name of the subsystem:");  
		 	form.setName(input.nextLine());  
		 	System.out.println("Enter the description of the subsystem:");  
		 	form.setDescription(input.nextLine());  
		} catch (UnauthorizedAccessException e) {  
			System.out.println(e.getMessage());  
			return;  
		}  
		
	 	projectController.createSubsystem(form);
	 	System.out.print("Subsystem is created.");
	}
	
	public static void createBugReport() {
		BugReportCreationForm form = null;
		try {
			form = bugReportController.getBugReportCreationForm();
			Project chosenProject = selectProject(projectController.getProjectList());
			Subsystem chosenSubsystem = selectSubsystem(chosenProject.getSubsystems());
			
			form.setIssuer((Issuer) userController.getLoggedInUser());
			form.setSubsystem(chosenSubsystem);
			System.out.println("Enter the title of the BugReport:");
			form.setTitle(input.nextLine());
			System.out.println("Enter a description:");
			form.setDescription(input.nextLine());
			
			List<BugReport> allReports = bugReportController.getBugReportList();
			ArrayList<BugReport> projectReports = new ArrayList<BugReport>();
			ArrayList<Subsystem> projectSubs = chosenProject.getAllDirectOrIndirectSubsystems();
			for (BugReport r : allReports) 
				if (projectSubs.contains(r.getSubsystem()))
					projectReports.add(r);
			
			ArrayList<BugReport> selectedDependencies = selectBugReports(projectReports);
			form.setDependsOn(selectedDependencies);
		} catch (UnauthorizedAccessException e) {
			System.out.println(e.getMessage());
		}
					
		bugReportController.createBugReport(form);
		System.out.println("The bug report is created.");
	}
	
	public static void inspectBugReport() {
		BugReport bugReport;
		bugReport = selectBugReport();
		if (bugReport == null)
			return;
		System.out.println(" -- " + bugReport.getTitle() + " -- ");
		System.out.println(" Description: " + bugReport.getDescription());
		System.out.println(" BugTag: " + bugReport.getBugTag().toString());
		System.out.println(" Creation Date: " + bugReport.getCreationDate());
		System.out.println(" Issued by: " + bugReport.getIssuedBy().getUserName());
		System.out.println(" Subsystem: " + bugReport.getSubsystem().getName());
		if (bugReport.getBugTag().getBugTagEnum() == BugTagEnum.DUPLICATE)
			System.out.println(" Duplicate: " + bugReport.getDuplicate().getTitle());
		System.out.println(" Assignees: ");
		for (Developer dev : bugReport.getAssignees())
			System.out.println(" - " + dev.getUserName());
		System.out.println(" Depends on: ");
		for (BugReport bug : bugReport.getDependsOn())
			System.out.println(" - " + bug.getTitle());
		System.out.println(" Comments: ");
		
		printComments(bugReport.getComments());
	}
	
	public static void createComment() {
		try {
			CommentCreationForm form = bugReportController.getCommentCreationForm();
			
			BugReport chosenBugReport = selectBugReport();
			boolean valid = false;
			while (!valid) {
				System.out.println("Comment directly on this bug report or on one of its comments?");
				System.out.println("1. Directly");
				System.out.println("2. Comments");
				int selected = input.nextInt();
				input.nextLine();
				if (selected == 1)
					form.setCommentable(chosenBugReport);
				else if (selected == 2) {
					form.setCommentable(selectComment(chosenBugReport, null));
				} else 
					continue; 
				
				System.out.println("Enter your comment:");
				form.setText(input.nextLine());
				
				bugReportController.createComment(form);
				System.out.println("Comment is created.");
				valid = true;
			}
			
		} catch (UnauthorizedAccessException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void assignToBugReport() {
		BugReportAssignForm form = null;
		BugReport report = null;
		Developer dev = null;
		try {
			form = bugReportController.getBugReportAssignForm();
			report = selectBugReport();
			dev = (Developer)selectUser(userController.getUserList(UserCategory.DEVELOPER));
		} catch (UnauthorizedAccessException e) {
			System.out.println(e.getMessage());
			return;
		}
		
		form.setBugReport(report);
		form.setDeveloper(dev);
		bugReportController.assignToBugReport(form);
		System.out.println("Developer is assigned to bug report.");
	}
	
	public static void updateBugReport() {
		BugReportUpdateForm form = null;
		try {
			form = bugReportController.getBugReportUpdateForm();
		} catch (UnauthorizedAccessException e) {
			System.out.println(e.getMessage());
			return;
		}
		
		BugReport selected = selectBugReport();
		form.setBugReport(selected);
		form.setBugTag(selectBugTag());
		
		bugReportController.updateBugReport(form);
		System.out.println("Bug report is updated.");
	}
	
	private static User selectUser(List<User> users) {
		while (true) {
			System.out.println("Select a user by entering the username: ");
			for (User user : users)
				System.out.println(user.getUserName() + " (" + user.getFirstName() + " " + user.getMiddleName() + " " + user.getLastName() + ")");
			
			String name = input.nextLine();
			for (User user : users) {
				if (user.getUserName().equals(name))
					return user;
			}
		}
	}
	
	private static void printComments(List<Comment> comments) {
		for (int index = 0; index < comments.size(); index++) {
			String level = " " + (index+1) + ".";
			System.out.println(level + comments.get(index).getText());
			printComments(comments.get(index).getComments(), level);
		}
	}

	private static void printComments(List<Comment> comments, String string) {
		for (int index = 0; index < comments.size(); index++) {
			String level = string + (index+1) + ".";
			System.out.println(level + comments.get(index).getText());
			printComments(comments.get(index).getComments(), level);
		}
	}
	

	private static BugReport selectBugReport() {
		FilterType type = selectFilterType();
		System.out.println("Enter the search parameter: ");
		String parameter = input.nextLine();
		List<BugReport> filtered;
		try {
			filtered = bugReportController.getOrderedList(new FilterType[]{type}, new String[]{parameter});
			while (true) {
				System.out.println("Select a bugreport by entering its number: ");
				int number = 1;
				for (BugReport bugReport : filtered) {
					System.out.println(number + ". " + bugReport.getTitle());
					number++;
				}
				
				int selected = input.nextInt();
				input.nextLine();
				if (selected <= filtered.size())
					return filtered.get(selected - 1);
			}
		} catch (UnauthorizedAccessException e) {
			System.out.println(e.getMessage());
		}
		
		return null;
	}
	
	private static ArrayList<BugReport> selectBugReports(ArrayList<BugReport> reports) {
		while (true) {
			ArrayList<BugReport> selected = new ArrayList<BugReport>();
			System.out.println("Select bugreports by entering its numbers separated by commas: ");
			int number = 1;
			for (BugReport bugReport : reports) {
				System.out.println(number + ". " + bugReport.getTitle());
				number++;
			}
			
			try {
				String[] rawInput = input.nextLine().split(",");
				for (String raw : rawInput)
					selected.add(reports.get(Integer.parseInt(raw.trim()) - 1));
				return selected;
				
			} catch (Exception e) {
			}
		}
	}
		
	private static Commentable selectComment(BugReport report, Comment currentlySelected) {
		while (true) {			
			int number = 1;
			if (currentlySelected == null) {
				System.out.println("Select a comment by entering its number: ");
				for (Comment c : report.getComments()) {
					System.out.println(number + ". " + c.getText());
					number++;
				}
			} else {
				System.out.println("Select the current comment or continue on one of its replies: ");
				System.out.println("Current comment: " + currentlySelected.getText());
				System.out.println("0. Select current comment.");
				for (Comment c : currentlySelected.getComments()) {
					System.out.println(number + ". " + c.getText());
					number++;
				}
			}
			
			int selected = input.nextInt();
			input.nextLine();
			if (selected == 0 && currentlySelected != null)
				return currentlySelected;
			
			if (selected > 0 && currentlySelected != null && selected <= currentlySelected.getComments().size())
				currentlySelected = currentlySelected.getComments().get(selected - 1);
			
			else if (selected > 0 && report != null && selected <= report.getComments().size())
				currentlySelected = report.getComments().get(selected - 1);
		}
	}
	
	private static BugTag selectBugTag() {
		while (true) {
			System.out.println("Select a bug tag by entering its number");
			
			int number = 1;
			for (BugTagEnum tag : BugTagEnum.values()){
				System.out.println(number + ". " + tag.toString());
				number++;
			}
			
			int selected = input.nextInt();
			input.nextLine();
			if (selected <= BugTagEnum.values().length)
				return BugTagEnum.values()[selected - 1].createBugTag();
		}
	}

	private static Role selectRole() {
		while (true) {
			System.out.println("Select a role by entering its numner");
			
			int number = 1;
			for (Role role : Role.values()) {
				System.out.println(number + ". " + role.toString());
				number++;
			}
			
			int selected = input.nextInt();
			input.nextLine();
			if (selected <= Role.values().length)
				return Role.values()[selected-1];
		}
	}

	private static FilterType selectFilterType() {
		while(true) {
			System.out.println("Select a search mode by entering its number: ");
			int number = 1;
			for (FilterType type : FilterType.values()) {
				System.out.println(number + ". " + type.toString());
				number++;
			}
			
			int selected = input.nextInt();
			input.nextLine();
			if (selected <= FilterType.values().length)
				return FilterType.values()[selected - 1];
		}
	}
	
	private static Project selectProject(List<Project> projects) {
		while (true) {
			System.out.println("Select a project by entering its number: ");
			int number = 1;
			for (Project project : projects) {
				System.out.println(number + ". " + project.getName());
				number++;
			}
			
			int selected = input.nextInt();
			input.nextLine();
			if (selected <= projects.size())
				return projects.get(selected - 1);
		}
	}
	private static model.projects.System selectSystem(List<model.projects.System> systems) {
		while (true) {
			System.out.println("Select a project or subsystem by entering its number: ");
			int number = 1;
			for (model.projects.System sys : systems) {
				System.out.println(number + ". " + sys.getName());
				number++;
			}
			
			int selected = input.nextInt();
			input.nextLine();
			if (selected <= systems.size())
				return systems.get(selected - 1);
		}
	}
	
	private static Subsystem selectSubsystem(List<Subsystem> subsystems) {
		while (true) {
			System.out.println("Select a subsystem by entering its number: ");
			int number = 1;
			for (Subsystem sub : subsystems) {
				System.out.println(number + ". " + sub.getName());
				number++;
			}
			
			int selected = input.nextInt();
			input.nextLine();
			if (selected <= subsystems.size())
				return subsystems.get(selected - 1);
		}
	}
}
