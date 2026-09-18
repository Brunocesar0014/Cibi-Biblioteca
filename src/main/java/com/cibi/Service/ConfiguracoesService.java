package com.cibi.Service;

import com.cibi.repository.ConfiguracoesRepository;

import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Regras de negócio das configurações do CIBI: valores padrão, validação de
 * limites e cache em memória para que as outras telas leiam sem ir ao banco.
 */
public class ConfiguracoesService {

    /* ====================== CHAVES ====================== */

    public static final String TEMA                = "aparencia.tema";
    public static final String COR_DESTAQUE        = "aparencia.cor_destaque";
    public static final String SIDEBAR_COMPACTA    = "aparencia.sidebar_compacta";
    public static final String TAMANHO_FONTE       = "aparencia.tamanho_fonte";

    public static final String PRAZO_DIAS          = "emprestimo.prazo_dias";
    public static final String LIMITE_LIVROS       = "emprestimo.limite_livros";
    public static final String RENOVACOES          = "emprestimo.renovacoes";
    public static final String MULTA_DIA           = "emprestimo.multa_dia";
    public static final String COBRAR_MULTA        = "emprestimo.cobrar_multa";
    public static final String BLOQUEAR_ATRASO     = "emprestimo.bloquear_atraso";

    public static final String ALERTA_DEVOLUCAO    = "notificacao.devolucao_hoje";
    public static final String ALERTA_ATRASO       = "notificacao.atraso";
    public static final String ALERTA_ESTOQUE      = "notificacao.sem_exemplar";
    public static final String ANTECEDENCIA_DIAS   = "notificacao.antecedencia_dias";

    public static final String NOME_BIBLIOTECA     = "sistema.nome_biblioteca";
    public static final String ITENS_PAGINA        = "sistema.itens_pagina";
    public static final String PASTA_BACKUP        = "sistema.pasta_backup";
    public static final String BACKUP_AUTOMATICO   = "sistema.backup_automatico";

    /* ====================== PADRÕES ====================== */

    private static final Map<String, String> PADROES = criarPadroes();

    private static Map<String, String> criarPadroes() {
        Map<String, String> p = new LinkedHashMap<>();
        p.put(TEMA, "ESCURO");
        p.put(COR_DESTAQUE, "#2563EB");
        p.put(SIDEBAR_COMPACTA, "false");
        p.put(TAMANHO_FONTE, "MEDIO");

        p.put(PRAZO_DIAS, "14");
        p.put(LIMITE_LIVROS, "3");
        p.put(RENOVACOES, "1");
        p.put(MULTA_DIA, "0.50");
        p.put(COBRAR_MULTA, "false");
        p.put(BLOQUEAR_ATRASO, "true");

        p.put(ALERTA_DEVOLUCAO, "true");
        p.put(ALERTA_ATRASO, "true");
        p.put(ALERTA_ESTOQUE, "false");
        p.put(ANTECEDENCIA_DIAS, "2");

        p.put(NOME_BIBLIOTECA, "CIBI — Controle Interno de Biblioteca");
        p.put(ITENS_PAGINA, "50");
        p.put(PASTA_BACKUP, System.getProperty("user.home") + "/CIBI/backup");
        p.put(BACKUP_AUTOMATICO, "false");
        return Collections.unmodifiableMap(p);
    }

    /* ====================== ESTADO ====================== */

    private final ConfiguracoesRepository repository;
    private Map<String, String> cache;

    public ConfiguracoesService() {
        this(new ConfiguracoesRepository());
    }

    public ConfiguracoesService(ConfiguracoesRepository repository) {
        this.repository = repository;
    }

    /* ====================== LEITURA ====================== */

    /**
     * Devolve todas as configurações: os padrões sobrescritos pelo que estiver
     * gravado no banco. Se o banco falhar, os padrões são usados para que a
     * tela continue abrindo.
     */
    public Map<String, String> carregarTodas() {
        Map<String, String> resultado = new LinkedHashMap<>(PADROES);
        try {
            resultado.putAll(repository.buscarTodas());
        } catch (SQLException e) {
            System.err.println("[CIBI] Falha ao ler configurações, usando padrões: " + e.getMessage());
        }
        cache = resultado;
        return new LinkedHashMap<>(resultado);
    }

    private Map<String, String> cache() {
        if (cache == null) {
            carregarTodas();
        }
        return cache;
    }

    public String get(String chave) {
        String valor = cache().get(chave);
        return valor != null ? valor : PADROES.get(chave);
    }

    public int getInt(String chave) {
        try {
            return Integer.parseInt(get(chave).trim());
        } catch (NumberFormatException e) {
            return Integer.parseInt(PADROES.get(chave));
        }
    }

    public double getDouble(String chave) {
        try {
            return Double.parseDouble(get(chave).trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            return Double.parseDouble(PADROES.get(chave));
        }
    }

    public boolean getBoolean(String chave) {
        return Boolean.parseBoolean(get(chave));
    }

    public Map<String, String> getPadroes() {
        return new LinkedHashMap<>(PADROES);
    }

    /* ====================== ESCRITA ====================== */

    /**
     * Valida e grava as configurações informadas.
     *
     * @throws ConfiguracaoInvalidaException quando algum valor está fora dos limites
     * @throws SQLException                  quando a gravação falha
     */
    public void salvar(Map<String, String> valores) throws ConfiguracaoInvalidaException, SQLException {
        validar(valores);
        repository.salvarTodas(valores);
        cache().putAll(valores);
    }

    /**
     * Restaura todos os valores originais do sistema.
     */
    public void restaurarPadroes() throws SQLException {
        repository.limpar();
        repository.salvarTodas(PADROES);
        cache = new LinkedHashMap<>(PADROES);
    }

    /* ====================== VALIDAÇÃO ====================== */

    private void validar(Map<String, String> v) throws ConfiguracaoInvalidaException {
        int prazo = inteiro(v, PRAZO_DIAS, "Prazo de empréstimo");
        if (prazo < 1 || prazo > 90) {
            throw new ConfiguracaoInvalidaException("O prazo de empréstimo deve ficar entre 1 e 90 dias.");
        }

        int limite = inteiro(v, LIMITE_LIVROS, "Limite de livros");
        if (limite < 1 || limite > 20) {
            throw new ConfiguracaoInvalidaException("O limite de livros por usuário deve ficar entre 1 e 20.");
        }

        int renovacoes = inteiro(v, RENOVACOES, "Renovações permitidas");
        if (renovacoes < 0 || renovacoes > 5) {
            throw new ConfiguracaoInvalidaException("O número de renovações deve ficar entre 0 e 5.");
        }

        int antecedencia = inteiro(v, ANTECEDENCIA_DIAS, "Antecedência do aviso");
        if (antecedencia < 0 || antecedencia > prazo) {
            throw new ConfiguracaoInvalidaException(
                    "A antecedência do aviso não pode ser negativa nem maior que o prazo de empréstimo.");
        }

        if (v.containsKey(MULTA_DIA)) {
            double multa;
            try {
                multa = Double.parseDouble(v.get(MULTA_DIA).trim().replace(',', '.'));
            } catch (NumberFormatException e) {
                throw new ConfiguracaoInvalidaException("Informe a multa como um valor numérico. Ex.: 0,50");
            }
            if (multa < 0 || multa > 999) {
                throw new ConfiguracaoInvalidaException("A multa por dia deve ficar entre R$ 0,00 e R$ 999,00.");
            }
        }

        if (v.containsKey(NOME_BIBLIOTECA) && v.get(NOME_BIBLIOTECA).trim().isEmpty()) {
            throw new ConfiguracaoInvalidaException("Informe o nome da biblioteca.");
        }
    }

    private int inteiro(Map<String, String> v, String chave, String rotulo)
            throws ConfiguracaoInvalidaException {
        String bruto = v.containsKey(chave) ? v.get(chave) : get(chave);
        try {
            return Integer.parseInt(bruto.trim());
        } catch (NumberFormatException e) {
            throw new ConfiguracaoInvalidaException(rotulo + ": informe um número inteiro válido.");
        }
    }

    /** Erro de validação das configurações — tratado pelo controller. */
    public static class ConfiguracaoInvalidaException extends Exception {
        public ConfiguracaoInvalidaException(String mensagem) {
            super(mensagem);
        }
    }
}
