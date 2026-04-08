package com.alan.sistema.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.alan.sistema.model.Empresa;

@Repository
public interface EmpresaRepository extends MongoRepository<Empresa, String>{

    // Webhook Asaas: payment id fica em asaasData.ultimoPaymentId
    Optional<Empresa> findByAsaasDataUltimoPaymentId(String paymentId);

    Optional<Empresa> findByZapsignDataDocToken(String docToken);

    //verifica se existe alguma clinente com o cpfCnpj informado
    boolean existsByCpfCnpj(String cpfCnpj);

    Optional<Empresa> findByCpfCnpj(String cpfCnpj);

    Optional<Empresa> findByZapsignDataExternalId(String externalId);


}
