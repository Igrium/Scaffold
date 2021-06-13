package org.scaffoldeditor.nbt.util;

import java.util.Objects;

/**
 * Represents a mutable pair of objects.
 * @author Igrium
 *
 * @param <T> First object type.
 * @param <U> Second object type.
 */
public class Pair<T, U> {
	private T first;
	private U second;
	
	public Pair(T first, U second) {
		this.first = first;
		this.second = second;
	}
	
	public T getFirst() {
		return first;
	}
	public void setFirst(T first) {
		this.first = first;
	}
	
	public U getSecond() {
		return second;
	}
	public void setSecond(U second) {
		this.second = second;
	}
	
	@Override
	public String toString() {
		return "Pair("+getFirst()+", "+getSecond()+")";
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Pair)) {
			return super.equals(obj);
		}
		Pair<?, ?> other = (Pair<?, ?>) obj;
		return (getFirst().equals(other.getFirst()) && getSecond().equals(other.getSecond()));
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getFirst(), getSecond());
	}
}
