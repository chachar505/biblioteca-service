package com.gameup.library_service.dto;

import com.gameup.library_service.model.EstadoJuego;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BibliotecaRequestDTO {

    @NotNull(message = "El idUsuario es obligatorio")
    private Long idUsuario;

    @NotNull(message = "El idJuego es obligatorio")
    private Long idJuego;

    private EstadoJuego estado;
}