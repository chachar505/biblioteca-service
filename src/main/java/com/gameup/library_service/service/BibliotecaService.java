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

import feign.FeignException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BibliotecaService {

    private final BibliotecaRepository bibliotecaRepository;
    private final UsuarioFeignClient usuarioFeignClient;
    private final JuegoFeignClient juegoFeignClient;

    @Transactional
    public BibliotecaResponseDTO agregarJuego(BibliotecaRequestDTO dto) {

        UsuarioDTO usuario;

        try {
            usuario = usuarioFeignClient.obtenerUsuarioPorId(dto.getIdUsuario());
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Usuario con id " + dto.getIdUsuario() + " no encontrado");
        }

        if (usuario.isCuentaBloqueada()) {
            throw new BusinessException("La cuenta del usuario está bloqueada");
        }

        JuegoDTO juego;

        try {
            juego = juegoFeignClient.obtenerJuegoPorId(dto.getIdJuego());
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Juego con id " + dto.getIdJuego() + " no encontrado");
        }

        boolean yaExiste = bibliotecaRepository.existsByIdUsuarioAndIdJuego(
                dto.getIdUsuario(), dto.getIdJuego()
        );

        if (yaExiste) {
            throw new BusinessException("El usuario ya tiene este juego en su biblioteca");
        }

        BibliotecaUsuario entrada = BibliotecaUsuario.builder()
                .idUsuario(dto.getIdUsuario())
                .idJuego(dto.getIdJuego())
                .estado(dto.getEstado() != null ? dto.getEstado() : EstadoJuego.ACTIVO)
                .build();

        BibliotecaUsuario guardada = bibliotecaRepository.save(entrada);

        return mapToResponse(guardada, usuario.getNombrePantalla(), juego.getNombreJuego());
    }

    public List<BibliotecaResponseDTO> obtenerBibliotecaPorUsuario(Long idUsuario) {

        try {
            usuarioFeignClient.obtenerUsuarioPorId(idUsuario);
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Usuario con id " + idUsuario + " no encontrado");
        }

        return bibliotecaRepository.findByIdUsuario(idUsuario)
                .stream()
                .map(entrada -> {
                    String nombreJuego = null;
                    try {
                        nombreJuego = juegoFeignClient.obtenerJuegoPorId(entrada.getIdJuego()).getNombreJuego();
                    } catch (Exception e) {
                        log.warn("No se pudo obtener el nombre del juego con id {}: {}", entrada.getIdJuego(), e.getMessage());
                    }
                    return mapToResponse(entrada, null, nombreJuego);
                })
                .toList();
    }

    public BibliotecaResponseDTO obtenerPorId(Long id) {

        BibliotecaUsuario entrada = bibliotecaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entrada con id " + id + " no encontrada"));

        return mapToResponse(entrada, null, null);
    }

    public boolean tieneJuego(Long idUsuario, Long idJuego) {
        return bibliotecaRepository.existsByIdUsuarioAndIdJuego(idUsuario, idJuego);
    }

    @Transactional
    public BibliotecaResponseDTO cambiarEstado(Long id, EstadoJuego nuevoEstado) {

        BibliotecaUsuario entrada = bibliotecaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entrada con id " + id + " no encontrada"));

        entrada.setEstado(nuevoEstado);
        BibliotecaUsuario actualizada = bibliotecaRepository.save(entrada);

        return mapToResponse(actualizada, null, null);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!bibliotecaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Entrada con id " + id + " no encontrada");
        }
        bibliotecaRepository.deleteById(id);
    }

    private BibliotecaResponseDTO mapToResponse(
            BibliotecaUsuario entity,
            String nombreUsuario,
            String nombreJuego
    ) {
        return BibliotecaResponseDTO.builder()
                .id(entity.getId())
                .idUsuario(entity.getIdUsuario())
                .idJuego(entity.getIdJuego())
                .fechaAdquisicion(entity.getFechaAdquisicion())
                .estado(entity.getEstado())
                .nombreUsuario(nombreUsuario)
                .nombreJuego(nombreJuego)
                .build();
    }
}