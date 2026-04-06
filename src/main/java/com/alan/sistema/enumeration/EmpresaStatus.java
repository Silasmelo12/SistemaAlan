package com.alan.sistema.enumeration;

/**
 * PROCESSANDO: O cadastro foi recebido, mas o sistema ainda está criando o cliente no Asaas ou gerando o contrato no ZapSign.

 * AGUARDANDO_PAGAMENTO: O boleto de adesão foi gerado e enviado, mas o Asaas ainda não confirmou o recebimento.

 * AGUARDANDO_ASSINATURA: O pagamento foi confirmado, mas o cliente ainda não assinou o contrato via ZapSign.

 * ERRO_INTEGRACAO: Algo falhou (ex: CPF inválido ou API fora do ar). Esse status serve para você filtrar no banco e atuar manualmente.
 
 * ATIVO: Pagamento confirmado e contrato assinado. O cliente tem acesso total ao sistema.

 * INADIMPLENTE: O cliente é ativo, mas uma mensalidade venceu e não foi paga. (O seu CobrancaScheduler pode mudar o status para este aqui automaticamente).

 * BLOQUEADO: Após X dias de inadimplência, o sistema corta o acesso, mas mantém os dados.
*/

public enum EmpresaStatus {
    PROCESSANDO,
    CLIENTE_CRIADO,
    AGUARDANDO_PAGAMENTO,
    AGUARDANDO_ASSINATURA,
    ATIVO,
    INADIMPLENTE,
    BLOQUEADO,
    ERRO_INTEGRACAO,
    CANCELADO;
}
