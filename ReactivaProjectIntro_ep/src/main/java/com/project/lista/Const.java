package com.project.lista;

final class Const<T> implements Lista<T> {

	private final T head;
	private final Lista<T> tail;

	public Const(T head, Lista<T> tail) {
		this.head = head;
		this.tail = tail;
	}

	@Override
	public T head() {
		// TODO Auto-generated method stub
		return head;
	}

	@Override
	public Lista<T> tail() {
		// TODO Auto-generated method stub
		return tail;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toString() {
		return String.format("[%s,%s]", head, tail.toString());
	}

}
