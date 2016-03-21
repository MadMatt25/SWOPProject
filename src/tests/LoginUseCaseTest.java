package tests;

import java.util.List;

import model.users.IUser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import model.BugTrap;
import model.users.Administrator;
import model.users.User;
import model.users.exceptions.NoUserWithUserNameException;


public class LoginUseCaseTest {
	
	BugTrap bugTrap;

	@Before
	public void setUp() throws Exception {
		bugTrap = new BugTrap();
		//add user
		bugTrap.getUserManager().createAdmin("", "", "", "ADMIN");
	}

	@Test
	public void loginSuccesTest() {
		//step 1
		List<IUser> list = bugTrap.getUserManager().getAdmins();
		//step 2
		IUser user = list.get(0);
		//step 3
		String message = bugTrap.getUserManager().loginAs(user);
		//step 4
		Assert.assertEquals("User: ADMIN successfully logged in.",message);
	}
	
	@Test (expected = NoUserWithUserNameException.class)
	public void loginNoUserTest() {
		//step 3, wrong input
		bugTrap.getUserManager().loginAs(null);
	}

}
