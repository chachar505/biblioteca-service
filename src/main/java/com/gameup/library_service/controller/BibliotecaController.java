package com.gameup.library_service.controller;

import com.gameup.library_service.assembler.BibliotecaModelAssembler;
import com.gameup.library_service.dto.BibliotecaRequestDTO;
import com.gameup.library_service.dto.BibliotecaResponseDTO;
import com.gameup.library_service.model.EstadoJuego;
import com.gameup.library_service.service.BibliotecaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/biblioteca")
@RequiredArgsConstructor
@Tag(name = "Biblioteca", description = "Métodos del microservicio de biblioteca de juegos")
public class BibliotecaController {

    private final BibliotecaService bibliotecaService;
    private final BibliotecaModelAssembler assembler;

    @PostMapping
    @Operation(summary = "Agregar juego a biblioteca", description = "Agrega un juego a la biblioteca de un usuario")
    public ResponseEntity<EntityModel<BibliotecaResponseDTO>> agregarJuego(
            @Valid @RequestBody BibliotecaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(assembler.toModel(bibliotecaService.agregarJuego(dto)));
    }

    @GetMapping(value = "/usuario/{idUsuario}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Obtener biblioteca de un usuario")
    public CollectionModel<EntityModel<BibliotecaResponseDTO>> obtenerBibliotecaPorUsuario(
            @Parameter(description = "ID del usuario", required = true)
            @PathVariable Long idUsuario) {
        List<EntityModel<BibliotecaResponseDTO>> lista = bibliotecaService
                .obtenerBibliotecaPorUsuario(idUsuario)
                .stream().map(assembler::toModel).collect(Collectors.toList());
        return CollectionModel.of(lista,
                Link.of("/api/biblioteca/usuario/" + idUsuario).withSelfRel());
    }

    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Obtener entrada de biblioteca por ID")
    public EntityModel<BibliotecaResponseDTO> obtenerPorId(
            @Parameter(description = "ID de la entrada", required = true)
            @PathVariable Long id) {
        return assembler.toModel(bibliotecaService.obtenerPorId(id));
    }

    @GetMapping("/usuario/{idUsuario}/juego/{idJuego}")
    @Operation(summary = "Verificar si un usuario tiene un juego")
    public ResponseEntity<Boolean> tieneJuego(
            @Parameter(description = "ID del usuario", required = true)
            @PathVariable Long idUsuario,
            @Parameter(description = "ID del juego", required = true)
            @PathVariable Long idJuego) {
        return ResponseEntity.ok(bibliotecaService.tieneJuego(idUsuario, idJuego));
    }

    @PatchMapping(value = "/{id}/estado", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Cambiar estado de un juego en la biblioteca", description = "Estados: ACTIVO, INACTIVO, REMOVIDO")
    public EntityModel<BibliotecaResponseDTO> cambiarEstado(
            @Parameter(description = "ID de la entrada", required = true)
            @PathVariable Long id,
            @Parameter(description = "Nuevo estado", required = true)
            @RequestParam EstadoJuego nuevoEstado) {
        return assembler.toModel(bibliotecaService.cambiarEstado(id, nuevoEstado));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar entrada de biblioteca")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de la entrada", required = true)
            @PathVariable Long id) {
        bibliotecaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}