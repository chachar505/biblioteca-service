package com.gameup.library_service.repository;

import com.gameup.library_service.model.BibliotecaUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BibliotecaRepository extends JpaRepository<BibliotecaUsuario, Long> {

    List<BibliotecaUsuario> findByIdUsuario(Long idUsuario);

    Optional<BibliotecaUsuario> findByIdUsuarioAndIdJuego(Long idUsuario, Long idJuego);

    boolean existsByIdUsuarioAndIdJuego(Long idUsuario, Long idJuego);
}
