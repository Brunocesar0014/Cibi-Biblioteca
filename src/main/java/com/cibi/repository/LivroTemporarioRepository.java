package com.cibi.repository;

import com.cibi.model.Aluno;
import com.cibi.model.Emprestimo;
import com.cibi.model.Livro;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Acervo e circulação compartilhados durante a execução, sob o mesmo bloqueio. */
public final class LivroTemporarioRepository {
    private static final Map<Integer, Livro> LIVROS = new LinkedHashMap<>();
    private static final Map<Integer, Emprestimo> EMPRESTIMOS = new LinkedHashMap<>();
    private static int proximoId = 1;
    private static int proximoEmprestimo = 1;

    public List<Livro> listar() {
        synchronized (LIVROS) {
            return LIVROS.values().stream().map(Livro::new).toList();
        }
    }

    public Livro buscarPorId(int id) {
        synchronized (LIVROS) {
            Livro livro = LIVROS.get(id);
            return livro == null ? null : new Livro(livro);
        }
    }

    public void inserir(Livro livro) {
        synchronized (LIVROS) {
            Livro copia = new Livro(livro);
            copia.setId(proximoId++);
            LIVROS.put(copia.getId(), copia);
            livro.setId(copia.getId());
        }
    }

    public void atualizar(Livro livro) {
        synchronized (LIVROS) {
            if (!LIVROS.containsKey(livro.getId())) throw new IllegalStateException("Livro não encontrado.");
            long emprestados = EMPRESTIMOS.values().stream()
                    .filter(e -> e.livroId() == livro.getId() && e.devolvidoEm() == null)
                    .count();
            if (livro.getQuantidadeDisponivel() + emprestados > livro.getQuantidadeTotal()) {
                throw new IllegalStateException("A quantidade total deve comportar os exemplares disponíveis e emprestados.");
            }
            LIVROS.put(livro.getId(), new Livro(livro));
        }
    }

    public void excluir(int id) {
        synchronized (LIVROS) {
            if (EMPRESTIMOS.values().stream().anyMatch(e -> e.livroId() == id && e.devolvidoEm() == null)) {
                throw new IllegalStateException("Registre a devolução dos exemplares antes de excluir este livro.");
            }
            if (LIVROS.remove(id) == null) throw new IllegalStateException("Livro não encontrado.");
        }
    }

    public List<Emprestimo> listarEmprestimos() {
        synchronized (LIVROS) {
            return List.copyOf(EMPRESTIMOS.values());
        }
    }

    public void registrarEmprestimo(Aluno aluno, int livroId, String colaborador, LocalDate retirada,
                                    LocalDate prevista, int limiteLivros, boolean bloquearAtraso) {
        synchronized (LIVROS) {
            Livro livro = LIVROS.get(livroId);
            if (livro == null || livro.getQuantidadeDisponivel() <= 0 || !"Disponível".equals(livro.getStatus())) {
                throw new IllegalStateException("O livro selecionado não está disponível para empréstimo.");
            }

            List<Emprestimo> ativos = EMPRESTIMOS.values().stream()
                    .filter(e -> e.matriculaAluno() == aluno.getMatricula() && e.devolvidoEm() == null)
                    .toList();
            if (bloquearAtraso && ativos.stream().anyMatch(e -> e.statusEm(retirada) == Emprestimo.Status.ATRASADO)) {
                throw new IllegalStateException("O aluno tem devoluções em atraso.");
            }
            if (ativos.size() >= limiteLivros) {
                throw new IllegalStateException("O aluno atingiu o limite de livros emprestados.");
            }

            int id = proximoEmprestimo++;
            Emprestimo emprestimo = new Emprestimo(id, aluno.getMatricula(), aluno.getNome(), livroId,
                    livro.getTitulo(), livro.getAutor(), colaborador, retirada, prevista, null);
            livro.setQuantidadeDisponivel(livro.getQuantidadeDisponivel() - 1);
            if (livro.getQuantidadeDisponivel() == 0) livro.setStatus("Indisponível");
            EMPRESTIMOS.put(id, emprestimo);
        }
    }

    public void registrarDevolucao(int id, LocalDate data) {
        synchronized (LIVROS) {
            Emprestimo emprestimo = EMPRESTIMOS.get(id);
            if (emprestimo == null) throw new IllegalStateException("Empréstimo não encontrado.");
            if (emprestimo.devolvidoEm() != null) throw new IllegalStateException("A devolução já foi registrada.");

            Livro livro = LIVROS.get(emprestimo.livroId());
            livro.setQuantidadeDisponivel(livro.getQuantidadeDisponivel() + 1);
            if ("Indisponível".equals(livro.getStatus())) livro.setStatus("Disponível");
            EMPRESTIMOS.put(id, emprestimo.devolverEm(data));
        }
    }
}
