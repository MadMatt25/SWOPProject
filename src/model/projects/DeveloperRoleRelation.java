package model.projects;

import model.users.IUser;

/**
 * This class describes a relation between users and their role
 * in a certain project. One user can have multiple roles, and thus
 * one user can occur in multiple UserRoleRelation objects.
 */
public class DeveloperRoleRelation {
	
	private IUser user;	//User
	private Role role;	//Role
	
	/**
	 * Constructor.
	 * @param user User to assign a Role to.
	 * @param role The Role to assign to given User.
	 */
	public DeveloperRoleRelation(IUser user, Role role) {
		if (!user.isDeveloper())
			throw new IllegalArgumentException("User should be a developer!");

		this.user = user;
		this.role = role;
	}

	/**
	 * Copy constructor.
	 * @param developerRoleRelation The DeveloperRoleRelation to copy.
     */
	DeveloperRoleRelation(DeveloperRoleRelation developerRoleRelation) {
		this.user = developerRoleRelation.getUser();
		this.role = developerRoleRelation.getRole();
	}
	
	/**
	 * Get the user involved in this relation.
	 * @return The user involved in this relation.
	 */
	public IUser getUser() {
		return user;
	}
	
	/**
	 * Get the role involved in this relation.
	 * @return The role involved in this relation.
	 */
	public Role getRole() {
		return role;
	}
	
	/**
	 * Set the role involved in this relation
	 * @param role The new role for this relation
	 */
	public void setRole(Role role) {
		this.role = role; 
	}

	/**
	 * Terminates this developer relation
	 */
	public void terminate() {
		user = null;
		role = null;
	}
}
