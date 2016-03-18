package model.bugreports.bugtag;

import model.bugreports.BugReport;

public class UnderReview extends BugTag {

	public UnderReview() {
		super(new BugTagEnum[]{BugTagEnum.ASSIGNED, BugTagEnum.RESOLVED, BugTagEnum.CLOSED, BugTagEnum.DUPLICATE, BugTagEnum.NOT_A_BUG});
	}

	@Override
	public BugTagEnum getBugTagEnum() {
		return BugTagEnum.UNDER_REVIEW;
	}

	@Override
	public BugReport getDuplicate() {
		return null;
	}

}
