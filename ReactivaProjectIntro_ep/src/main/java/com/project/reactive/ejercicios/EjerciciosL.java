package com.project.reactive.ejercicios;

import com.project.lista.Lista;
import com.project.memory.Memorized;
import com.project.tailCall.TailCall;
import io.reactivex.rxjava3.core.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EjerciciosL {

    //TUPLA
    record Tupla<T, U>(T t1, U t2) {
        public String toString() {
            return String.format("[%s,%s]", this.t1(), this.t2());
        }
    }

    //MONO
    static <T> Mono<T> tMono(T data) {
        return Mono.just(data);//just() agrega 1 elementos
    }

    static <T> Mono<T> tMonoSupplier() {
        return Mono.fromSupplier(() -> {
            throw new RuntimeException("Excepcion ocurrida");
        });
    }

    static <T> Mono<T> tMonoEmpty(T data) {
        return Mono.justOrEmpty(data);
    }

    //FLUX
    static <T> Flux<T> tFlux(T... elements) {
        return Flux.just(elements);//just() agrega varios elementos
    }

    static <T> Flux<T> tFluxFromStream(T... elements) {
        return Flux.fromStream(() -> Stream.of(elements));
    }

    static <T> Flux<T> tFluxFromArray(T[] elements) {
        return Flux.fromArray(elements);
    }

    //OBSERVABLE
    static <T> Observable<T> tobservable(T data) {
        return Observable.just(data); //just() agrega 1 elemento
    }

    //USO DEL LOG PARA OBTENER EL MENSAJE
    private static final Logger log = LoggerFactory.getLogger(EjemplosLT.class);

    //METODOS TAIL RECURSIVOS  TAIL CALL RECURSIVOS
    static Flux<Integer> progresionAritmetica(Integer inicio, Integer progresion, Integer cantidad, Flux<Integer> almacenar) {
        if (cantidad <= 0) {
            return almacenar;
        } else {
            return progresionAritmetica(inicio + progresion, progresion, cantidad - 1, almacenar.startWith(inicio));
        }
    }

    static TailCall<Flux<Integer>> progresionAritmeticaTC(Integer inicio, Integer progresion, Integer cantidad, Flux<Integer> almacenar) {
        if (cantidad <= 0) {
            return TailCall.ret(almacenar);
        } else {
            return TailCall.sus(() -> progresionAritmeticaTC(inicio + progresion, progresion, cantidad - 1, almacenar.startWith(inicio)));
        }
    }

    static <T> Flux<T> subLista(Lista<T> lista, Flux<T> retorno, Predicate<T> condicion) {
        if (lista.isEmpty()) {
            return retorno;
        } else {
            if (condicion.test(lista.head())) {
                return subLista(lista.tail(), retorno.startWith(lista.head()), condicion);
            } else {
                return subLista(lista.tail(), retorno, condicion);
            }
        }
    }

    static <T> TailCall<Flux<T>> subListaTC(Lista<T> lista, Flux<T> almacenar, Predicate<T> condicion) {
        if (lista.isEmpty()) {
            return TailCall.ret(almacenar);
        } else {
            if (condicion.test(lista.head())) {
                return TailCall.sus(() -> subListaTC(lista.tail(), almacenar.startWith(lista.head()), condicion));
            } else {
                return TailCall.sus(() -> subListaTC(lista.tail(), almacenar, condicion));
            }
        }
    }

    static <T> Mono<T> getElementType(Lista<T> lista, Mono<T> devolver) {
        if (lista.isEmpty()) {
            return devolver;
        } else {
            if (devolver.equals(lista.head())) {
                return getElementType(lista.tail(), devolver.thenReturn(lista.head()));
            } else {
                return getElementType(lista.tail(), devolver);
            }
        }
    }

    static <T> TailCall<Mono<T>> getElementTypeTC(Lista<T> lista, Mono<T> devolver) {
        if (lista.isEmpty()) {
            return TailCall.ret(devolver);
        } else {
            if (devolver.equals(lista.head())) {
                return TailCall.sus(() -> getElementTypeTC(lista.tail(), devolver.thenReturn(lista.head())));
            } else {
                return TailCall.sus(() -> getElementTypeTC(lista.tail(), devolver));
            }
        }
    }

    static <T> Mono<T> unicaFruta(Lista<T> lista, Mono<T> almacenar) {
        if (lista.isEmpty()) {
            return almacenar;
        } else {
            if (lista.head().equals(almacenar)) {
                return unicaFruta(lista.tail(), almacenar.thenReturn(lista.head()));
            } else {
                return unicaFruta(lista.tail(), almacenar);
            }
        }
    }

    static <T> TailCall<Mono<T>> unicaFrutaTC(Lista<T> lista, Mono<T> almacenar) {
        if (lista.isEmpty()) {
            return TailCall.ret(almacenar);
        } else {
            if (lista.head().equals(almacenar)) {
                return TailCall.sus(() -> unicaFrutaTC(lista.tail(), almacenar.thenReturn(lista.head())));
            } else {
                return TailCall.sus(() -> unicaFrutaTC(lista.tail(), almacenar));
            }
        }
    }

    static <T, U> TailCall<Flux<Tupla<T, U>>> parListasTC(Lista<T> lista1, Lista<U> lista2, Flux<Tupla<T, U>> tupla) {
        if (lista1.isEmpty() && lista2.isEmpty()) {
            return TailCall.ret(tupla);
        }
        if (lista1.tail().isEmpty() || lista2.tail().isEmpty()) {
            return TailCall.ret(tupla.startWith(new Tupla<>(lista1.head(), lista2.head())).concatWith(tupla));
        } else {
            return TailCall.sus(() -> parListasTC(lista1.tail(), lista2.tail(), tupla.startWith(new Tupla<>(lista1.head(), lista2.head()))));
        }
    }

    public static void main(String[] args) {

        /*
        Adicional Memoria
         */

        //MEMORY MONO
        Mono<Integer> elementoMono = tMono(20);

        Function<Mono<Integer>, Mono<Integer>> memoryMono = Memorized.memory(x -> x.filter(elem -> elem != null && elem >= 0));
        memoryMono.apply(elementoMono).subscribe(
                System.out::println,
                error -> log.error(error.getMessage()),
                () -> System.out.println("Memoria Mono Completada")
        );

        //MEMORY FLUX
        Flux<Integer> elementosFlux = tFlux(1, 20, 9, 18, 19);
        Function<Flux<Integer>, Flux<Integer>> memoryFlux = Memorized.memory(y -> y.filter(elem -> elem != null && elem >= 0).doOnNext(elem -> {
            if (elem == null) {
                throw new RuntimeException("En la lista hay un valor vacio.");
            }
        }));
        memoryFlux.apply(elementosFlux).subscribe(
                elements -> System.out.printf("[%s],", elements),
                error -> log.error(error.getMessage()),
                () -> System.out.println("\nMemoria Flux Completada.")
        );

        /*
        Adicional Ejercicio de Tupla
         */
        Lista<Integer> lista1 = Lista.of(2, 5, 9, 1, 3, 6, 0);
        Lista<Integer> lista2 = Lista.of(10, 13, 19, 14, 17, 11, 12);

        TailCall<Flux<Tupla<Integer, Integer>>> res4 = parListasTC(lista1.prepend(elementoMono.block()), lista2.append(elementoMono.block()), Flux.empty());

        res4.eval().subscribe(
                valor -> System.out.printf("[%s,%s]", valor.t1(), valor.t2()),
                error -> log.error(error.getMessage()),
                () -> System.out.println("\nTupla Finalizada.")
        );

        /*
        Ejemplos Mono<T>
         */

        Lista<String> nombres = Lista.of("Jose", "Marco", "Edwin", "Lopez", "Manzana", "Pedro");
        Mono<String> fruta = tMono("Manzana");
        Mono<String> res3 = unicaFruta(nombres, fruta);
        res3.doOnNext(elem -> {
            if (elem == null) {
                throw new RuntimeException("El nombre no existe o no puede estar vacio.");
            }
        }).subscribe(
                System.out::println,
                error -> log.error(error.getMessage()),
                () -> System.out.println("Completado")
        );

        TailCall<Mono<String>> res3TC = unicaFrutaTC(nombres, fruta);
        res3TC.eval().map(elem -> elem.concat(" es ").concat(nombres.head())).doOnNext(frut -> { //hay como concatenar si se especifica el tipo de dato.
            if (frut == null) {
                throw new RuntimeException("La fruta no existe o no esta en la lista.");
            }
        }).subscribe(
                System.out::println,
                error -> log.error(error.getMessage()),
                () -> System.out.println("Completado")
        );

        Lista<Integer> calificaciones = Lista.of(11, 10, 20, 14, 13, 19, 17, 15);
        Mono<Integer> elementoMax = tMono(20);
        Mono<Integer> elementoMin = tMono(11);

        Mono<Integer> res2 = getElementType(calificaciones, elementoMax);
        res2.subscribe(
                value -> System.out.print("La nota mas alta es: " + value),
                error -> log.error(error.getMessage()),
                () -> System.out.println("\nValor Finalizada.")
        );

        TailCall<Mono<Integer>> res2TC = getElementTypeTC(calificaciones, elementoMin);
        res2TC.eval().subscribe(
                x -> System.out.print("La nota mas baja es: " + x),
                error -> log.error(error.getMessage()),
                () -> System.out.println("\nValor Finalizada")
        );

        /*
        Ejemplos Flux<T>
         */

        //Metodo Recursivo
        Flux<Integer> res1 = subLista(Lista.of(2, 12, 42, 32, 10, 29, -1, 8, 29, 19, 4, 6, 8), Flux.empty(), value -> value % 2 == 0);
        res1.filter(elem -> elem > 0).doOnNext(elem -> {
            if (elem == null) {
                throw new RuntimeException("Hay un valor vacio en la lista");
            }
        }).subscribe(
                elem -> System.out.printf("[%s],", elem),
                error -> log.error(error.getMessage()),
                () -> System.out.println("\nFinalizado.")
        );

        //Metodo Tail Call Recursivo
        TailCall<Flux<Integer>> res1TC = subListaTC(
                Lista.of(29, 10, 2, 3, 4, 10, 39332, 29, 184422, 111, 15, 14, -2, -1, 10, 1000, 29299, 13111), Flux.empty(), value -> value % 3 == 0);
        res1TC.eval().filter(elem -> elem <= 100 && elem >= 0).doOnNext(value -> {
            if (value == null) {
                throw new RuntimeException("En la lista hay un valor vacio. ");
            }
        }).subscribe(
                elem -> System.out.printf("[%s],", elem),
                error -> log.error(error.getMessage()),
                () -> System.out.println("\nFinalizado.")
        );

        //Metodo Recursivo
        Flux<Integer> res = progresionAritmetica(2, 3, 10, Flux.empty());
        res.subscribe(
                valor -> System.out.printf("[%s],", valor), //evitemos el log.info para visualizar de mejor manera la progresion aritmetica
                error -> log.error(error.getMessage()),
                () -> System.out.println("\nCompletado")
        );

        //Metodo Tail Call Recursivos
        TailCall<Flux<Integer>> resTC = progresionAritmeticaTC(2, 3, 10, Flux.empty());
        resTC.eval().collect(Collectors.toList()).subscribe(
                valor -> {
                    System.out.printf("[%s],", valor);
                },
                error -> log.error(error.getMessage()),
                () -> System.out.println("\nCompletado.")
        );

        /*
        METODOS DE LA LISTA, EJERCICIOS
        Dado que usamos el block para realizar ejercicios, es mas para conocer el entorno de la programacion reactiva,
        sin embargo, no es recomendable si se trabaja en un ambiente laboral con datos delicados.
         */

//        FOLD RIGHT

        Lista<Integer> calificaciones1 = Lista.of(12, 10, 20, 18, 15, 11, 9, 5);
        Mono<Integer> almacenarMono = tMono(5);

        Flux<Lista<Integer>> fluxList1 = tFlux(calificaciones1);
        Function<Integer, Function<Integer, Integer>> fn = elem -> ls -> ls + 1;

        fluxList1.subscribe(
                ls -> {
                    System.out.println(ls.drop(almacenarMono.filter(elem -> elem != null).switchIfEmpty(Mono.just(0)).block()).foldRight(0, fn));
                },
                error -> log.error(error.getMessage()),
                () -> System.out.println("Finalizado.")
        );


//        FOLD LEFT

        Flux<Lista<Integer>> fluxList2 = tFlux(calificaciones1);
        Function<Lista<Integer>, Function<Integer, Lista<Integer>>> fn1 = ls -> ls::append;
        fluxList2.subscribe(
                ls -> {
                    ls.foldLeft(Lista.NIL, fn1).take(almacenarMono
                            .filter(elem -> elem != null)
                            .switchIfEmpty(Mono.error(new RuntimeException("El valor no puede ser vacio.")))
                            .block()).forEach(elem -> System.out.printf("[%s],", elem));
                },
                error -> log.error(error.getMessage()),
                () -> System.out.println("\nCompleto.")
        );


//        MAP

        Flux<Lista<Integer>> fluxList3 = tFlux(calificaciones1);

        fluxList3.subscribe(
                ls -> {
                    ls.map(elem -> elem * 2)
                            .filter(x -> x % 2 == 0)
                            .replace(9, almacenarMono
                                    .map(x -> x + 2)
                                    .filter(elem -> elem != null && elem.equals(ls.head()))
                                    .switchIfEmpty(Mono.error(new RuntimeException("El valor no puede ser vacio o repetido.")))
                                    .block()).forEach(elem -> System.out.printf("[%s],", elem));
                },
                error -> log.error(error.getMessage()),
                () -> new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("La ejecucion ha sido finalizado.");
                    }
                }
        );


//        REDUCING

        Flux<Lista<Integer>> fluxList4 = tFlux(calificaciones1);

        fluxList4.subscribe(
                ls -> {
                    System.out.println(ls.reducing(almacenarMono.filter(elem -> elem != null).block(), x -> y -> {
                        if (x < y) {
                            return y;
                        } else {
                            return x;
                        }
                    }));
                },
                error -> log.error(error.getMessage()),
                () -> new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("La ejecucion de tu codigo ha sido finalizado.");
                    }
                }
        );


//        FILTER

        Flux<Lista<Integer>> fluxList5 = tFlux(calificaciones1);

        fluxList5.subscribe(
                ls -> {
                    ls.filter(elem -> elem % 2 == 0)
                            .replace(13, almacenarMono.filter(elem -> elem != null)
                                    .switchIfEmpty(Mono.error(new RuntimeException("El valor no contiene en la lista")))
                                    .block()).forEach(elem -> System.out.printf("[%s],", elem));
                },
                error -> log.error(error.getMessage()),
                () -> System.out.println("Finalizado")
        );

//        CONTAINS - OPTIONAL<T>

        Flux<Lista<Integer>> fluxList6 = tFlux(calificaciones1);

        fluxList6.subscribe(
                ls -> {
                    System.out.println(ls.contains(almacenarMono.filter(elem -> elem != null)
                            .switchIfEmpty(Mono.error(new RuntimeException("El valor no puede estar vacio.")))
                            .block()).isPresent());
                },
                error -> log.error(error.getMessage()),
                () -> System.out.println("Completado.")
        );

//        REPLACE

        Flux<Lista<Integer>> fluxList7 = tFlux(calificaciones1);

        fluxList7.subscribe(
                ls -> {
                    ls.replace(9, almacenarMono.filter(elem -> elem != null)
                            .switchIfEmpty(Mono.error(new RuntimeException("El valor no puede estar vacio.")))
                            .block()).forEach(elem -> System.out.printf("[%s],", elem));
                },
                error -> log.error(error.getMessage()),
                () -> System.out.println("\nCompletado.")
        );

//        FOR EACH
        Flux<Lista<Integer>> fluxList8 = tFlux(calificaciones1);

        fluxList8.subscribe(
                ls -> {
                    ls.take(almacenarMono.filter(elem -> elem != null).switchIfEmpty(Mono.just(0)).block()).forEach(elem -> System.out.printf("[%s],", elem));
                },
                error -> log.error(error.getMessage()),
                () -> System.out.println("\nFinalizado.")
        );


//        TAKE WHILE
        Flux<Lista<Integer>> fluxList9 = tFlux(calificaciones1);

        fluxList9.subscribe(
                ls -> {
                    ls.append(almacenarMono.filter(elem -> elem != null)
                            .switchIfEmpty(Mono.just(0)).block()).takeWhile(elem -> elem % 2 == 0).forEach(x -> System.out.printf("[%s],", x));
                },
                error -> log.error(error.getMessage()),
                () -> System.out.println("\nFinalizado.")
        );

        //TAKE
        Flux<Lista<Integer>> fluxList10 = tFlux(calificaciones1);

        fluxList10.subscribe(
                ls -> {
                    ls.take(almacenarMono.filter(elem -> elem != null).switchIfEmpty(Mono.just(0)).block()).forEach(x -> System.out.printf("[%s],", x));
                },
                error -> log.error(error.getMessage()),
                () -> System.out.println("\nCompletado.")
        );


        //CONCAT
        Lista<Integer> reprobados = Lista.of(13, 9, 12, 8, 11, 14, 5, 10);
        Flux<Lista<Integer>> fluxList11 = tFlux(calificaciones1);

        fluxList11.subscribe(
                ls -> {
                    ls.concat(reprobados.filter(elem -> elem >= 11)
                                    .append(almacenarMono.filter(elem -> elem != null && elem >= 11)
                                            .switchIfEmpty(Mono.error(new RuntimeException("El valor no puede estar vacio o debe ser mayor para aprobar.")))
                                            .block()))
                            .forEach(x -> System.out.printf("[%s],", x));
                },
                error -> log.error(error.getMessage()),
                () -> System.out.println("\nFinalizado.")
        );

//        DROP WHILE

        Flux<Lista<Integer>> fluxList12 = tFlux(calificaciones1);
        fluxList12.subscribe(
                ls -> {
                    ls.append(almacenarMono.filter(elem -> elem != null)
                                    .switchIfEmpty(Mono.just(0)).block())
                            .dropWhile(elem -> elem % 2 == 0).forEach(x -> System.out.printf("[%s],", x));
                },
                error -> log.error(error.getMessage()),
                () -> System.out.println("\nFinalizado.")
        );

//        DROP

        Flux<Lista<Integer>> fluxList13 = tFlux(calificaciones1);
        fluxList13.subscribe(
                ls -> {
                    ls.drop(almacenarMono.filter(elem -> elem != null).switchIfEmpty(Mono.just(0)).block()).forEach(x -> System.out.printf("[%s],", x));
                },
                error -> log.error(error.getMessage()),
                () -> System.out.println("Completado.")
        );

//        SIZE

        Flux<Lista<Integer>> fluxList14 = tFlux(calificaciones1);
        fluxList14.subscribe(
                ls -> {
                    System.out.println(
                            ls.remove(almacenarMono
                                            .filter(elem -> elem != null && elem.equals(ls.head())).block())
                                    .invertir().size());
                },
                error -> log.info(error.getMessage()),
                () -> System.out.println("Finalizado.")
        );


//        INVERTIR

        Flux<Lista<Integer>> fluxList15 = tFlux(calificaciones1);
        fluxList15
                .subscribe(
                        ls -> {
                            ls.invertir()
                                    .remove(almacenarMono.filter(elem -> elem != null && elem.equals(ls.head()))
                                            .switchIfEmpty(Mono.error(new RuntimeException("El valor no puede estar vacio o no se encuentra en la lista")))
                                            .block()).forEach(x -> System.out.printf("[%s],", x));
                        },
                        error -> log.error(error.getMessage()),
                        () -> System.out.println("Finalizado.")
                );

//        REMOVE

        Flux<Lista<Integer>> fluxList16 = tFlux(calificaciones1);

        fluxList16.subscribe(
                ls -> {
                    ls.remove(almacenarMono
                            .filter(elem -> elem != null && elem.equals(ls.head()))
                            .switchIfEmpty(Mono.error(new RuntimeException(
                                    "El valor no puede ser vacio. || No se encuentra ese valor en la lista para remover")))
                            .block()).forEach(x -> System.out.printf("[%s],", x));
                },
                error -> log.error(error.getMessage()),
                () -> System.out.println("Finalizado")
        );

//        APPEND OR PREPEND

        Flux<Lista<Integer>> fluxList17 = tFlux(calificaciones1);
        fluxList17
                .subscribe(
                        ls -> {
                            ls.append(almacenarMono
                                    .filter(elem -> elem != null && elem != 0 && elem >= 14)
                                    .switchIfEmpty(Mono.error(new RuntimeException(
                                            "\nEl valor no puede estar vacio.\n" +
                                                    "El valor debe ser mayor a 14 para aprobar")))
                                    .block()).forEach(x -> System.out.printf("[%s],", x));
                        },
                        error -> {
                            log.error(error.getMessage());
                        },
                        () -> {
                            System.out.println("Finalizado.");
                        }
                );

    }

}
