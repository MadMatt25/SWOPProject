package model.bugreports;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import controllers.exceptions.UnauthorizedAccessException;
import model.BugTrap;
import model.bugreports.bugtag.BugTag;
import model.bugreports.builders.BugReportBuilder;
import model.bugreports.comments.Commentable;
import model.bugreports.filters.BugReportFilter;
import model.bugreports.filters.FilterType;
import model.notifications.signalisations.BugReportCreationSignalisation;
import model.projects.IProject;
import model.projects.ISubsystem;
import model.projects.ISystem;
import model.projects.Subsystem;
import model.users.IUser;

/**
 * Class that stores and manages BugReports.
 */
public class BugReportManager {

	private final List<BugReport> bugReportList; //List that keeps BugReports.
	private final BugTrap bugTrap;
	/**
	 * Constructor.
	 * @param bugTrap the BugTrap System
	 */
	public BugReportManager(BugTrap bugTrap) {
		this.bugReportList = new ArrayList<BugReport>();
		this.bugTrap = bugTrap;
	}
	
	/**
	 * returns an array of all the different filter types
	 * @return an array of filter types
	 */
	public FilterType[] getFilterTypes() {
		return FilterType.values();
	}

	/**
	 * returns an ordered list of bug reports
	 * @param types Filter Types to filter by.
	 * @param arguments Filter arguments.
	 * @return an ordered list of bug reports
	 */
	public List<IBugReport> getOrderedList(FilterType[] types, String[] arguments) {
		List<IBugReport> filteredList = getBugReportList();
		
		BugReportFilter filter = new BugReportFilter(filteredList);

		for (int index = 0; index < types.length; index++)
			filter.filter(types[index], arguments[index]);
		
		return filter.getFilteredList();
	}

	/**
	 * returns a copy of the bug report list
	 * @return list of bug reports
	 */
	public List<IBugReport> getBugReportList(){
		ArrayList<IBugReport> clonedList = new ArrayList<IBugReport>();
		clonedList.addAll(bugReportList);
		return clonedList;
	}

	/**
	 * returns all the bug reports for a given project
	 * @param project Project to return BugReports for.
	 * @return list of bug reports
	 */
	public List<IBugReport> getBugReportsForProject(IProject project) {
		return getBugReportsForSystem(project);
	}
	
	/**
	 * Return all BugReports for given System.
	 * @param system The System to get all BugReports for.
	 * @return  The BugReports of given System.
	 */
	public List<IBugReport> getBugReportsForSystem(ISystem system) {
		List<ISubsystem> subs = system.getAllDirectOrIndirectSubsystems();
		List<IBugReport> reports = new ArrayList<IBugReport>();
		if(system.getParent() != null)
			subs.add((Subsystem) system);

		for (IBugReport r : bugReportList)
			for (ISubsystem s : subs)
				if (r.getSubsystem() == s)
					reports.add(r);

		return reports;
	}

	/**
	 * Delete the BugReports for given System and deletes the registrations for this bug report
	 * @param system The System for which to delete the BugReports
	 * @throws UnauthorizedAccessException If the User is not authorized for this action.
	 */
	public void deleteBugReportsForSystem(ISystem system) throws UnauthorizedAccessException {
		for (IBugReport report : getBugReportsForSystem(system)){
			bugTrap.getNotificationManager().deleteRegistrationsForObservable(report);
			((BugReport)report).terminate();
			bugReportList.remove(report);
		}
	}

	/**
	 * adds a bug report
	 * @param title Title of the BugReport
	 * @param description Description of the BugReport
	 * @param creationDate Creation Date of the BugReport
	 * @param subsystem Subsystem of the BugReport
	 * @param issuer Issuer of the BugReport
	 * @param dependencies Dependencies of the BugReport
	 * @param assignees Assignees of the BugReport
	 * @param tag Tag of the BugReport
	 */
	public void addBugReport(String title, String description, Date creationDate, ISubsystem subsystem, IUser issuer, List<IBugReport> dependencies, List<IUser> assignees, BugTag tag) {
		BugReport report = new BugReportBuilder().setTitle(title)
				.setDescription(description)
				.setSubsystem(subsystem)
				.setIssuer(issuer)
				.setDependsOn(dependencies)
				.setCreationDate(creationDate)
				.setAssignees(assignees)
				.setBugTag(tag)
				.getBugReport();
		bugReportList.add(report);
		((Subsystem)subsystem).signal(new BugReportCreationSignalisation(report));
	}
	
	/**
	 * Add a BugReport with a Target Milestone
	 * @param title Title of the BugReport
	 * @param description Description of the BugReport
	 * @param creationDate Creation Date of the BugReport
	 * @param subsystem Subsystem of the BugReport
	 * @param issuer Issuer of the BugReport
	 * @param dependencies Dependencies of the BugReport
	 * @param assignees Assignees of the BugReport
	 * @param tag Tag of the BugReport
	 * @param milestone Target Milestone of the BugReport
	 */
	public void addBugReportWithTargetMilestone(String title, String description, Date creationDate, ISubsystem subsystem, IUser issuer, List<IBugReport> dependencies, List<IUser> assignees, BugTag tag, List<Integer> milestone) {
		TargetMilestone target = new TargetMilestone(milestone);
		if(target.compareTo(subsystem.getAchievedMilestone()) <= 0) throw new IllegalArgumentException("The target milestone should be strict higher than the achieved milestone of the subsystem");
		
		BugReport report = new BugReportBuilder().setTitle(title)
				.setDescription(description)
				.setSubsystem(subsystem)
				.setIssuer(issuer)
				.setDependsOn(dependencies)
				.setCreationDate(creationDate)
				.setAssignees(assignees)
				.setBugTag(tag)
				.setMilestone(milestone)
				.getBugReport();
		bugReportList.add(report);
		((Subsystem)subsystem).signal(new BugReportCreationSignalisation(report));
	}

	/**
	 * Assigns a given developer to a BugReport.
	 * @param bugReport BugReport to add Developer to.
	 * @param dev Developer to assign.
	 * @throws UnauthorizedAccessException If the current user is not allowed to do this action.
	 */
	public void assignToBugReport(IBugReport bugReport, IUser dev) throws UnauthorizedAccessException{
		IProject project = bugReport.getSubsystem().getProject();
		IUser user = bugTrap.getUserManager().getLoggedInUser();
		if(!project.isLead(user) && !project.isTester(user)) throw new UnauthorizedAccessException("A lead or tester should be logged in to assign bug report");
		
		BugReport report = null;
		for (BugReport b : bugReportList)
			if (b == bugReport)
				report = b;
		
		report.assignDeveloper(dev);
	}
	
	/**
	 * update a bug report with a tag
	 * @param bugReport BugReport to update.
	 * @param tag Tag to update to.
	 * @throws UnauthorizedAccessException If the current user is not allowed to do this action.
	 */
	public void updateBugReport(IBugReport bugReport, BugTag tag) throws UnauthorizedAccessException {
		if (bugReport == null || tag == null)
			throw new IllegalArgumentException("Arguments should not be null.");
		if (tag.hasToBeLeadToSet() && !(bugReport.getProject().getLeadDeveloper() == bugTrap.getUserManager().getLoggedInUser()))
			throw new UnauthorizedAccessException();
		
		BugReport report = null;
		for (BugReport b : bugReportList)
			if (b == bugReport)
				report = b;
		
		report.updateBugTag(tag);
	}

	/**
	 * Adds a comment to a Commentable object.
	 * @param commentable Commentable to comment on.
	 * @param text Text of the Comment.
	 */
	public void addComment(Commentable commentable, String text) {
		if (commentable == null || text == null)
			throw new IllegalArgumentException("Arguments should not be null.");
		
		commentable.addComment(text);
	}
	
	/**
	 * Proposes a test to a given BugReport.
	 * @param report given bug report
	 * @param test Test to propose
	 * @throws UnauthorizedAccessException if the logged in user is not a tester for this bugreport
	 */
	public void proposeTest(IBugReport report, String test) throws UnauthorizedAccessException {
		if (report == null || test == null)
			throw new IllegalArgumentException("Arguments should not be null.");
		IUser user = bugTrap.getUserManager().getLoggedInUser();
		BugReport bugReport = (BugReport) report;
		if(!bugReport.getSubsystem().getProject().isTester(user))
			throw new UnauthorizedAccessException("The logged in user needs to be a tester to propose a test");
		
		bugReport.addTest(test);
	}
	
	/**
	 * Proposes a patch to a given BugReport.
	 * @param report Given bug report
	 * @param patch The Patch to propose.
	 * @throws UnauthorizedAccessException if the logged in user is not a programmer for this BugReport.
	 */
	public void proposePatch(IBugReport report, String patch) throws UnauthorizedAccessException {
		if (report == null || patch == null)
			throw new IllegalArgumentException("Arguments should not be null.");
		IUser user = bugTrap.getUserManager().getLoggedInUser();
		BugReport bugReport = (BugReport) report;
		if(!bugReport.getSubsystem().getProject().isProgrammer(user))
			throw new UnauthorizedAccessException("The logged in user needs to be a programmer to propose a patch");
		
		bugReport.addPatch(patch);
	}
}