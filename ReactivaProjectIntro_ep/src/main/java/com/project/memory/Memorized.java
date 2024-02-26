package com.project.memory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Memorized<T, U> {

	private final Map<T, U> cache = new HashMap<>();

	private Memorized() {
	}

	// retornar una funcion (cache)
	private Function<T, U> memorizarInterno(Function<T, U> fn) {
//		return x -> {
//			if (cache.containsKey(x)) {
//				return cache.get(x);
//			} else {
//				U tmp = fn.apply(x);
//				cache.put(x, tmp);
//				return tmp;
//			}
//		};
//		return x -> cache.computeIfAbsent(x, y -> fn.apply(y));
		return x -> cache.computeIfAbsent(x, fn::apply);
//		return x -> cache.containsKey(x) ? cache.get(x) : cache.put(x, fn.apply(x));
	}

//	static <T, U> Function<T, U> memory(Function<T, U> fn) {
	public static <T, U> Function<T, U> memory(Function<T, U> fn) {
		return new Memorized<T, U>().memorizarInterno(fn);
	}

}
