package com.cibi.model;
public class Livro {
    
    private int id;
    private String titulo;
    private String autor;
    private String sinopse;
    
    //construtor vazio
    public Livro(){}
    
    //construtor completo
    public Livro(int id, String titulo, String autor, String sinopse){
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.sinopse = sinopse;
    }
    
    //construtor sem id(para cadastros)
    public Livro(String titulo, String autor, String sinopse){
        this.titulo = titulo;
        this.autor = autor;
        this.sinopse = sinopse;
    }
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }
    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getSinopse() {
        return sinopse;
    }
    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    
    
    
    
    
}
