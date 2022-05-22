package net.ddns.minersonline.HistorySurvival.api.commands;

import java.util.Objects;
import java.util.function.Predicate;

public class Permission implements Predicate {
	private String perm;

	public Permission(String perm) {
		this.perm = perm;
	}

	public String getPerm() {
		return perm;
	}

	@Override
	public boolean test(Object o) {
		if(o instanceof Permission perm){
			return Objects.equals(perm.getPerm(), this.perm);
		}
		return false;
	}
}
