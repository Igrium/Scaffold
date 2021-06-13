package org.scaffoldeditor.nbt.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

public class SingleTypePair<E> extends Pair<E, E> implements Collection<E> {

	public SingleTypePair(E first, E second) {
		super(first, second);
	}

	@Override
	public int size() {
		return 2;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean contains(Object o) {
		return getFirst().equals(o) || getSecond().equals(o);
	}

	@Override
	public Object[] toArray() {
		return new Object[] { getFirst(), getSecond() };
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		return (T[]) Arrays.copyOf(toArray(), 2, a.getClass());
	}

	@Override
	public boolean add(E e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object obj : c) {
			if (!contains(obj)) return false;
		}
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
	}
	
	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			
			int index = -1;

			@Override
			public boolean hasNext() {
				return index < 1;
			}

			@Override
			public E next() {
				index++;
				return index == 0 ? getFirst() : getSecond();
			}
		};
	}
	
	public static class Collector<E> implements java.util.stream.Collector<E, Object, SingleTypePair<E>> {

		@Override
		public Supplier<Object> supplier() {
			return null;
		}

		@Override
		public BiConsumer<Object, E> accumulator() {
			return null;
		}

		@Override
		public BinaryOperator<Object> combiner() {
			return null;
		}

		@Override
		public Function<Object, SingleTypePair<E>> finisher() {
			return null;
		}

		@Override
		public Set<Characteristics> characteristics() {
			return null;
		}
		
	}
}
