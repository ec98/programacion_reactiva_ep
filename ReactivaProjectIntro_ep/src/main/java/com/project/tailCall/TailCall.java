package com.project.tailCall;

import java.util.function.Supplier;

/*
 * nodo intermedio es para calculos
 * nodo final es el resultado de la recursion
 * 
 * similar a una lista
 * TailCall es calcular datos intermedios
*/

public sealed interface TailCall<T> permits Return, Suspend {

	T eval();

	TailCall<T> resume();

	boolean isSuspend();

	static <T> TailCall<T> ret(T t) {
		return new Return<>(t);
	}

	static <T> TailCall<T> sus(Supplier<TailCall<T>> s) {
		return new Suspend<>(s);
	}

}
