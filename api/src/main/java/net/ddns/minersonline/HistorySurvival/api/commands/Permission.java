package net.ddns.minersonline.HistorySurvival.api.commands;

import java.util.function.Predicate;

/**
 * A class that is used to check if a {@link CommandSender} has permission to
 *  execute a command.
 */
public class Permission implements Predicate<String> {
	private final String perm;

	/**
	 * A constructor to create the permission.
	 * @param perm A string that is the required permission node.
	 */
	public Permission(String perm) {
		this.perm = perm;
	}

	/**
	 * A function to return the permission.
	 */
	public String getPerm() {
		return perm;
	}


	@Override
	public boolean test(String s) {
		return s.equals(perm);
	}
}
