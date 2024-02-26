package com.project.lista;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public sealed interface Lista<T> permits Const, Nil {

    //	public static final
    Lista NIL = new Nil();

    T head();

    Lista<T> tail();

    boolean isEmpty();

    // creando lista y cola
    static <T> Lista<T> of(T h, Lista<T> t) {
        return new Const<T>(h, t);
    }

    // lista de elementos
    static <T> Lista<T> of(T... elemens) {
        Lista<T> ret = Lista.NIL;
        for (int i = elemens.length - 1; i >= 0; i--) {
            T h = elemens[i]; // guardamos los elementos
            var tmp = Lista.of(h, ret); // creamos el guardado de la lista
            // ret = Lista.of(h,ret);
            ret = tmp; // devuelve null
        }
        return ret; // lista
    }

    // sumaList: Sumar elementos de la lista.
    static Lista<Integer> sumaList(Lista<Integer> lista) {
        if (lista.tail().isEmpty()) {
            return Lista.of(lista.head());
        } else {
            return Lista.of(lista.head() + sumaList(lista.tail()).head());
        }
    }

    // sumaList: Funcion
    /*
     * static Function<Lista<Integer>, Integer> sumaListFn() { return lista ->
     * lista.tail().isEmpty() ? lista.head() : lista.head() +
     * sumaListFn().apply(lista.tail()); }
     */

    // ordenarList: Ordenar los elementos ascendentes
    static Lista<Integer> sorting(Lista<Integer> lista) {
        if (lista.tail().isEmpty()) {
            return Lista.of(lista.head());
        } else {
            if (lista.head() < sorting(lista.tail()).head()) {
                return Lista.of(lista.head(), sorting(lista.tail()));
            } else {
                return ordenamiento(sorting(lista.tail()), lista.head());
            }
        }
    }

    /*
     * static Function<Lista<Integer>, Lista<Integer>> sortingFn() { return lista ->
     * lista.tail().isEmpty() ? Lista.of(lista.head()) : lista.head() <
     * sortingFn().apply(lista.tail()).head() ? Lista.of(lista.head(),
     * sortingFn().apply(lista.tail())) :
     * ordenamientoFn(lista.head()).apply(lista.tail()); }
     */

    private static Lista<Integer> ordenamiento(Lista<Integer> lista, Integer elemento) {
        if (lista.isEmpty() || elemento < lista.head()) {
            return Lista.of(elemento, lista);
        } else {
            return Lista.of(lista.head(), ordenamiento(lista.tail(), elemento));
        }
    }

    /*
     * private static Function<Lista<Integer>, Lista<Integer>>
     * ordenamientoFn(Integer elemento) { return lista -> lista.isEmpty() ||
     * elemento < lista.head() ? Lista.of(elemento, lista) : Lista.of(lista.head(),
     * ordenamientoFn(elemento).apply(lista.tail())); }
     */

    // numMaxList: Obtener el numero maximo de la lista.
    static Lista<Integer> numMaxList(Lista<Integer> lista) {
        if (lista.tail().isEmpty() || lista.head() > numMaxList(lista.tail()).head()) {
            return Lista.of(lista.head());
        } else {
            return Lista.of(numMaxList(lista.tail()).head());
        }
    }

    /*
     * static Function<Lista<Integer>, Integer> numMaxListFn() { return lista ->
     * lista.tail().isEmpty() || lista.head() > numMaxListFn().apply(lista.tail()) ?
     * lista.head() : Lista.of(numMaxListFn().apply(lista.tail())).head(); }
     */

    // numMinList: Obtener el numero minimo de la lista.
    static Lista<Integer> numMinList(Lista<Integer> lista) {
        if (lista.tail().isEmpty() || lista.head() < numMinList(lista.tail()).head()) {
            return Lista.of(lista.head());
        } else {
            return Lista.of(numMinList(lista.tail()).head());
        }
    }

    /*
     * static Function<Lista<Integer>, Integer> numMinListFn() { return lista ->
     * lista.tail().isEmpty() || lista.head() < numMinListFn().apply(lista.tail()) ?
     * lista.head() : Lista.of(numMinListFn().apply(lista.tail())).head(); }
     */

    // range (inicio,fin)
    static Lista<Integer> range(Integer inicio, Integer fin) {
        if (inicio == fin) {
            return Lista.of(inicio);
        } else {
            return range(inicio + 1, fin).prepend(inicio);
        }
    }

    /*
     * static Function<Integer, Lista<Integer>> rangeFn(Integer inicio, Integer fin)
     * { return elemento -> inicio == fin ? Lista.of(inicio) : rangeFn(inicio + 1,
     * fin).apply(elemento).prepend(inicio); }
     */

    /*
     * ----------------------------------DEFAULT-------------------------------
     * Realizar Funciones a los metodos por defecto (listo)
     */

    // append: agregar el elemento al final de la lista.
    default Lista<T> append(T elem) {
        if (isEmpty()) {
            return Lista.of(elem);
        } else {
            return Lista.of(head(), tail().append(elem));
        }
    }

    /*
     * default Function<T, Lista<T>> appendFn() { if (isEmpty()) { return (T
     * elemento) -> Lista.of(elemento); } else { return (T elemento) ->
     * Lista.of(head(), tail().appendFn().apply(elemento)); } }
     */

    // prepend: agregar el elemento al inicio de la lista.
    default Lista<T> prepend(T elem) {
        return Lista.of(elem, this);
//		if (isEmpty()) {
//			return Lista.of(elem);
//		} else {
//			return Lista.of(tail().prepend(elem).head(), tail());
//		}
    }

    /*
     * default Function<T, Lista<T>> prependFn() { return (T elemento) ->
     * Lista.of(elemento, this); }
     */

    // remove: elimina el elemento de la lista
    default Lista<T> remove(T elem) {
        if (isEmpty()) {
            return NIL;
        } else {
            if (head().equals(elem)) {
                return tail();
            } else {
                return Lista.of(head(), tail().remove(elem));
            }
        }
    }

//	default TailCall<Lista<T>> removeTC(T elem, Lista<T> acc) {
//		if (acc.isEmpty()) {
//			return TailCall.ret(acc);
//		} else {
//			if (elem == acc.head()) {
//				return TailCall.sus(() -> removeTC(elem, acc.tail()));
//			} else {
//				return TailCall.sus(() -> removeTC(acc.head(), acc.tail().remove(elem)));
//			}
//		}
//	}

    /*
     * default Function<T, Lista<T>> removeFn() { return elemento -> isEmpty() ? NIL
     * : head().equals(elemento) ? tail() : Lista.of(head(),
     * tail().removeFn().apply(elemento)); }
     */

    // invertir: invierte la lista completa
    default Lista<T> invertir() {
        return this.foldLeft(Lista.NIL, ls -> t -> ls.prepend(t));
//		return this.foldRight(Lista.NIL, t -> ls -> ls.append(t));
//		if (isEmpty()) {
//			return NIL;
//		} else {
//			return tail().invertir().append(head());
//		}
    }

    /*
     * default Function<Lista<T>, Lista<T>> invertirFn() { if (isEmpty()) { return
     * elemento -> NIL; } else { return elemento ->
     * tail().invertir().append(elemento.head()); } }
     */

    // size: el tamanio de la lista
    default Integer size() {
        return this.foldLeft(0, elem -> t -> elem + 1);
//		return this.foldLeft(0, t -> elem -> elem + 1);
//		if (isEmpty()) {
//			return 0;
//		} else {
//			return tail().size() + 1;
//		}
    }

    // get: devuelve el indice de la lista
    default T get(int indice) {
        if (indice == 0) {
            return this.head();
        } else {
            return this.tail().get(indice - 1);
        }
    }

    /*
     * default Function<Integer, Integer> sizeFn() { if (isEmpty()) { return
     * elemento -> 0; } else { return elemento -> tail().sizeFn().apply(elemento) +
     * 1; } }
     */

    // drop: Exam: drop(2) -> elimina dos elementos al principio de la lista.
    default Lista<T> drop(int n) {
        if (isEmpty() || n <= 0) {
            return this;
        }
        return tail().drop(n - 1);
//		if (isEmpty()) {
//			return NIL;
//		} else {
//			if (n <= 0) {
//				return this; // Lista.of(head(),tail());
//			} else {
//				return tail().drop(n - 1);
//			}
//		}
    }

    /*
     * default Function<Integer, Lista<T>> dropFn() { return elemento -> isEmpty()
     * || elemento <= 0 ? this : tail().dropFn().apply(elemento - 1); }
     */

    // dropWhile: Exam: dropWhile(2) -> elimina elementos mediante de una condicion.
    default Lista<T> dropWhile(Predicate<T> p) {
        return isEmpty() || !p.test(head()) ? this : tail().dropWhile(p);
//		if (isEmpty() || !p.test(head())) {
//			return this;
//		}
//		return tail().dropWhile(p);
    }

    /*
     * default Function<T, Lista<T>> dropWhileFn(Predicate<T> p) { return elemento
     * -> isEmpty() || !p.test(elemento) ? this : tail().dropWhile(p); }
     */

    // concat: Concatena una lista con otra.
    default Lista<T> concat(Lista<T> lista) {
//		return isEmpty() ? lista : Lista.of(head(), tail()).concat(lista);
        if (isEmpty()) {
            return lista;
        } else {
            return Lista.of(head(), tail().concat(lista));
        }
    }

    /*
     * default Function<Lista<T>, Lista<T>> concatFn() { return listas -> isEmpty()
     * ? listas : Lista.of(head(), tail().concatFn().apply(listas)); }
     */

    // take: Exam: take(2)-> toma dos elementos primeros de la lista.
    default Lista<T> take(int n) {
//		return isEmpty() || n <= 0 ? NIL : Lista.of(head(), tail()).take(n - 1);
        if (isEmpty() || n <= 0) {
            return NIL;
        }
        return Lista.of(head(), tail().take(n - 1));
    }

    /*
     * default Function<Integer, Lista<T>> takeFn() { return n -> isEmpty() || n <=
     * 0 ? NIL : Lista.of(head(), tail().takeFn().apply(n - 1)); }
     */

    // takeWhile: toma elementos mediante una condicion.
    default Lista<T> takeWhile(Predicate<T> p) {
//		return isEmpty() || !p.test(head()) ? NIL : Lista.of(head(), tail()).takeWhile(p);
        if (isEmpty() || !p.test(head())) {
            return NIL;
        }
        return Lista.of(head(), tail().takeWhile(p));
    }

    /*
     * default Function<T, Lista<T>> takeWhileFn(Predicate<T> p) { return n ->
     * isEmpty() || !p.test(head()) ? NIL : Lista.of(head(),
     * tail().takeWhileFn(p).apply(n)); }
     */

    default void forEach(Consumer<T> fn) {
        if (!isEmpty()) {
            fn.accept(head());
            tail().forEach(fn);
        }
    }

    default Lista<T> replace(T elem, T newElem) {
        if (isEmpty()) {
            return Lista.NIL;
        }
        return head().equals(elem) ? Lista.of(newElem, tail()) : Lista.of(head(), tail().replace(elem, newElem));
//			if (head().equals(elem)) {
//				return Lista.of(newElem, tail());
//			} else {
//				return Lista.of(head(), tail().replace(elem, newElem));
//			}
//		}
    }

    default Optional<T> contains(T elem) {
        if (isEmpty()) {
            return Optional.empty();
        }
        return head().equals(elem) ? Optional.of(head()) : tail().contains(elem);
//			if (head().equals(elem)) {
//				return Optional.of(head()); //retorna el mismo
//			} else {
//				return tail().contains(elem); //sigue buscando por la cola
//			}
//		}
    }

    // filter: Cumple una condicion dada
    default Lista<T> filter(Predicate<T> p) {
        return isEmpty() ? NIL : p.test(head()) ? Lista.of(head(), tail().filter(p)) : tail().filter(p);
//		if (isEmpty()) {
//			return NIL;
//		} else {
//			if (p.test(head())) {
//				return Lista.of(head(), tail().filter(p));
//			} else {
//				return tail().filter(p);
//			}
//		}
    }

//	static <T, U> Lista<U> mapv2(Lista<T> lista, Function<T, U> fn) {
//		if (lista.isEmpty()) {
//			return Lista.NIL;
//		} else {
//			return Lista.of(fn.apply(lista.head()), mapv2(lista.tail(), fn));
////			return mapv2(lista.tail(), fn).prepend(fn.apply(lista.head()));
//		}
//	}

    // reducing iterativo
    default T reducing(T identidad, Function<T, Function<T, T>> fn) {
        T acum = identidad;
        var tmp = this;
        while (!tmp.isEmpty()) {
            acum = fn.apply(tmp.head()).apply(acum);
            tmp = tmp.tail();
        }
        return acum;
    }

    // invertir iterativo
    default Lista<T> invertirv2() {
        var tmp = this;

        Lista<T> retTmp = Lista.NIL;
        while (!tmp.isEmpty()) {
            retTmp = Lista.of(tmp.head(), retTmp);
            tmp = tmp.tail();
        }
        return retTmp;
    }

    // iterativo
    default <U> Lista<U> mapIte(Function<T, U> fn) {
        var tmp = this;

        Lista<U> retTmp = Lista.NIL;

        while (!tmp.isEmpty()) {
            retTmp = Lista.of(fn.apply(tmp.head()), retTmp);
            tmp = tmp.tail();
        }
        return retTmp.invertirv2();
    }

    // Correcursion
    static <T, U> List<U> mapIt(List<T> lista, Function<T, U> fn) {
        List<U> tmp = new ArrayList<>();

        for (T elem : lista) {
            tmp.add(fn.apply(elem));
        }
        return tmp;
    }

    // recursivo
    default <U> Lista<U> map(Function<T, U> fn) {
        if (isEmpty()) {
            return Lista.NIL;
        } else {
//				return Lista.of(fn.apply(head()), tail().map(fn));
            return tail().map(fn).prepend(fn.apply(head()));
        }
    }

    // co-recursivo
    default <U> U foldLeft(U identidad, Function<U, Function<T, U>> fn) {
        U acc = identidad;
        var tmp = this;

        while (!tmp.isEmpty()) {
            acc = fn.apply(acc).apply(tmp.head());
            tmp = tmp.tail();
        }
        return acc;
    }

    /*
     * default <U> TailCall<U> foldLeftTC(Lista<T> ls, U identidad, Function<U,
     * Function<T, U>> fn) { if (ls.isEmpty()) { return TailCall.ret(identidad); }
     * else { return TailCall.sus(() -> foldLeftTC(ls.tail(),
     * fn.apply(identidad).apply(ls.head()), fn)); } }
     */

    // recursivo
    default <U> U foldRight(U identidad, Function<T, Function<U, U>> fn) {
        if (this.isEmpty()) {
            return identidad;
        } else {
//			T elem = this.head();
//			U tmp = this.tail().foldRight(identidad, fn);
            return fn.apply(this.head()).apply(this.tail().foldRight(identidad, fn));
        }
    }

    /*
     * default <U> TailCall<U> foldRightTC(U identidad, Function<T, Function<U, U>>
     * fn) { if (this.isEmpty()) { return TailCall.ret(identidad); } else { return
     * TailCall.sus(() -> foldRightTC(fn.apply(this.head()).apply(identidad), fn));
     * } }
     */
}
