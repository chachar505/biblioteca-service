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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BibliotecaService {

    private final BibliotecaRepository bibliotecaRepository;
    private final UsuarioFeignClient usuarioFeignClient;
    private final JuegoFeignClient juegoFeignClient;

    public BibliotecaResponseDTO agregarJuego(BibliotecaRequestDTO dto) {

        UsuarioDTO usuario;

        try {
            usuario = usuarioFeignClient.obtenerUsuarioPorId(dto.getIdUsuario());

        } catch (FeignException.NotFound e) {

            throw new ResourceNotFoundException(
                    "Usuario con id " + dto.getIdUsuario() + " no encontrado"
            );
        }

        if (usuario.isCuentaBloqueada()) {

            throw new BusinessException(
                    "La cuenta del usuario está bloqueada"
            );
        }

        JuegoDTO juego;

        try {
            juego = juegoFeignClient.obtenerJuegoPorId(dto.getIdJuego());

        } catch (FeignException.NotFound e) {

            throw new ResourceNotFoundException("Juego con id " + dto.getIdJuego() + " no encontrado");
        }

        boolean existe = bibliotecaRepository.existsByIdUsuarioAndIdJuego(dto.getIdUsuario(), dto.getIdJuego()
        );

        if (existe) {

            throw new BusinessException(
                    "El usuario ya tiene este juego en su biblioteca"
            );
        }

        BibliotecaUsuario bibliotecaUsuario = BibliotecaUsuario.builder()
                .idUsuario(dto.getIdUsuario())
                .idJuego(dto.getIdJuego())
                .estado(
                        dto.getEstado() != null
                                ? dto.getEstado()
                                : EstadoJuego.ACTIVO
                )
                .build();

        BibliotecaUsuario guardado =
                bibliotecaRepository.save(bibliotecaUsuario);

        return mapToResponse(
                guardado,
                usuario.getNombrePantalla(),
                juego.getNombreJuego()
        );
    }

    @Transactional(readOnly = true)
    public List<BibliotecaResponseDTO> obtenerBibliotecaPorUsuario(Long idUsuario) {

        try {

            usuarioFeignClient.obtenerUsuarioPorId(idUsuario);

        } catch (FeignException.NotFound e) {

            throw new ResourceNotFoundException(
                    "Usuario con id " + idUsuario + " no encontrado"
            );
        }

        return bibliotecaRepository.findByIdUsuario(idUsuario)
                .stream()
                .map(biblioteca -> {

                    String nombreJuego = null;

                    try {

                        JuegoDTO juego = juegoFeignClient
                                .obtenerJuegoPorId(biblioteca.getIdJuego());

                        nombreJuego = juego.getNombreJuego();

                    } catch (Exception ignored) {
                    }

                    return mapToResponse(
                            biblioteca,
                            null,
                            nombreJuego
                    );
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public BibliotecaResponseDTO obtenerPorId(Long id) {

        BibliotecaUsuario bibliotecaUsuario =
                bibliotecaRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Entrada con id " + id + " no encontrada"
                                )
                        );

        return mapToResponse(
                bibliotecaUsuario,
                null,
                null
        );
    }

    @Transactional(readOnly = true)
    public boolean tieneJuego(Long idUsuario, Long idJuego) {

        return bibliotecaRepository.existsByIdUsuarioAndIdJuego(
                idUsuario,
                idJuego
        );
    }

    public BibliotecaResponseDTO cambiarEstado(
            Long id,
            EstadoJuego nuevoEstado
    ) {

        BibliotecaUsuario bibliotecaUsuario =
                bibliotecaRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Entrada con id " + id + " no encontrada"
                                )
                        );

        bibliotecaUsuario.setEstado(nuevoEstado);

        BibliotecaUsuario actualizado =
                bibliotecaRepository.save(bibliotecaUsuario);

        return mapToResponse(
                actualizado,
                null,
                null
        );
    }

    public void eliminar(Long id) {

        boolean existe = bibliotecaRepository.existsById(id);

        if (!existe) {

            throw new ResourceNotFoundException(
                    "Entrada con id " + id + " no encontrada"
            );
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