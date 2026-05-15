package com.gameup.library_service.controller;

import com.gameup.library_service.dto.BibliotecaRequestDTO;
import com.gameup.library_service.dto.BibliotecaResponseDTO;
import com.gameup.library_service.model.EstadoJuego;
import com.gameup.library_service.service.BibliotecaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/biblioteca")
@RequiredArgsConstructor
public class BibliotecaController {

    private final BibliotecaService bibliotecaService;

    @PostMapping
    public ResponseEntity<BibliotecaResponseDTO> agregarJuego(
            @Valid @RequestBody BibliotecaRequestDTO dto
    ) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bibliotecaService.agregarJuego(dto));
    }

    @GetMapping ("/usuario/{idUsuario}")
    public ResponseEntity<List<BibliotecaResponseDTO>> obtenerBibliotecaPorUsuario(
            @PathVariable Long idUsuario
    ) {

        return ResponseEntity.ok(
                bibliotecaService.obtenerBibliotecaPorUsuario(idUsuario)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BibliotecaResponseDTO> obtenerPorId(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(bibliotecaService.obtenerPorId(id));
    }

    @GetMapping("/usuario/{idUsuario}/juego/{idJuego}")
    public ResponseEntity<Boolean> tieneJuego(
            @PathVariable Long idUsuario,
            @PathVariable Long idJuego
    ) {

        return ResponseEntity.ok(
                bibliotecaService.tieneJuego(idUsuario, idJuego)
        );
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<BibliotecaResponseDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoJuego nuevoEstado
    ) {

        return ResponseEntity.ok(
                bibliotecaService.cambiarEstado(id, nuevoEstado)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        bibliotecaService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}