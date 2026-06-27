package com.gameup.library_service.service;

import com.gameup.library_service.client.JuegoFeignClient;
import com.gameup.library_service.client.UsuarioFeignClient;
import com.gameup.library_service.dto.BibliotecaRequestDTO;
import com.gameup.library_service.dto.BibliotecaResponseDTO;
import com.gameup.library_service.dto.JuegoDTO;
import com.gameup.library_service.dto.UsuarioDTO;
import com.gameup.library_service.exception.BusinessException;
import com.gameup.library_service.exception.ResourceNotFoundException;
import com.gameup.library_service.model.BibliotecaUsuario;
import com.gameup.library_service.model.EstadoJuego;
import com.gameup.library_service.repository.BibliotecaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests del BibliotecaService")
class BibliotecaServiceTest {

    @Mock private BibliotecaRepository bibliotecaRepository;
    @Mock private UsuarioFeignClient usuarioFeignClient;
    @Mock private JuegoFeignClient juegoFeignClient;

    @InjectMocks
    private BibliotecaService bibliotecaService;

    private BibliotecaUsuario entradaMock;
    private UsuarioDTO usuarioMock;
    private JuegoDTO juegoMock;

    @BeforeEach
    void setUp() {
        entradaMock = BibliotecaUsuario.builder()
                .id(1L).idUsuario(1L).idJuego(1L)
                .fechaAdquisicion(LocalDateTime.now())
                .estado(EstadoJuego.ACTIVO).build();

        usuarioMock = new UsuarioDTO();
        usuarioMock.setId(1L);
        usuarioMock.setNombrePantalla("JuanitoGamer");
        usuarioMock.setCuentaBloqueada(false);

        juegoMock = new JuegoDTO();
        juegoMock.setIdJuego(1L);
        juegoMock.setNombreJuego("God of War");
        juegoMock.setPrecio(new BigDecimal("29.99"));
        juegoMock.setActivo(true);
    }

    @Test
    @DisplayName("Agregar juego a biblioteca exitosamente")
    void agregarJuego_exitoso() {
        BibliotecaRequestDTO dto = BibliotecaRequestDTO.builder()
                .idUsuario(1L).idJuego(1L).build();
        when(usuarioFeignClient.obtenerUsuarioPorId(1L)).thenReturn(usuarioMock);
        when(juegoFeignClient.obtenerJuegoPorId(1L)).thenReturn(juegoMock);
        when(bibliotecaRepository.existsByIdUsuarioAndIdJuego(1L, 1L)).thenReturn(false);
        when(bibliotecaRepository.save(any(BibliotecaUsuario.class))).thenReturn(entradaMock);
        BibliotecaResponseDTO resultado = bibliotecaService.agregarJuego(dto);
        assertThat(resultado).isNotNull();
        verify(bibliotecaRepository).save(any(BibliotecaUsuario.class));
    }

    @Test
    @DisplayName("Agregar juego duplicado lanza excepción")
    void agregarJuego_duplicado_lanzaExcepcion() {
        BibliotecaRequestDTO dto = BibliotecaRequestDTO.builder()
                .idUsuario(1L).idJuego(1L).build();
        when(usuarioFeignClient.obtenerUsuarioPorId(1L)).thenReturn(usuarioMock);
        when(juegoFeignClient.obtenerJuegoPorId(1L)).thenReturn(juegoMock);
        when(bibliotecaRepository.existsByIdUsuarioAndIdJuego(1L, 1L)).thenReturn(true);
        assertThatThrownBy(() -> bibliotecaService.agregarJuego(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("ya tiene este juego");
    }

    @Test
    @DisplayName("Agregar juego con cuenta bloqueada lanza excepción")
    void agregarJuego_cuentaBloqueada_lanzaExcepcion() {
        usuarioMock.setCuentaBloqueada(true);
        BibliotecaRequestDTO dto = BibliotecaRequestDTO.builder()
                .idUsuario(1L).idJuego(1L).build();
        when(usuarioFeignClient.obtenerUsuarioPorId(1L)).thenReturn(usuarioMock);
        assertThatThrownBy(() -> bibliotecaService.agregarJuego(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("bloqueada");
    }

    @Test
    @DisplayName("Obtener por ID existente retorna entrada")
    void obtenerPorId_existente_retornaEntrada() {
        when(bibliotecaRepository.findById(1L)).thenReturn(Optional.of(entradaMock));
        BibliotecaResponseDTO resultado = bibliotecaService.obtenerPorId(1L);
        assertThat(resultado.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Obtener por ID inexistente lanza excepción")
    void obtenerPorId_inexistente_lanzaExcepcion() {
        when(bibliotecaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> bibliotecaService.obtenerPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Cambiar estado exitosamente")
    void cambiarEstado_exitoso() {
        when(bibliotecaRepository.findById(1L)).thenReturn(Optional.of(entradaMock));
        when(bibliotecaRepository.save(any(BibliotecaUsuario.class))).thenReturn(entradaMock);
        BibliotecaResponseDTO resultado = bibliotecaService.cambiarEstado(1L, EstadoJuego.INACTIVO);
        assertThat(resultado).isNotNull();
        verify(bibliotecaRepository).save(any(BibliotecaUsuario.class));
    }

    @Test
    @DisplayName("Verificar si usuario tiene juego retorna true")
    void tieneJuego_retornaTrue() {
        when(bibliotecaRepository.existsByIdUsuarioAndIdJuego(1L, 1L)).thenReturn(true);
        assertThat(bibliotecaService.tieneJuego(1L, 1L)).isTrue();
    }
}