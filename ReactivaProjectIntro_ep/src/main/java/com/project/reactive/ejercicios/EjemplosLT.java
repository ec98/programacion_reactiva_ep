package com.project.reactive.ejercicios;

import io.reactivex.rxjava3.core.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
Authors: Franklin Condor, Edwin Piruch
 */

/*
Haciendo uso de la lista tradicional
 */
public class EjemplosLT {

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

    static <T> Flux<T> tFlux(T... elements) {
        return Flux.just(elements);//just() agrega varios elementos
    }

    static <T> Flux<T> tFluxFromStream(T... elements) {
        return Flux.fromStream(() -> Stream.of(elements));
    }

    static <T> Flux<T> tFluxFromArray(T[] elements) {
        return Flux.fromArray(elements);
    }

    static <T> Observable<T> tobservable(T data) {
        return Observable.just(data); //just() agrega 1 elemento
    }

    private static final Logger log = LoggerFactory.getLogger(EjemplosLT.class);

    public static void main(String[] args) {


        //Quinteros Ejemplos


        //Mono
        Mono<String> nombreMono = tMono("Gomez Pablo");
        Mono<String> apellidoMono = tMono("Jaramillo Eduardo");

        Mono<Usuario> usuarioMono = nombreMono.map(name -> new Usuario(name.split(" ")[1].toUpperCase(), name.split(" ")[0].toUpperCase()))
                .doOnNext(usuario -> {
                    if (usuario == null) {
                        throw new RuntimeException("El usuario no puede estar vacio.");
                    } else {
                        System.out.println(usuario.getNombre().concat(" ").concat(usuario.getApellido()));
                    }
                });
        Mono<Usuario> usuarioMono1 = apellidoMono.map(name -> new Usuario(name.split(" ")[1].toUpperCase(), name.split(" ")[0].toUpperCase()))
                .doOnNext(usuario -> {
                    if (usuario == null) {
                        throw new RuntimeException("El usuario no puede estar vacio.");
                    } else {
                        System.out.println(usuario.getNombre().concat(" ").concat(usuario.getApellido()));
                    }
                });
        usuarioMono.zipWith(usuarioMono1).subscribe(e -> log.info(e.toString()), error -> log.error(error.getMessage()), new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Completado.");
                    }
                }
        );

        //Flux
        Flux<String> nombres = tFlux("Alejandro Perez", "Maria .react", "Delgado Marco", "Fernanda Valenzuela", "Pedro .react", "Lopez Mario");
        Flux<Usuario> usuarios = nombres.map(name -> new Usuario(name.split(" ")[0].toUpperCase(), name.split(" ")[1].toUpperCase()))
                .filter(user -> !user.getApellido().equalsIgnoreCase(".react"))
                .doOnNext(usuario -> {
                    if (usuario == null) {
                        throw new RuntimeException("Los usuarios no pueden estar sin existir.");
                    } else {
                        System.out.println(usuario.getNombre().concat(" ").concat(usuario.getApellido()));
                    }
                })
                .map(usuario -> {
                    String name = usuario.getNombre().toLowerCase();
                    usuario.setNombre(name);
                    return usuario;
                });

        usuarios.subscribe(e -> log.info(e.toString()), error -> log.error(error.getMessage()), new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Se ha finalizado la ejecucion con exito.");
                    }
                }
        );


        //Cuarteros Ejemplos


        //Mono
        Mono<Integer> n1 = tMonoEmpty(134);
        Mono<Integer> n2 = tMonoEmpty(n1.block());
        Mono<Integer> n3 = tMonoEmpty(n2.block());
        Mono<Integer> n4 = tMonoEmpty(n3.block());

        Mono<Integer> res = tMono(n4.block());
        res.subscribe(System.out::println);

        //Flux
        Flux<String> stringFlux = tFluxFromArray(new String[]{"data1", "data2", "data3"});
        //stringFlux.subscribe(x -> System.out.printf("[%s],", x));
        //Otra forma de recorrer los datos, es la siguiente:
        stringFlux.map(dev -> dev.charAt(dev.length() - 1))
                .doOnNext(x -> System.out.printf("[%s]", x))
                .subscribe(); //doOnNext -> recorre cada elemento en un flujo de datos.


        //Terceros Ejemplos

        Mono<String> ret = tMonoSupplier();
        ret.subscribe(
                dato -> System.out.println("Dato recibido" + dato),
                err -> System.out.println("Error" + err),
                () -> System.out.println("Finalizacion")
        );

        Flux<Integer> ret2 = tFluxFromStream(23, 14, -190, -10, 23, 14, -40, 230, 30, -19, 18, 12, 0, -1, 2);
        ret2.filter(x -> x % 2 == 0).collect(Collectors.toList()).subscribe(
                values -> {
                    values.forEach(x -> System.out.printf("[%s],", x));
                });


        //Segundos Ejemplos

        //Mono
        Mono<String> mono = tMono("Hello world!");
        mono.subscribe(
                elem -> System.out.println(elem),// onNext -> pasa un elemento en un flujo de datos.
                err -> System.out.println(err), //onError -> devuelve un elemento erroneo en un flujo de datos.
                () -> System.out.println("Completo") //onComplete -> completa en un flujo de datos.
        );

        //Flux
        Flux<String> flux = tFlux("Pedro.", "Maria#", "Sofia", "Marcelo", "Mario.", "Edwin.");
        flux.filter(elem -> elem.contains(".")).map(dev -> dev.replace('.', ' ')).collect(Collectors.toList()).subscribe(
                elem -> System.out.println(elem) //devuelve elementos tales condiciones.
        );


        //Primeros Ejemplos

        //Mono y Flux
        //Usando de la lista tradicional
        List<Integer> elementosFromMono = new ArrayList<>();
        //Mono
        Mono<Integer> res1 = tMono(123);
        //Suscribirse al Mono
        res1.subscribe(elementosFromMono::add);
        System.out.println(elementosFromMono);

        //Usando de la lista tradicional
        List<Integer> elementosFromFlux = new ArrayList<>();
        //Flux
        Flux<Integer> res2 = tFlux(12, 134, 11, 90, 18, 172, 4, 291, 103, 13, 92, 191, 913);
        //Suscribirse al flux
        res2.subscribe(elementosFromFlux::add);
        System.out.println(elementosFromFlux);

        //Observable
        //Ejemplo 1
        Observable<String> res3 = tobservable("Hello");
        final String[] ret1 = {""};
        res3.subscribe(
                elem -> ret1[0] += elem
        );
        for (String v : ret1) {
            if (v.equals("Hello")) {
                System.out.println(v);
            } else {
                System.out.println("error");
            }
        }
        //Ejemplo 2
        String[] letras = {"a", "b", "c", "d", "e", "f", "g"};
        Observable<String[]> datos = tobservable(letras);
        final String[] r = {""};
        datos.subscribe(
                e -> r[0] += e,
                Throwable::printStackTrace,
                () -> r[0] += "_Complete"
        );
        for (String safe : r) {
            if (safe.equals("abcdefg_Complete")) {
                System.out.println(safe);
            } else {
                System.out.println("error");
            }
        }


    }
}