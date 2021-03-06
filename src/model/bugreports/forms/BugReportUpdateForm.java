package model.bugreports.forms;

import model.Form;
import model.bugreports.IBugReport;
import model.bugreports.bugtag.BugTag;

public class BugReportUpdateForm implements Form{

	private IBugReport bugReport;	//The BugReport to update.
	private BugTag bugTag;			//The new BugTag.

	public BugReportUpdateForm() {
		//Explicitly setting this to null.
		this.bugReport 	= null;
		this.bugTag		= null;
	}
	
	//Getters and Setters
	
	@Override
	public void allVarsFilledIn() {
		if (getBugReport() == null) throw new NullPointerException("Bugreport is null");
		if (getBugTag() == null) throw new NullPointerException("BugTag is null");
	}

	public void setBugReport(IBugReport bugReport) {
		if (bugReport == null) throw new NullPointerException("Given BugReport is null.");
		
		this.bugReport = bugReport;
	}
	
	public IBugReport getBugReport() {
		return bugReport;
	}
	
	public void setBugTag(BugTag bugTag) {
		if (bugTag == null) throw new NullPointerException("Given BugTag is null");
		
		this.bugTag = bugTag;
	}
	
	public BugTag getBugTag() {
		return bugTag;
	}
}