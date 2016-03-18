package model.bugreports.bugtag;

/**
 * This class represents the Resolved bug tag.
 * This means that a BugReport has been fixed.
 */
public class Resolved extends BugTag {

	public Resolved() {
		super(new BugTagEnum[]{BugTagEnum.CLOSED, BugTagEnum.NOT_A_BUG, BugTagEnum.DUPLICATE});
	}

	@Override
	public BugTagEnum getBugTagEnum() {
		return BugTagEnum.RESOLVED;
	}

}
