package com.gameup.library_service.assembler;

import com.gameup.library_service.controller.BibliotecaController;
import com.gameup.library_service.dto.BibliotecaResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class BibliotecaModelAssembler implements RepresentationModelAssembler<BibliotecaResponseDTO, EntityModel<BibliotecaResponseDTO>> {

    @Override
    public EntityModel<BibliotecaResponseDTO> toModel(BibliotecaResponseDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(BibliotecaController.class).obtenerPorId(dto.getId())).withSelfRel(),
                Link.of("/api/biblioteca/usuario/" + dto.getIdUsuario()).withRel("biblioteca-usuario")
        );
    }
}