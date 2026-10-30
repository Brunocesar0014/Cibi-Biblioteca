package com.cibi.model;

public class Livro {
    private int id;
    private String titulo;
    private String autor;
    private String sinopse;
    // Campos provisórios de catalogação; ainda não fazem parte do esquema SQL.
    private String subtitulo;
    private String editora;
    private String categoria;
    private String isbn;
    private Integer anoPublicacao;
    private String edicao;
    private String volume;
    private Integer numeroPaginas;
    private String idioma;
    private String localPublicacao;
    private String assuntos;
    private String numeroChamada;
    private String localizacao;
    private int quantidadeTotal = 1;
    private int quantidadeDisponivel = 1;
    private String status = "Disponível";

    public Livro() {}

    public Livro(int id, String titulo, String autor, String sinopse) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.sinopse = sinopse;
    }

    public Livro(String titulo, String autor, String sinopse) {
        this(0, titulo, autor, sinopse);
    }

    public Livro(Livro outro) {
        this(outro.id, outro.titulo, outro.autor, outro.sinopse);
        subtitulo = outro.subtitulo;
        editora = outro.editora;
        categoria = outro.categoria;
        isbn = outro.isbn;
        anoPublicacao = outro.anoPublicacao;
        edicao = outro.edicao;
        volume = outro.volume;
        numeroPaginas = outro.numeroPaginas;
        idioma = outro.idioma;
        localPublicacao = outro.localPublicacao;
        assuntos = outro.assuntos;
        numeroChamada = outro.numeroChamada;
        localizacao = outro.localizacao;
        quantidadeTotal = outro.quantidadeTotal;
        quantidadeDisponivel = outro.quantidadeDisponivel;
        status = outro.status;
    }

    public int getId() { return id; }
    public void setId(int valor) { id = valor; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String valor) { titulo = valor; }
    public String getAutor() { return autor; }
    public void setAutor(String valor) { autor = valor; }
    public String getSinopse() { return sinopse; }
    public void setSinopse(String valor) { sinopse = valor; }
    public String getSubtitulo() { return subtitulo; }
    public void setSubtitulo(String valor) { subtitulo = valor; }
    public String getEditora() { return editora; }
    public void setEditora(String valor) { editora = valor; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String valor) { categoria = valor; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String valor) { isbn = valor; }
    public Integer getAnoPublicacao() { return anoPublicacao; }
    public void setAnoPublicacao(Integer valor) { anoPublicacao = valor; }
    public String getEdicao() { return edicao; }
    public void setEdicao(String valor) { edicao = valor; }
    public String getVolume() { return volume; }
    public void setVolume(String valor) { volume = valor; }
    public Integer getNumeroPaginas() { return numeroPaginas; }
    public void setNumeroPaginas(Integer valor) { numeroPaginas = valor; }
    public String getIdioma() { return idioma; }
    public void setIdioma(String valor) { idioma = valor; }
    public String getLocalPublicacao() { return localPublicacao; }
    public void setLocalPublicacao(String valor) { localPublicacao = valor; }
    public String getAssuntos() { return assuntos; }
    public void setAssuntos(String valor) { assuntos = valor; }
    public String getNumeroChamada() { return numeroChamada; }
    public void setNumeroChamada(String valor) { numeroChamada = valor; }
    public String getLocalizacao() { return localizacao; }
    public void setLocalizacao(String valor) { localizacao = valor; }
    public int getQuantidadeTotal() { return quantidadeTotal; }
    public void setQuantidadeTotal(int valor) { quantidadeTotal = valor; }
    public int getQuantidadeDisponivel() { return quantidadeDisponivel; }
    public void setQuantidadeDisponivel(int valor) { quantidadeDisponivel = valor; }
    public String getStatus() { return status; }
    public void setStatus(String valor) { status = valor; }
}
