package com.gameup.library_service.dto;

import com.gameup.library_service.model.EstadoJuego;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BibliotecaResponseDTO {

    private Long id;
    private Long idUsuario;
    private Long idJuego;
    private LocalDateTime fechaAdquisicion;
    private EstadoJuego estado;
    private String nombreUsuario;
    private String nombreJuego;
}