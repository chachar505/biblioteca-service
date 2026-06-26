package com.gameup.library_service.config;

import com.gameup.library_service.model.BibliotecaUsuario;
import com.gameup.library_service.model.EstadoJuego;
import com.gameup.library_service.repository.BibliotecaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final BibliotecaRepository bibliotecaRepository;

    @Override
    public void run(String... args) {
        if (bibliotecaRepository.count() > 0) {
            log.info(">>> Biblioteca ya cargada. Se omite la inicialización.");
            return;
        }

        Faker faker = new Faker();
        EstadoJuego[] estados = EstadoJuego.values();

        for (long usuarioId = 1; usuarioId <= 5; usuarioId++) {
            for (long juegoId = 1; juegoId <= 2; juegoId++) {
                BibliotecaUsuario entrada = BibliotecaUsuario.builder()
                        .idUsuario(usuarioId)
                        .idJuego(juegoId)
                        .fechaAdquisicion(LocalDateTime.now()
                                .minusDays(faker.number().numberBetween(1, 365)))
                        .estado(estados[faker.number().numberBetween(0, estados.length)])
                        .build();
                bibliotecaRepository.save(entrada);
            }
        }

        log.info(">>> 10 entradas de biblioteca generadas con DataFaker OK.");
    }
}