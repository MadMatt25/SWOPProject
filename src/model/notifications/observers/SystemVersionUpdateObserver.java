package model.notifications.observers;

import model.notifications.Mailbox;
import model.notifications.NotificationType;
import model.notifications.Observable;
import model.notifications.signalisations.Signalisation;

public class SystemVersionUpdateObserver extends ObserverWithMailbox {

    public SystemVersionUpdateObserver(Mailbox mailbox, Observable observes) {
        super(mailbox, observes);
    }

    @Override
    public void signal(Signalisation signalisation) {
        if (signalisation.getType() == getNotificationType()) {
            getMailbox().addNotification("BLABLABLA"); //TODO: Write something useful here
        }
    }

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.SYSTEM_VERSION_UPDATE;
    }
}
