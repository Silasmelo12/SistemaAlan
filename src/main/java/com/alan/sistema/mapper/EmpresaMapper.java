package com.alan.sistema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.alan.sistema.dto.requests.EmpresaRequestDTO;
import com.alan.sistema.dto.response.EmpresaResponseDTO;
import com.alan.sistema.model.Empresa;

@Mapper(componentModel="spring")
public interface EmpresaMapper {

    @Mapping(target="id", ignore=true)
    @Mapping(target="dataCriacao", ignore=true)
    @Mapping(target="dataUltimaAtualizacao", ignore=true)
    @Mapping(target="status", ignore=true)
    @Mapping(target="asaasData", ignore=true)
    @Mapping(target="zapsignData", ignore=true)
    Empresa toEmpresa(EmpresaRequestDTO dto);

    @Mapping(target="signUrl", ignore=true)
    @Mapping(target="invoiceUrl", ignore=true)
    EmpresaResponseDTO toEmpresaResponseDto(Empresa empresa);

    /**
     * Atualiza uma instância existente de Empresa com os dados do DTO.
     * O @MappingTarget avisa ao MapStruct para NÃO criar uma nova Empresa,
     * mas sim dar 'set' na que já existe.
     */
    void updateEmpresaFromDto(EmpresaRequestDTO dto, @MappingTarget Empresa empresa);
}
