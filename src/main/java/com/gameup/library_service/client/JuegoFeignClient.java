package com.gameup.library_service.client;

import com.gameup.library_service.dto.JuegoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "juego-service", url = "${juego.service.url}")
public interface JuegoFeignClient {

    @GetMapping("/juegos/{id}")
    JuegoDTO obtenerJuegoPorId(@PathVariable("id") Long id);
}