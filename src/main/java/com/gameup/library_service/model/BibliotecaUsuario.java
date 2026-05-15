package com.gameup.library_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "biblioteca_usuario",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_usuario", "id_juego"})
)
public class BibliotecaUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Column(name = "id_juego", nullable = false)
    private Long idJuego;

    @Column(name = "fecha_adquisicion", nullable = false)
    private LocalDateTime fechaAdquisicion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoJuego estado;

    @PrePersist
    protected void onCreate() {
        if (this.fechaAdquisicion == null) {
            this.fechaAdquisicion = LocalDateTime.now();
        }

        if (this.estado == null) {
            this.estado = EstadoJuego.ACTIVO;
        }
    }
}