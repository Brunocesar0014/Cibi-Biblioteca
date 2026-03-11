package com.cibi.model;
public class Aluno {
    private String nome, turma;
    private long matricula;
    
    public void Aluno(String nome, String turma, long matricula){
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
