package model.bugreports.bugtag;

import model.bugreports.BugReport;

/**
 * This class represents the Assigned bug tag.
 * This means that a BugReport has been assigned to a
 * developer to be resolved.
 */
public class Assigned extends InProgress {

    public Assigned(BugReport bugReport) {
		super(bugReport);
	}

	@Override
    public BugTag getTag() {
        return BugTag.ASSIGNED;
    }

	@Override
	public boolean canRevert() {
		return true;
	}
	
	@Override
	public boolean canAddTests() {
		return true;
	}
	
	@Override
	public boolean canAddPatches() {
		return true;
	}
	
	@Override
	public double getMultiplier() {
		return 2;
	}
}