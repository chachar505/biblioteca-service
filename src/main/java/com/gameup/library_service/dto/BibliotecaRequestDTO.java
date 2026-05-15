package com.gameup.library_service.dto;

import com.gameup.library_service.model.EstadoJuego;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BibliotecaRequestDTO {

    private Long idUsuario;
    private Long idJuego;
    private EstadoJuego estado;
}