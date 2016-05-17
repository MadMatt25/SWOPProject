package tests.bugreporttests;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import controllers.exceptions.UnauthorizedAccessException;
import model.bugreports.BugReport;
import model.bugreports.IBugReport;
import model.bugreports.Patch;
import model.bugreports.TargetMilestone;
import model.bugreports.bugtag.BugTag;
import model.bugreports.comments.Comment;
import model.notifications.observers.Observer;
import model.projects.ISubsystem;
import model.users.IUser;
import model.users.Issuer;
import tests.BugTrapTest;

public class BugReportTest extends BugTrapTest {

	private BugReport bugReport;
	
	private String title = "BugReport";
	private String description = "Awesome BugReport";
	private ISubsystem subsystem;
	private List<IBugReport> dependsOn = new ArrayList<>();
	private List<IUser> assignees = new ArrayList<>();
	private List<Comment> comments = new ArrayList<>();
	private List<Observer> observers = new ArrayList<>();
	private IUser issuedBy = new Issuer(null, null, null, null);
	private Date creationDate = new Date();
	private BugTag bugTag = BugTag.NEW;
	private String stackTrace = "stack";
	private String errorMessage = "error";
	private String reproduction = "reproduction";
	private TargetMilestone targetMilestone = new TargetMilestone();
	private List<model.bugreports.Test> tests = new ArrayList<model.bugreports.Test>();
	private List<Patch> patches = new ArrayList<Patch>();
	private int impactFactor = 3;
	
	
	@Before
	public void setUp() {
		super.setUp();
		subsystem = excel;
		bugReport = new BugReport(bugTrap, title, description, subsystem, dependsOn, assignees, comments, issuedBy, creationDate, observers, bugTag, stackTrace, errorMessage, reproduction, targetMilestone, tests, patches, impactFactor);
	}

	@Test
	public void constructorTest() {
		assertTrue(bugReport.getTitle().equals(title));
		assertTrue(bugReport.getDescription().equals(description));
		assertEquals(bugReport.getSubsystem(), subsystem);
		assertEquals(bugReport.getDependsOn(), dependsOn);
		assertEquals(bugReport.getComments(), comments);
		assertEquals(bugReport.getIssuedBy(), issuedBy);
		assertEquals(bugReport.getCreationDate(), creationDate);
		assertEquals(bugReport.getBugTag(), bugTag);
		assertEquals(bugReport.getErrorMessage(), errorMessage);
		assertEquals(bugReport.getReproduction(), reproduction);
		assertEquals(bugReport.getTargetMilestone(), targetMilestone);
		assertEquals(bugReport.getStackTrace(), stackTrace);
	}
	
	@Test
	public void addCommentTest() {
		assertEquals(0, bugReport.getComments().size());
		bugReport.addComment("foobar");
		assertEquals(1, bugReport.getComments().size());
		assertTrue(bugReport.getComments().get(0).getText().equals("foobar"));
	}
	
	@Test
	public void assignDeveloperTest() {
		bugTrap.getUserManager().loginAs(lead);
		try { bugReport.assignDeveloper(admin); fail(); } catch (IllegalArgumentException e) {} catch (UnauthorizedAccessException e) { fail(); }
		try { bugReport.assignDeveloper(issuer); fail(); } catch (IllegalArgumentException e) {} catch (UnauthorizedAccessException e) { fail(); }
		
		assertEquals(0, bugReport.getAssignees().size());
		try {
			bugReport.assignDeveloper(prog);
		} catch (UnauthorizedAccessException e) {
			fail();
		}
		assertEquals(1, bugReport.getAssignees().size());
		assertEquals(bugReport.getAssignees().get(0), prog);
	}
	
	@Test
	public void updateBugTagTest() {
		BugTag assignedTag 		= BugTag.ASSIGNED;
		BugTag closedTag 		= BugTag.CLOSED;
		BugTag duplicateTag 	= BugTag.DUPLICATE;
		BugTag newTag 			= BugTag.NEW;
		BugTag notABugTag 		= BugTag.NOTABUG;
		BugTag resolvedTag 		= BugTag.RESOLVED;
		BugTag underReviewTag 	= BugTag.UNDERREVIEW;

		bugTrap.getUserManager().loginAs(lead);

		//From New to New is allowed.
		try {
			bugReport.updateBugTag(newTag);
		} catch (UnauthorizedAccessException e) {
			fail();
		}
		//From New to InProgress is allowed.
		try {
			bugReport.updateBugTag(underReviewTag);
		} catch (UnauthorizedAccessException e) {
			fail();
		}
		//Walking around in InProgress is allowed.
		try {
			bugReport.updateBugTag(assignedTag);
		} catch (UnauthorizedAccessException e) {
			fail();
		}
		try {
			bugReport.updateBugTag(underReviewTag);
		} catch (UnauthorizedAccessException e) {
			fail();
		}

		//Going back to New is not allowed.
		try { bugReport.updateBugTag(newTag); fail(); } catch (IllegalStateException e) { } catch (UnauthorizedAccessException e) { }
		
		//From InProgress to Closed is allowed.
		try {
			bugReport.updateBugTag(duplicateTag);
		} catch (UnauthorizedAccessException e) {
			fail();
		}
		//Walking around in Closed is not allowed.
		try { bugReport.updateBugTag(resolvedTag); fail(); } catch (IllegalStateException e) { } catch (UnauthorizedAccessException e) { }
		try { bugReport.updateBugTag(closedTag); fail(); } catch (IllegalStateException e) { } catch (UnauthorizedAccessException e) { }
		try { bugReport.updateBugTag(notABugTag); fail(); } catch (IllegalStateException e) { } catch (UnauthorizedAccessException e) { }
		
		//Going back to previous states is not allowed.
		try { bugReport.updateBugTag(newTag); fail(); } catch (IllegalStateException e) { } catch (UnauthorizedAccessException e) { }
		try { bugReport.updateBugTag(underReviewTag); fail(); } catch (IllegalStateException e) { } catch (UnauthorizedAccessException e) { }
		try { bugReport.updateBugTag(assignedTag); fail(); } catch (IllegalStateException e) { } catch (UnauthorizedAccessException e) { }
	}

	@Test
	public void compareTest() {
		assertEquals(-1, clippyBug.compareTo(wordBug));
		assertEquals(1, wordArtBug.compareTo(clippyBug));

		assertEquals(0, bugReport.compareTo(bugReport));
		assertEquals(0, bugReport.compareTo(bugReport));

		assertEquals(-1, bugReport.compareTo(clippyBug));
		assertEquals(1, clippyBug.compareTo(bugReport));
	}
}
