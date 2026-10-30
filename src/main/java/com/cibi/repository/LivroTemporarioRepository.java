package com.cibi.repository;

import com.cibi.model.Livro;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Acervo compartilhado apenas durante esta execução. Não acessa o banco SQL. */
public final class LivroTemporarioRepository {
    private static final Map<Integer, Livro> LIVROS = new LinkedHashMap<>();
    private static int proximoId = 1;

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
            LIVROS.put(livro.getId(), new Livro(livro));
        }
    }

    public void excluir(int id) {
        synchronized (LIVROS) {
            if (LIVROS.remove(id) == null) throw new IllegalStateException("Livro não encontrado.");
        }
    }
}
