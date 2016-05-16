package model.notifications;

import model.bugreports.bugtag.BugTag;
import model.notifications.observers.BugReportChangeObserver;
import model.notifications.observers.BugReportSpecificTagObserver;
import model.notifications.observers.CreateBugReportObserver;
import model.notifications.observers.CreateCommentObserver;
import model.notifications.observers.ObserverWithMailbox;

public enum NotificationType {
	BUGREPORT_CHANGE {	//A Change in a BugReport.
		@Override
		public ObserverWithMailbox createObserver(Mailbox box, Observable observable, BugTag tag) {
			return new BugReportChangeObserver(box, observable);
		}
	},
	BUGREPORT_SPECIFIC_TAG { //A change in a BugReport to specific Tag.
		@Override
		public ObserverWithMailbox createObserver(Mailbox box, Observable observable, BugTag tag) {
			return new BugReportSpecificTagObserver(box, observable, tag);
		}
	},
	CREATE_COMMENT { //A B
		@Override
		public ObserverWithMailbox createObserver(Mailbox box, Observable observable, BugTag tag) {
			return new CreateCommentObserver(box, observable);
		}
	},
	CREATE_BUGREPORT {
		@Override
		public ObserverWithMailbox createObserver(Mailbox box, Observable observable, BugTag tag) {
			return new CreateBugReportObserver(box, observable);
		}
	};

	public abstract ObserverWithMailbox createObserver(Mailbox box, Observable observable, BugTag tag);
}