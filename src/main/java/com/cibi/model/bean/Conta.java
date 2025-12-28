package com.cibi.model.bean;

public class Conta {
    
    private int id;
    private String usuario;
    private String senha;
    private String perfil;

    // Construtor vazio (obrigatório para funcionar bem com JDBC e frameworks)
    public Conta() {}
    
    // Construtor completo (útil para criar objetos rapidamente)
    public Conta(int id, String usuario, String senha, String perfil) {
        this.id = id;
        this.usuario = usuario;
        this.senha = senha;
        this.perfil = perfil;
    }
    
    // Construtor alternativo (sem o ID, útil para novos cadastros)
    public Conta(String usuario, String senha,String perfil) {
        this.usuario = usuario;
        this.senha = senha;
        this.perfil = perfil;
    }

    // Getters e Setters (não estáticos)
    public String getUsuario() {
        return usuario;
    }
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenha() {
        return senha;
    }
    public void setSenha(String senha) {
        this.senha = senha;
    }
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    
    public String getPerfil() {
        return perfil;
    }
    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }
}