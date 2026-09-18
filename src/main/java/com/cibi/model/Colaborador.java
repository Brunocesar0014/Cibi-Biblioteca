package com.cibi.model;

public class Colaborador extends Aluno{

    private String telefone;
    private String senha;

    // Construtor vazio (obrigatório para funcionar bem com JDBC e frameworks)
    public Colaborador() {}

    // Construtor completo (útil para criar objetos rapidamente)
    public Colaborador(String nome, String senha, String turma, String telefone, long matricula) {
        super(nome, turma, matricula);
        this.senha = senha;
        this.telefone = telefone;
    }

    // Construtor com apenas usuario e senha (Para realizar login)
    public Colaborador(String nome, String senha) {
        super(nome, null, 0);
        this.senha = senha;
    }

    // Getters e Setters (não estáticos)

    public String getSenha() {
        return senha;
    }
    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTelefone() {
        return telefone;
    }
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}
