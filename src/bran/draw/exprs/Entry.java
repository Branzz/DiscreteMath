package bran.draw.exprs;

import bran.mathexprs.treeparts.Variable;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public abstract class Entry {

	Set<? extends Entry> subEntries;
	// Set<Entry> superEntries;

	ExpressionInputException exception = null;

	public Entry() {
		this.subEntries = Collections.emptySet();
		// this.superEntries = Collections.emptySet();
	}

	public Entry(final Set<? extends Entry> subEntries) {
		this.subEntries = subEntries;
		// this.superEntries = Collections.emptySet();
	}

	public void setSubEntries(final Set<Entry> subEntries) {
		this.subEntries = subEntries;
	}

	public boolean except(ExpressionInputException exception) {
		if (this.exception != null)
			return false;
		this.exception = exception;
		return true;
	}

	public void unexcept() {
		exception = null;
	}

	public boolean isExcepted() {
		return exception != null;
	}

	public boolean calculated(final int i) {
		return true;
	}

	public abstract void refresh(final int i);

}
