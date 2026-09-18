package com.cibi.model;
public class Aluno {
    private long matricula;
    private String nome;
    private String turma;

    public Aluno() {
    }
    
    public Aluno(String nome, String turma, long matricula){
        this.nome = nome;
        this.turma = turma;
        this.matricula = matricula;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getTurma() {
        return turma;
    }
    public void setTurma(String turma) {
        this.turma = turma;
    }
    
    public long getMatricula() {
        return matricula;
    }
    public void setMatricula(long matricula) {
        this.matricula = matricula;
    }
}
