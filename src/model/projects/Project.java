package model.projects;

import java.util.Date;
import java.util.List;

import model.users.IUser;

public class Project extends System implements IProject {

	private Version version;
	private final Date creationDate;
	private final ProjectTeam projectTeam;
	
	private Date startDate;
	private double budgetEstimate;
	
	
	/**
	 * Constructor for a project
	 * @param name The name for the project
	 * @param description The description for the project
	 * @param version The version of the project
	 * @param creationDate The date the project was created
	 * @param startDate The date the project starts
	 * @param budgetEstimate The budget estimate for the project
     * @param projectTeam The team assigned to the project
     */
	public Project(String name, String description, List<Subsystem> subsystems, Version version, Date creationDate, Date startDate, double budgetEstimate, ProjectTeam projectTeam, List<AchievedMilestone> milestones) {
		super(name, description, null, subsystems, milestones);
		
		this.version		= version;
		this.creationDate	= creationDate;
		this.startDate		= startDate;
		this.budgetEstimate	= budgetEstimate;
		this.projectTeam	= projectTeam;
	}

	protected Project(Project other) {
		super(other.name, other.description, null, other.subsystems, other.milestones);
		
		this.version		= other.version;
		this.creationDate 	= new Date(other.getCreationDate().getTime());
		this.startDate	  	= new Date(other.getStartDate().getTime());
		this.projectTeam 	= new ProjectTeam(other.getTeam());
		this.budgetEstimate = other.getBudgetEstimate();
	}

	public Project copy() {
		return new Project(this);
	}
	
	@Override
	public Date getCreationDate() {
		return creationDate;
	}

	@Override
	public Date getStartDate() {
		return startDate;
	}

	@Override
	public double getBudgetEstimate() {
		return budgetEstimate;
	}

	public ProjectTeam getTeam() {
		return projectTeam;
	}

	@Override
	public IUser getLeadDeveloper() {
		return getTeam().getLeadDeveloper();
	}

	@Override
	public void setLeadDeveloper(IUser user) {
		if (!user.isDeveloper())
			throw new IllegalArgumentException("Lead developer should be a developer.");
		getTeam().setLeadDeveloper(user);
	}

	@Override
	public List<IUser> getProgrammers() {
		return getTeam().getProgrammers();
	}

	@Override
	public List<IUser> getTesters() {
		return getTeam().getTesters();
	}

	@Override
	public List<IUser> getAllDevelopers() {
		return getTeam().getAllDevelopers();
	}

	@Override
	public void addProgrammer(IUser programmer) {
		if (!programmer.isDeveloper())
			throw new IllegalArgumentException("Programmer should be a developer!");
		getTeam().addMember(programmer, Role.PROGRAMMER);
	}

	@Override
	public void addTester(IUser tester) {
		if (!tester.isDeveloper())
			throw new IllegalArgumentException("Tester should be a developer!");
		getTeam().addMember(tester, Role.TESTER);
	}
	
	@Override
	public List<Role> getRolesNotAssignedTo(IUser dev){
		return getTeam().getRolesNotAssignedTo(dev);
	}
	
	@Override
	public boolean isLead(IUser dev){
		return getTeam().isLead(dev);
	}

	@Override
	public boolean isTester(IUser dev){
		return getTeam().isTester(dev);
	}

	@Override
	public boolean isProgrammer(IUser dev){
		return getTeam().isProgrammer(dev);
	}

	@Override
	public Version getVersion() {
		return version;
	}
	
	public void setVersion(Version version) {
		this.version = version;
	}

	public void setBudgetEstimate(double budgetEstimate) {
		this.budgetEstimate = budgetEstimate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

}