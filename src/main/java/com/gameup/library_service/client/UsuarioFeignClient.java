package com.gameup.library_service.client;

import com.gameup.library_service.config.FeignConfig;
import com.gameup.library_service.dto.UsuarioDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "usuario-service",
        url = "${usuario.service.url}",
        configuration = FeignConfig.class
)
public interface UsuarioFeignClient {

    @GetMapping("/api/usuarios/{id}")
    UsuarioDTO obtenerUsuarioPorId(@PathVariable Long id);
@FeignClient(name = "usuario-service", url = "${usuario.service.url}")
public interface UsuarioFeignClient {

    @GetMapping("/usuarios/{id}")
    UsuarioDTO obtenerUsuarioPorId(@PathVariable("id") Long id);
}