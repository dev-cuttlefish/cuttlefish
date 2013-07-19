package ch.ethz.sg.cuttlefish.misc;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

public final class Pair<T> implements Collection<T>, Serializable {

	private static final long serialVersionUID = 8010058259564231599L;

	private T first;
	private T second;

	public Pair(T first, T second) {
		this.first = first;
		this.second = second;
	}

	public Pair(Collection<? extends T> values) {
		if (values == null)
			throw new IllegalArgumentException(
					"Input collection cannot be null");

		if (values.size() == 2) {
			if (values.contains(null))
				throw new IllegalArgumentException(
						"Pair cannot contain null values");
			Iterator<? extends T> iter = values.iterator();
			first = iter.next();
			second = iter.next();
		} else
			throw new IllegalArgumentException(
					"Pair may only be created from a Collection of exactly 2 elements");
	}

	public T getFirst() {
		return first;
	}

	public T getSecond() {
		return second;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public Iterator<T> iterator() {
		return null;
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException("Pairs cannot be mutated");
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException("Pairs cannot be mutated");
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException("Pairs cannot be mutated");
	}

	@Override
	public int size() {
		return 2;
	}

	@Override
	public Object[] toArray() {
		Object[] to_return = new Object[2];
		to_return[0] = first;
		to_return[1] = second;
		return to_return;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <S> S[] toArray(S[] a) {
		S[] to_return = a;
		Class<?> type = a.getClass().getComponentType();
		if (a.length < 2)
			to_return = (S[]) java.lang.reflect.Array.newInstance(type, 2);
		to_return[0] = (S) first;
		to_return[1] = (S) second;

		if (to_return.length > 2)
			to_return[2] = null;
		return to_return;
	}

	public boolean add(T o) {
		throw new UnsupportedOperationException("Pairs cannot be mutated");
	}

	public boolean addAll(Collection<? extends T> c) {
		throw new UnsupportedOperationException("Pairs cannot be mutated");
	}

	public void clear() {
		throw new UnsupportedOperationException("Pairs cannot be mutated");
	}

	public boolean contains(Object o) {
		return (first == o || first.equals(o) || second == o || second
				.equals(o));
	}

	public boolean containsAll(Collection<?> c) {
		if (c.size() > 2)
			return false;
		Iterator<?> iter = c.iterator();
		Object c_first = iter.next();
		Object c_second = iter.next();
		return this.contains(c_first) && this.contains(c_second);
	}

	@SuppressWarnings("rawtypes")
	public boolean equals(Object o) {
		if (o == this)
			return true;

		if (o instanceof Pair) {
			Pair otherPair = (Pair) o;
			Object otherFirst = otherPair.getFirst();
			Object otherSecond = otherPair.getSecond();
			return (this.first == otherFirst || (this.first != null && this.first
					.equals(otherFirst)))
					&& (this.second == otherSecond || (this.second != null && this.second
							.equals(otherSecond)));
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "<" + first.toString() + ", " + second.toString() + ">";
	}

}
