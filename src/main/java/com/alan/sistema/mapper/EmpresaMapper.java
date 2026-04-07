package com.alan.sistema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.alan.sistema.dto.requests.EmpresaRequestDTO;
import com.alan.sistema.dto.response.EmpresaResponseDTO;
import com.alan.sistema.model.Empresa;

@Mapper(componentModel="spring")
public interface EmpresaMapper {

    Empresa toEmpresa (EmpresaRequestDTO dto);

    EmpresaResponseDTO toEmpresaResponseDto (Empresa empresa);

    /**
     * Atualiza uma instância existente de Empresa com os dados do DTO.
     * O @MappingTarget avisa ao MapStruct para NÃO criar uma nova Empresa,
     * mas sim dar 'set' na que já existe.
     */
    void updateEmpresaFromDto(EmpresaRequestDTO dto, @MappingTarget Empresa empresa);
}
