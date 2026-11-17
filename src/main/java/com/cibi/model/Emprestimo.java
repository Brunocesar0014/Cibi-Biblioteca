package com.cibi.model;

import java.time.LocalDate;

public record Emprestimo(int id, long matriculaAluno, String aluno, int livroId, String livro,
                         String autor, String colaborador, LocalDate retirada,
                         LocalDate devolucaoPrevista, LocalDate devolvidoEm) {

    public enum Status {
        EM_ANDAMENTO("Em andamento", "em-andamento"),
        ATRASADO("Atrasado", "atrasado"),
        DEVOLVIDO("Devolvido", "devolvido");

        private final String rotulo;
        private final String classe;

        Status(String rotulo, String classe) {
            this.rotulo = rotulo;
            this.classe = classe;
        }

        public String classe() {
            return classe;
        }

        @Override
        public String toString() {
            return rotulo;
        }
    }

    public Status statusEm(LocalDate hoje) {
        if (devolvidoEm != null) return Status.DEVOLVIDO;
        return devolucaoPrevista.isBefore(hoje) ? Status.ATRASADO : Status.EM_ANDAMENTO;
    }

    public Emprestimo devolverEm(LocalDate data) {
        return new Emprestimo(id, matriculaAluno, aluno, livroId, livro, autor,
                colaborador, retirada, devolucaoPrevista, data);
    }
}
