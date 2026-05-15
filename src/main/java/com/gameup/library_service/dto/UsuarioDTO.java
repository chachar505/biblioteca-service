package com.gameup.library_service.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UsuarioDTO {

    private Long id;
    private String nombrePantalla;
    private String email;
    private BigDecimal billetera;
    private boolean cuentaBloqueada;
}