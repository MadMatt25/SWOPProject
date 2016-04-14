package model.projects;

import java.util.ArrayList;
import java.util.List;

import model.notifications.Observable;
import model.notifications.observers.Observer;
import model.notifications.signalisations.Signalisation;

/**
 * This class represents a system in BugTrap.
 * A system is a project or subsystem, both can be composed of subsystems.
 */
public abstract class System implements ISystem, Observable {

	protected String name;		//System name.
	protected String description;	//System description.
	protected final System parent;		//Parent System, if any.
	protected final List<Subsystem> subsystems;	//Subsystems.
	
	protected AchievedMilestone milestone;

	protected List<Observer> observers = new ArrayList<Observer>();
	
	public System(String name, String description, System parent, List<Subsystem> subsystems, AchievedMilestone milestone) {
		this.name 			= name;
		this.description 	= description;
		this.parent 		= parent;
		this.milestone		= milestone;
		this.subsystems 	= subsystems == null ? new ArrayList<>() : subsystems;
		this.milestone 		= milestone == null ? new AchievedMilestone() : milestone;
	}

	@Override
	public void attach(Observer observer) {
		if (!this.observers.contains(observer))
			this.observers.add(observer);
	}

	@Override
	public void detach(Observer observer) {
		if (this.observers.contains(observer))
			this.observers.remove(observer);
	}
	
	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public ISystem getParent() {
		return parent;
	}
	
	@Override
	public List<ISubsystem> getSubsystems() {
		List<ISubsystem> copy = new ArrayList<>(); copy.addAll(subsystems);
		return copy;
	}
	
	public List<ISubsystem> getAllDirectOrIndirectSubsystems() {
		ArrayList<ISubsystem> subs = new ArrayList<ISubsystem>();
		for (ISubsystem s : subsystems) {
			subs.add(s);
			for (ISubsystem ss : s.getAllDirectOrIndirectSubsystems())
				subs.add(ss);
		}
		return subs;
	}

	public List<Subsystem> getAllSubsystems() {
		ArrayList<Subsystem> subs = new ArrayList<Subsystem>();
		for (Subsystem s : subsystems) {
			subs.add(s);
			for (Subsystem ss : s.getAllSubsystems())
				subs.add(ss);
		}
		return subs;
	}

	@Override
	public AchievedMilestone getAchievedMilestone() {
		return milestone;
	}

	public void declareAchievedMilestone(List<Integer> numbers) {
		AchievedMilestone highest = highestAchievedMilestone();
		AchievedMilestone achieved = new AchievedMilestone(numbers);

		if ((highest == null) || achieved.compareTo(highest) <= 0 && (this.milestone == null || achieved.compareTo(this.milestone) >= 0)) {
			milestone = achieved;
		} else {
			throw new IllegalArgumentException("The given milestone should be equal to or less than the higheset milestone of its (in)direct subsystems and the declared milestone must be larger than the current milestone.");
		}
	}

	private AchievedMilestone highestAchievedMilestone() {
		AchievedMilestone highest = null;
		for (ISubsystem s : getAllDirectOrIndirectSubsystems()) {
			if (highest == null || s.getAchievedMilestone().compareTo(highest) == 1)
				highest = s.getAchievedMilestone();
		}
		return highest;
	}

	public void signal(Signalisation signalisation) {
		if (this.parent != null)
			this.parent.signal(signalisation);

		for (Observer observer : this.observers)
			observer.signal(signalisation);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;

		if (!(o instanceof System))
			return false;

		System sys = (System)o;

		if (this.getSubsystems().size() != sys.getSubsystems().size())
			return false;

		if (!this.getName().equals(sys.getName()))
			return false;
		if (!this.getDescription().equals(sys.getDescription()))
			return false;
		if (!this.getAchievedMilestone().equals(sys.getAchievedMilestone()))
			return false;
		if ((this.getParent() == null ^ sys.getParent() == null))
			return false;
		if (this.getParent() != null && sys.getParent() != null && !this.parent.simpleEquals(sys.getParent()))
			return false;

		for (ISubsystem sub : this.getSubsystems()) {
			boolean foundEquals = false;
			for (ISubsystem sub2 : sys.getSubsystems()) {
				if (sub.equals(sub2)) {
					foundEquals = true;
					break;
				}
			}

			if (!foundEquals)
				return false;
		}

		return true;
	}

	// Simple equals only travels up and does not compare the subsystems.
	// Otherwise endless loop:
	// Subsystem compare subsystems, those compare parents, those compare subs etc...
	private boolean simpleEquals(Object o) {
		if (!(o instanceof System))
			return false;

		System sys = (System)o;

		if (this.getSubsystems().size() != sys.getSubsystems().size())
			return false;

		if (!this.getName().equals(sys.getName()))
			return false;
		if (!this.getDescription().equals(sys.getDescription()))
			return false;
		if (!this.getAchievedMilestone().equals(sys.getAchievedMilestone()))
			return false;
		if ((this.getParent() == null ^ sys.getParent() == null))
			return false;
		if (this.getParent() != null && sys.getParent() != null && !this.parent.simpleEquals(sys.getParent()))
			return false;

		return true;
	}
}