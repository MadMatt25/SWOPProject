package model.users;

public class Issuer extends User {

	public Issuer(String firstName, String middleName, String lastName, String userName) {
		super(firstName, middleName, lastName, userName);
	}

	@Override
	protected UserCategory getCategory() {
		return UserCategory.ISSUER;
	}
}
