package Bancos;

import java.util.HashMap;
import java.util.Map;

public class codLiquItau {
    private Map<String, String> liquidacao = new HashMap<String,String>();
    private Map<String, String> ocorrencias = new HashMap<String,String>();
    private Map<String, String> entradasrej = new HashMap<String,String>();
    private Map<String, String> alteracaorej = new HashMap<String,String>();
    private Map<String, String> instrucaorej = new HashMap<String,String>();
    private Map<String, String> baixasrej = new HashMap<String,String>();

    public codLiquItau() {
        // Liquidação
        this.liquidacao = new HashMap<String,String>();
        this.liquidacao.put("AA","CAIXA ELETRÔNICO ITAÚ");
        this.liquidacao.put("AC","PAGAMENTO EM CARTÓRIO AUTOMATIZADO");
        this.liquidacao.put("AO", "ACERTO ONLINE");
        this.liquidacao.put("BC", "BANCOS CORRESPONDENTES");
        this.liquidacao.put("BF", "ITAÚ BANKFONE");
        this.liquidacao.put("BL", "ITAÚ BANKLINE");
        this.liquidacao.put("B0", "OUTROS BANCOS – RECEBIMENTO OFF-LINE");
        this.liquidacao.put("B1", "OUTROS BANCOS – PELO CÓDIGO DE BARRAS");
        this.liquidacao.put("B2", "OUTROS BANCOS – PELA LINHA DIGITÁVEL");
        this.liquidacao.put("B3", "OUTROS BANCOS – PELO AUTO ATENDIMENTO");
        this.liquidacao.put("B4", "OUTROS BANCOS – RECEBIMENTO EM CASA LOTÉRICA");
        this.liquidacao.put("B5", "OUTROS BANCOS – CORRESPONDENTE");
        this.liquidacao.put("B6", "OUTROS BANCOS – TELEFONE");
        this.liquidacao.put("B7", "OUTROS BANCOS – ARQUIVO ELETRÔNICO");
        this.liquidacao.put("CC", "AGÊNCIA ITAÚ – COM CHEQUE DE OUTRO BANCO");
        this.liquidacao.put("CI", "CORRESPONDENTE ITAÚ");
        this.liquidacao.put("CK", "SISPAG – SISTEMA DE CONTAS A PAGAR ITAÚ");
        this.liquidacao.put("CP", "AGÊNCIA ITAÚ – POR DÉBITO EM CONTA CORRENTE, CHEQUE ITAÚ* OU DINHEIRO");
        this.liquidacao.put("DG", "AGÊNCIA ITAÚ – CAPTURADO EM OFF-LINE");
        this.liquidacao.put("LC", "PAGAMENTO EM CARTÓRIO DE PROTESTO COM CHEQUE");
        this.liquidacao.put("EA", "TERMINAL DE CAIXA");
        this.liquidacao.put("Q0", "AGENDAMENTO – PAGAMENTO AGENDADO VIA BANKLINE OU OUTRO CANAL ELETRÔNICO E LIQUIDADO NA DATA INDICADA");
        this.liquidacao.put("RA", "DIGITAÇÃO – REALIMENTAÇÃO AUTOMÁTICA");
        this.liquidacao.put("ST", "PAGAMENTO VIA SELTEC");
        
        // Ocorrencias
        this.ocorrencias = new HashMap<String,String>();
        this.ocorrencias.put("02", "ENTRADA CONFIRMADA");
        this.ocorrencias.put("03", "ENTRADA REJEITADA");
        this.ocorrencias.put("04" ,"ALTERAÇÃO DE DADOS – NOVA ENTRADA OU ALTERAÇÃO/EXCLUSÃOADOS ACATADA");
        this.ocorrencias.put("05" ,"ALTERAÇÃO DE DADOS – BAIXA");
        this.ocorrencias.put("06" ,"LIQUIDAÇÃO NORMAL");
        this.ocorrencias.put("08" ,"LIQUIDAÇÃO EM CARTÓRIO");
        this.ocorrencias.put("09" ,"BAIXA SIMPLES");
        this.ocorrencias.put("10" ,"BAIXA POR TER SIDO LIQUIDADO");
        this.ocorrencias.put("11" ,"EM SER (SÓ NO RETORNO MENSAL)");
        this.ocorrencias.put("12" ,"ABATIMENTO CONCEDIDO");
        this.ocorrencias.put("13" ,"ABATIMENTO CANCELADO");
        this.ocorrencias.put("14" ,"VENCIMENTO ALTERADO");
        this.ocorrencias.put("15" ,"BAIXAS REJEITADAS");
        this.ocorrencias.put("16" ,"INSTRUÇÕES REJEITADAS");
        this.ocorrencias.put("17" ,"ALTERAÇÃO/EXCLUSÃO DE DADOS REJEITADA");
        this.ocorrencias.put("18" ,"COBRANÇA CONTRATUAL – INSTRUÇÕES/ALTERAÇÕES REJEITADAS/PENDENTES");
        this.ocorrencias.put("19" ,"CONFIRMAÇÃO RECEBIMENTO DE INSTRUÇÃO DE PROTESTO");
        this.ocorrencias.put("20" ,"CONFIRMAÇÃO RECEBIMENTO DE INSTRUÇÃO DE SUSTAÇÃO DE PROTESTO /TARIFA");
        this.ocorrencias.put("21" ,"CONFIRMAÇÃO RECEBIMENTO DE INSTRUÇÃO DE NÃO PROTESTAR");
        this.ocorrencias.put("23" ,"PROTESTO ENVIADO A CARTÓRIO/TARIFA");
        this.ocorrencias.put("24" ,"INSTRUÇÃO DE PROTESTO SUSTADA");
        this.ocorrencias.put("25" ,"ALEGAÇÕES DO PAGADOR");
        this.ocorrencias.put("26" ,"TARIFA DE AVISO DE COBRANÇA");
        this.ocorrencias.put("27" ,"TARIFA DE EXTRATO POSIÇÃO (B40X)");
        this.ocorrencias.put("28" ,"TARIFA DE RELAÇÃO DAS LIQUIDAÇÕES");
        this.ocorrencias.put("29" ,"TARIFA DE MANUTENÇÃO DE TÍTULOS VENCIDOS");
        this.ocorrencias.put("30" ,"DÉBITO MENSAL DE TARIFAS (PARA ENTRADAS E BAIXAS)");
        this.ocorrencias.put("32" ,"BAIXA POR TER SIDO PROTESTADO");
        this.ocorrencias.put("33" ,"CUSTAS DE PROTESTOJaneiro 2017 Cobrança FEBRABAN 240 22");
        this.ocorrencias.put("34" ,"CUSTAS DE SUSTAÇÃO");
        this.ocorrencias.put("35" ,"CUSTAS DE CARTÓRIO DISTRIBUIDOR");
        this.ocorrencias.put("36" ,"CUSTAS DE EDITAL");
        this.ocorrencias.put("37" ,"TARIFA DE EMISSÃO DE BOLETO/TARIFA DE ENVIO DE DUPLICATA");
        this.ocorrencias.put("38" ,"TARIFA DE INSTRUÇÃO");
        this.ocorrencias.put("39" ,"TARIFA DE OCORRÊNCIAS");
        this.ocorrencias.put("40" ,"TARIFA MENSAL DE EMISSÃO DE BOLETO/TARIFA MENSAL DE ENVIO DE DUPLICATA");
        this.ocorrencias.put("41" ,"DÉBITO MENSAL DE TARIFAS – EXTRATO DE POSIÇÃO (B4EP/B4OX)");
        this.ocorrencias.put("42" ,"DÉBITO MENSAL DE TARIFAS – OUTRAS INSTRUÇÕES");
        this.ocorrencias.put("43" ,"DÉBITO MENSAL DE TARIFAS – MANUTENÇÃO DE TÍTULOS VENCIDOS");
        this.ocorrencias.put("44" ,"DÉBITO MENSAL DE TARIFAS – OUTRAS OCORRÊNCIAS");
        this.ocorrencias.put("45" ,"DÉBITO MENSAL DE TARIFAS – PROTESTO");
        this.ocorrencias.put("46" ,"DÉBITO MENSAL DE TARIFAS – SUSTAÇÃO DE PROTESTO");
        this.ocorrencias.put("47" ,"BAIXA COM TRANSFERÊNCIA PARA DESCONTO");
        this.ocorrencias.put("48" ,"CUSTAS DE SUSTAÇÃO JUDICIAL");
        this.ocorrencias.put("51" ,"TARIFA MENSAL REFERENTE A ENTRADAS BANCOS CORRESPONDENTES NA CARTEIRA");
        this.ocorrencias.put("52" ,"TARIFA MENSAL BAIXAS NA CARTEIRA");
        this.ocorrencias.put("53" ,"TARIFA MENSAL BAIXAS EM BANCOS CORRESPONDENTES NA CARTEIRA");
        this.ocorrencias.put("54" ,"TARIFA MENSAL DE LIQUIDAÇÕES NA CARTEIRA");
        this.ocorrencias.put("55" ,"TARIFA MENSAL DE LIQUIDAÇÕES EM BANCOS CORRESPONDENTES NA CARTEIRA");
        this.ocorrencias.put("56" ,"CUSTAS DE IRREGULARIDADE");
        this.ocorrencias.put("57" ,"INSTRUÇÃO CANCELADA");
        this.ocorrencias.put("60" ,"ENTRADA REJEITADA CARNÊ");
        this.ocorrencias.put("61" ,"TARIFA EMISSÃO AVISO DE MOVIMENTAÇÃO DE TÍTULOS (2154)");
        this.ocorrencias.put("62" ,"DÉBITO MENSAL DE TARIFA – AVISO DE MOVIMENTAÇÃO DE TÍTULOS (2154)");
        this.ocorrencias.put("63" ,"TÍTULO SUSTADO JUDICIALMENTE");
        this.ocorrencias.put("74" ,"INSTRUÇÃO DE NEGATIVAÇÃO EXPRESSA REJEITADA");
        this.ocorrencias.put("75" ,"CONFIRMA O RECEBIMENTO DE INSTRUÇÃO DE ENTRADA EM NEGATIVAÇÃO EXPRESSA");
        this.ocorrencias.put("77" ,"CONFIRMA O RECEBIMENTO DE INSTRUÇÃO DE EXCLUSÃO DE ENTRADA EM NEGATIVAÇÃO EXPRESSA");
        this.ocorrencias.put("78" ,"CONFIRMA O RECEBIMENTO DE INSTRUÇÃO DE CANCELAMENTO DA NEGATIVAÇÃO EXPRESSA");
        this.ocorrencias.put("79" ,"NEGATIVAÇÃO EXPRESSA INFORMACIONAL");
        this.ocorrencias.put("80" ,"CONFIRMAÇÃO DE ENTRADA EM NEGATIVAÇÃO EXPRESSA – TARIFA");
        this.ocorrencias.put("82" ,"CONFIRMAÇÃO O CANCELAMENTO DE NEGATIVAÇÃO EXPRESSA - TARIFA");
        this.ocorrencias.put("83" ,"CONFIRMAÇÃO DA EXCLUSÃO/CANCELAMENTO DA NEGATIVAÇÃO EXPRESSA POR LIQUIDAÇÃO - TARIFA");
        this.ocorrencias.put("85" ,"TARIFA POR BOLETO (ATÉ 03 ENVIOS) COBRANÇA ATIVA ELETRÔNICA");
        this.ocorrencias.put("86" ,"TARIFA EMAIL COBRANÇA ATIVA ELETRÔNICA");
        this.ocorrencias.put("87" ,"TARIFA SMS COBRANÇA ATIVA ELETRÔNICA");
        this.ocorrencias.put("88" ,"TARIFA MENSAL POR BOLETO (ATÉ 03 ENVIOS) COBRANÇA ATIVA ELETRÔNICA");
        this.ocorrencias.put("89" ,"TARIFA MENSAL EMAIL COBRANÇA ATIVA ELETRÔNICA");
        this.ocorrencias.put("90" ,"TARIFA MENSAL SMS COBRANÇA ATIVA ELETRÔNICA");
        this.ocorrencias.put("91" ,"TARIFA MENSAL DE EXCLUSÃO DE ENTRADA EM NEGATIVAÇÃO EXPRESSA");
        this.ocorrencias.put("92" ,"TARIFA MENSAL DE CANCELAMENTO DE NEGATIVAÇÃO EXPRESSA");
        this.ocorrencias.put("93" ,"TARIFA MENSAL DE EXCLUSÃO/CANCELAMENTO DE NEGATIVAÇÃO EXPRESSA POR LIQUIDAÇÃO");
        this.ocorrencias.put("94" ,"CONFIRMA RECEBIMENTO DE INSTRUÇÃO DE NÃO NEGATIVAR");
        
        // Entradas rejeitadas
        this.entradasrej = new HashMap<String,String>();
        this.entradasrej.put("03", "NÃO FOI POSSÍVEL ATRIBUIR A AGÊNCIA PELO CEP OU CEP INVÁLIDO");
        this.entradasrej.put("04", "SIGLA DO ESTADO INVÁLIDA");
        this.entradasrej.put("05", "PRAZO DA OPERAÇÃO MENOR QUE PRAZO MÍNIMO OU MAIOR QUE O MÁXIMO)");
        this.entradasrej.put("08", "NOME DO PAGADOR NÃO INFORMADO OU DESLOCADO");
        this.entradasrej.put("09", "AGÊNCIA/CONTA AGÊNCIA ENCERRADA");
        this.entradasrej.put("10", "LOGRADOURO NÃO INFORMADO OU DESLOCADO");
        this.entradasrej.put("11", "CEP CEP NÃO NUMÉRICO");
        this.entradasrej.put("12", "SACADOR AVALISTA NOME NÃO INFORMADO OU DESLOCADO (BANCOS CORRESPONDENTES)");
        this.entradasrej.put("13", "ESTADO/CEP CEP INCOMPATÍVEL COM A SIGLA DO ESTADO");
        this.entradasrej.put("14", "NOSSO NÚMERO NOSSO NÚMERO JÁ REGISTRADO NO CADASTRO DO BANCO OU FORA DA FAIXA");
        this.entradasrej.put("15", "NOSSO NÚMERO NOSSO NÚMERO EM DUPLICIDADE NO MESMO MOVIMENTO");
        this.entradasrej.put("18", "DATA DE ENTRADA DATA DE ENTRADA INVÁLIDA PARA OPERAR COM ESTA CARTEIRA");
        this.entradasrej.put("19", "OCORRÊNCIA OCORRÊNCIA INVÁLIDA");
        this.entradasrej.put("21", "AG. COBRADORA \n CARTEIRA NÃO ACEITA DEPOSITÁRIA CORRESPONDENTE\nESTADO DA AGÊNCIA DIFERENTE DO ESTADO DO PAGADOR\nAG. COBRADORA NÃO CONSTA NO CADASTRO OU ENCERRANDO");
        this.entradasrej.put("22", "CARTEIRA CARTEIRA NÃO PERMITIDA (NECESSÁRIO CADASTRAR FAIXA LIVRE)");
        this.entradasrej.put("27", "CNPJ INAPTO CNPJ DO BENEFICIÁRIO INAPTO\nDEVOLUÇÃO DE TÍTULO EM GARANTIA");
        this.entradasrej.put("29", "CÓDIGO EMPRESA CATEGORIA DA CONTA INVÁLIDA");
        this.entradasrej.put("31", "AGÊNCIA/CONTA CONTA NÃO TEM PERMISSÃO PARA PROTESTAR (CONTATE SEU GERENTE)");
        this.entradasrej.put("35", "VALOR DO IOF IOF MAIOR QUE 5%");
        this.entradasrej.put("36", "QTDADE DE MOEDA QUANTIDADE DE MOEDA INCOMPATÍVEL COM VALOR DO TÍTULO");
        this.entradasrej.put("37", "CNPJ/CPF DO PAGADOR NÃO NUMÉRICO OU IGUAL A ZEROS");
        this.entradasrej.put("42", "NOSSO NÚMERO NOSSO NÚMERO FORA DE FAIXA");
        this.entradasrej.put("52", "AG. COBRADORA EMPRESA NÃO ACEITA BANCO CORRESPONDENTE");
        this.entradasrej.put("53", "AG. COBRADORA EMPRESA NÃO ACEITA BANCO CORRESPONDENTE - COBRANÇA MENSAGEM");
        this.entradasrej.put("54", "DATA DE VENCTO BANCO CORRESPONDENTE – TÍTULO COM VENCIMENTO INFERIOR A 15 DIAS");
        this.entradasrej.put("55", "DEP./BCO. CORRESP. CEP NÃO PERTENCE A DEPOSITÁRIA INFORMADA");
        this.entradasrej.put("56", "DT. VCTO./BCO. CORESP. VENCTO. SUPERIOR A 180 DIAS DA DATA DE ENTRADA");
        this.entradasrej.put("57", "DATA DE VENCIMENTO CEP SÓ DEPOSITÁRIA BCO. DO BRASIL COM VENCTO. INFERIOR A 8 DIAS");
        this.entradasrej.put("60", "ABATIMENTO VALOR DO ABATIMENTO INVÁLIDO");
        this.entradasrej.put("61", "JUROS DE MORA JUROS DE MORA MAIOR QUE O PERMITIDO");
        this.entradasrej.put("62", "DESCONTO VALOR DO DESCONTO MAIOR QUE O VALOR DO TÍTULO");
        this.entradasrej.put("63", "DESCONTO DE ANTECIPAÇÃO VALOR DA IMPORTÂNCIA POR DIA DE DESCONTO (IDD) NÃO PERMITIDO");
        this.entradasrej.put("64", "EMISSÃO DO TÍTULO DATA DE EMISSÃO DO TÍTULO INVÁLIDA (VENDOR)");
        this.entradasrej.put("65", "TAXA FINANCTO. TAXA INVÁLIDA (VENDOR)");
        this.entradasrej.put("66", "DATA DE VENCTO.. INVALIDA/FORA DE PRAZO DE OPERAÇÃO (MÍNIMO OU MÁXIMO)");
        this.entradasrej.put("67", "VALOR/QTIDADE. VALOR DO TÍTULO/QUANTIDADE DE MOEDA INVÁLIDO");
        this.entradasrej.put("68", "CARTEIRA CARTEIRA INVÁLIDA OU NÃO CADASTRADA NO INTERCÂMBIO DA COBRANÇA");
        this.entradasrej.put("98", "FLASH INVÁLIDO REGISTRO MENSAGEM SEM FLASH CADASTRADO OU FLASH INFORMADO DIFERENTE DO\nCADASTRADO Janeiro 2017 Cobrança FEBRABAN 240 24");
        this.entradasrej.put("99", "FLASH INVÁLIDO CONTA DE COBRANÇA COM FLASH CADASTRADO E SEM REGISTRO DE MENSAGEM\nCORRESPONDENTE");
        
        // Alteracao de dados rejeitada
        this.alteracaorej = new HashMap<String,String>();
        this.alteracaorej.put("02", "AGÊNCIA COBRADORA INVÁLIDA OU COM O MESMO CONTEÚDO");
        this.alteracaorej.put("04", "SIGLA DO ESTADO INVÁLIDA");
        this.alteracaorej.put("05", "DATA DE VENCIMENTO INVÁLIDA OU COM O MESMO CONTEÚDO");
        this.alteracaorej.put("06", "VALOR DO TÍTULO COM OUTRA ALTERAÇÃO SIMULTÂNEA");
        this.alteracaorej.put("08", "NOME DO PAGADOR COM O MESMO CONTEÚDO");
        this.alteracaorej.put("11", "CEP INVÁLIDO");
        this.alteracaorej.put("12", "NÚMERO INSCRIÇÃO INVÁLIDO DO SACADOR AVALISTA");
        this.alteracaorej.put("13", "SEU NÚMERO COM O MESMO CONTEÚDO");
        this.alteracaorej.put("21", "AGÊNCIA COBRADORA NÃO CONSTA NO CADASTRO DE DEPOSITÁRIA OU EM ENCERRAMENTO");
        this.alteracaorej.put("42", "ALTERAÇÃO INVÁLIDA PARA TÍTULO VENCIDO");
        this.alteracaorej.put("43", "ALTERAÇÃO BLOQUEADA – VENCIMENTO JÁ ALTERADO");
        this.alteracaorej.put("53", "INSTRUÇÃO COM O MESMO CONTEÚDO");
        this.alteracaorej.put("54", "DATA VENCIMENTO PARA BANCOS CORRESPONDENTES INFERIOR AO ACEITO PELO BANCO");
        this.alteracaorej.put("55", "ALTERAÇÕES IGUAIS PARA O MESMO CONTROLE (AGÊNCIA/CONTA/CARTEIRA/NOSSO NÚMERO)");
        this.alteracaorej.put("60", "VALOR DE IOF – ALTERAÇÃO NÃO PERMITIDA PARA CARTEIRAS DE N.S. – MOEDA VARIÁVEL");
        this.alteracaorej.put("61", "TÍTULO JÁ BAIXADO OU LIQUIDADO OU NÃO EXISTE TÍTULO CORRESPONDENTE NO SISTEMA");
        this.alteracaorej.put("66", "ALTERAÇÃO NÃO PERMITIDA PARA CARTEIRAS DE NOTAS DE SEGUROS – MOEDA VARIÁVEL");
        this.alteracaorej.put("67", "NOME INVÁLIDO DO SACADOR AVALISTA");
        this.alteracaorej.put("72", "ENDEREÇO INVÁLIDO – SACADOR AVALISTA");
        this.alteracaorej.put("73", "BAIRRO INVÁLIDO – SACADOR AVALISTA");
        this.alteracaorej.put("74", "CIDADE INVÁLIDA – SACADOR AVALISTA");
        this.alteracaorej.put("75", "SIGLA ESTADO INVÁLIDO – SACADOR AVALISTA");
        this.alteracaorej.put("76", "CEP INVÁLIDO – SACADOR AVALISTA");
        this.alteracaorej.put("81", "ALTERAÇÃO BLOQUEADA - TÍTULO COM NEGATIVAÇÃO EXPRESSA OU PROTESTO");
        
        // Instrucoes rejeitadas
        this.instrucaorej = new HashMap<String,String>();
        this.instrucaorej.put("01","INSTRUÇÃO/OCORRÊNCIA NÃO EXISTENTE");
        this.instrucaorej.put("03","CONTA NÃO TEM PERMISSÃO PARA PROTESTAR (CONTATE SEU GERENTE)");
        this.instrucaorej.put("06","NOSSO NÚMERO IGUAL A ZEROS");
        this.instrucaorej.put("09","CNPJ/CPF DO SACADOR/AVALISTA INVÁLIDO");
        this.instrucaorej.put("14","REGISTRO EM DUPLICIDADE");
        this.instrucaorej.put("15","CNPJ/CPF INFORMADO SEM NOME DO SACADOR/AVALISTA");
        this.instrucaorej.put("19","VALOR DO ABATIMENTO MAIOR QUE 90% DO VALOR DO TÍTULO");
        this.instrucaorej.put("20","EXISTE SUSTACAO DE PROTESTO PENDENTE PARA O TITULO");
        this.instrucaorej.put("21","TÍTULO NÃO REGISTRADO NO SISTEMA");
        this.instrucaorej.put("22","TÍTULO BAIXADO OU LIQUIDADO");
        this.instrucaorej.put("23","INSTRUÇÃO NÃO ACEITA");
        this.instrucaorej.put("24","INSTRUÇÃO INCOMPATÍVEL - EXISTE INSTRUÇÃO DE PROTESTO PARA O TÍTULO");
        this.instrucaorej.put("25","INSTRUÇÃO INCOMPATÍVEL - NÃO EXISTE INSTRUÇÃO DE PROTESTO PARA O TÍTULO");
        this.instrucaorej.put("26","INSTRUÇÃO NÃO ACEITA POR JÁ TER SIDO EMITIDA A ORDEM DE PROTESTO AO CARTÓRIO");
        this.instrucaorej.put("27","INSTRUÇÃO NÃO ACEITA POR NÃO TER SIDO EMITIDA A ORDEM DE PROTESTO AO CARTÓRIO");
        this.instrucaorej.put("28","JÁ EXISTE UMA MESMA INSTRUÇÃO CADASTRADA ANTERIORMENTE PARA O TÍTULO");
        this.instrucaorej.put("29","VALOR LÍQUIDO + VALOR DO ABATIMENTO DIFERENTE DO VALOR DO TÍTULO REGISTRADO");
        this.instrucaorej.put("30","EXISTE UMA INSTRUÇÃO DE NÃO PROTESTAR ATIVA PARA O TÍTULO");
        this.instrucaorej.put("31","EXISTE UMA OCORRÊNCIA DO PAGADOR QUE BLOQUEIA A INSTRUÇÃO");
        this.instrucaorej.put("32","DEPOSITÁRIA DO TÍTULO = 9999 OU CARTEIRA NÃO ACEITA PROTESTO");
        this.instrucaorej.put("33","ALTERAÇÃO DE VENCIMENTO IGUAL À REGISTRADA NO SISTEMA OU QUE TORNA O TÍTULO VENCIDO");
        this.instrucaorej.put("34","INSTRUÇÃO DE EMISSÃO DE AVISO DE COBRANÇA PARA TÍTULO VENCIDO ANTES DO VENCIMENTO");
        this.instrucaorej.put("35","SOLICITAÇÃO DE CANCELAMENTO DE INSTRUÇÃO INEXISTENTE");
        this.instrucaorej.put("36","TÍTULO SOFRENDO ALTERAÇÃO DE CONTROLE (AGÊNCIA/CONTA/CARTEIRA/NOSSO NÚMERO)");
        this.instrucaorej.put("37","INSTRUÇÃO NÃO PERMITIDA PARA A CARTEIRA");
        this.instrucaorej.put("40","INSTRUÇÃO INCOMPATÍVEL – NÃO EXISTE INSTRUÇÃO DE NEGATIVAÇÃO EXPRESSA PARA O TÍTULO");
        this.instrucaorej.put("41","INSTRUÇÃO NÃO PERMITIDA – TÍTULO JÁ ENVIADO PARA NEGATIVAÇÃO EXPRESSA");
        this.instrucaorej.put("42","INSTRUÇÃO NÃO PERMITIDA – TÍTULO COM NEGATIVAÇÃO EXPRESSA CONCLUÍDA");
        this.instrucaorej.put("43","PRAZO INVÁLIDO PARA NEGATIVAÇÃO – MÍNIMO; 02 DIAS CORRIDOS APÓS O VENCIMENTO");
        this.instrucaorej.put("45","INSTRUÇÃO INCOMPATÍVEL PARA O MESMO TÍTULO NESTA DATA");
        this.instrucaorej.put("47","INSTRUÇÃO NÃO PERMITIDA – ESPÉCIE INVÁLIDA");
        this.instrucaorej.put("48","DADOS DO PAGADOR INVÁLIDOS (CPF / CNPJ / NOME)");
        this.instrucaorej.put("49","DADOS DO ENDEREÇO DO PAGADOR INVÁLIDOS");
        this.instrucaorej.put("50","DATA DE EMISSÃO DO TÍTULO INVÁLIDA");
        this.instrucaorej.put("51","INSTRUÇÃO NÃO PERMITIDA – TÍTULO COM NEGATIVAÇÃO EXPRESSA AGENDADA");
        
        // Baixas rejeitadas
        this.baixasrej = new HashMap<String,String>();
        this.baixasrej.put("04","NOSSO NÚMERO EM DUPLICIDADE NUM MESMO MOVIMENTO");
        this.baixasrej.put("05","SOLICITAÇÃO DE BAIXA PARA TÍTULO JÁ BAIXADO OU LIQUIDADO");
        this.baixasrej.put("06","SOLICITAÇÃO DE BAIXA PARA TÍTULO NÃO REGISTRADO NO SISTEMA");
        this.baixasrej.put("07","COBRANÇA PRAZO CURTO - SOLICITAÇÃO DE BAIXA P/ TÍTULO NÃO REGISTRADO NO SISTEMA");
        this.baixasrej.put("08","SOLICITAÇÃO DE BAIXA PARA TÍTULO EM FLOATING");
        this.baixasrej.put("16","ABATIMENTO/ALTERAÇÃO DO VALOR DO TÍTULO OU SOLICITAÇÃO DE BAIXA BLOQUEADOS");
        this.baixasrej.put("40","NÃO APROVADA DEVIDO AO IMPACTO NA ELEGIBILIDADE DE GARANTIAS");
        this.baixasrej.put("41","AUTOMATICAMENTE REJEITADA");
        this.baixasrej.put("42","CONFIRMA RECEBIMENTO DE INSTRUÇÃO – PENDENTE DE ANÁLISE");
    }

    public String getLiquidacao(String key) { 
        return liquidacao.get(key).toString();
    }

    public String getOcorrencias(String key) {
        return ocorrencias.get(key).toString();
    }

    public String getEntradasrej(String key) {
        return entradasrej.get(key).toString();
    }

    public String getAlteracaorej(String key) {
        return alteracaorej.get(key).toString();
    }

    public String getInstrucaorej(String key) {
        return instrucaorej.get(key).toString();
    }

    public String getBaixasrej(String key) {
        return baixasrej.get(key).toString();
    }          
}
