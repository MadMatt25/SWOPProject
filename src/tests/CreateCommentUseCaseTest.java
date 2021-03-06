package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import controllers.exceptions.UnauthorizedAccessException;
import model.bugreports.IBugReport;
import model.bugreports.comments.Comment;
import model.bugreports.forms.CommentCreationForm;

public class CreateCommentUseCaseTest extends BugTrapTest{

	@Test
	public void createCommentOnBugReportTest() {
		//Log in.
		userController.loginAs(issuer);
				
		//1. The issuer indicates he wants to create a comment
		CommentCreationForm form = null;
		try {
			form = bugReportController.getCommentCreationForm();
		} catch (UnauthorizedAccessException e1) { fail("not authorized"); }
		
		//2. Include use case Select Bug Report.
		List<IBugReport> list = null;
		list = bugReportController.getBugReportList();

		//3. The system shows a list of all comments of the selected bug report.
		list.get(0).getComments();
		
		//4. The issuer indicates if he wants to comment directly on the bug report or on some other comment.
		form.setCommentable(list.get(0));
		
		//5. The system asks for the text of the comment.
		//6. The issuer writes his comment.
		form.setText("No! Clippy will become annoying :o");
		
		//7. The system adds the comment to the selected use case.
		try {
			bugReportController.createComment(form);
		} catch (UnauthorizedAccessException e) {
			fail(e.getMessage());
		}

		//Confirm.
		assertEquals(2, list.get(0).getComments().size());
		assertTrue(list.get(0).getComments().get(1).getText().equals("No! Clippy will become annoying :o"));
	}
	
	@Test
	public void createCommentOnCommentTest() {
		//Log in.
		userController.loginAs(prog);
				
		//1. The issuer indicates he wants to create a comment.
		CommentCreationForm form = null;
		try {
			form = bugReportController.getCommentCreationForm();
		} catch (UnauthorizedAccessException e1) { fail("not authorized"); }
		
		//2. Include use case Select Bug Report.
		List<IBugReport> list = bugReportController.getBugReportList();
		
		//3. The system shows a list of all comments of the selected bug report.
		List<Comment> comments = list.get(0).getComments();
		
		//4. The issuer indicates if he wants to comment directly on the bug report or on some other comment.
		form.setCommentable(comments.get(0));
		
		//5. The system asks for the text of the comment.
		//6. The issuer writes his comment.
		form.setText("Aren't you exaggerating?");
		
		//7. The system adds the comment to the selected use case.
		try {
			bugReportController.createComment(form);
		} catch (UnauthorizedAccessException e) {
			fail(e.getMessage());
		}

		//Confirm.
		assertEquals(1, comments.get(0).getComments().size());
		assertTrue(comments.get(0).getComments().get(0).getText().equals("Aren't you exaggerating?"));
	}
	
	@Test
	public void notAuthorizedTest() {
		try {
			bugReportController.getCommentCreationForm();
			fail("should throw exception");
		} catch (UnauthorizedAccessException e) { }
	}
	
	@Test (expected = NullPointerException.class)
	public void varsNotFilledTest() throws UnauthorizedAccessException {
		//Log in.
		userController.loginAs(issuer);

		CommentCreationForm form = bugReportController.getCommentCreationForm();
		bugReportController.createComment(form);
	}
	
	@Test
	public void nullFormTest() {
		//Log in.
		userController.loginAs(issuer);
		
		try {
			bugReportController.createComment(null);
			fail("should throw exception");
		}
		catch (IllegalArgumentException e) { }
		catch (UnauthorizedAccessException e) {
			fail("not authorized");
		}
	}
}
