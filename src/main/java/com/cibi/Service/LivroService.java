package com.cibi.Service;

import com.cibi.model.Livro;
import com.cibi.repository.LivroRepository;
import java.util.List;

public class LivroService {

    private final LivroRepository repository = new LivroRepository();

    public List<Livro> listarLivros() {
        return repository.listar();
    }

    public void inserirLivro(Livro livro) {
        validar(livro);
        repository.inserir(livro);
    }

    public void atualizarLivro(Livro livro) {
        validar(livro);
        if (livro.getId() <= 0) throw new IllegalArgumentException("id inválido");
        repository.atualizar(livro);
    }

    public void excluirLivro(int id) {
        if (id <= 0) throw new IllegalArgumentException("id inválido");
        repository.excluir(id);
    }

    private void validar(Livro livro) {
        if (livro == null) throw new IllegalArgumentException("livro obrigatório");
        if (livro.getTitulo() == null || livro.getTitulo().isBlank()) {
            throw new IllegalArgumentException("título obrigatório");
        }
        if (livro.getTitulo().length() > 100) throw new IllegalArgumentException("título muito longo");
        if (livro.getAutor() != null && livro.getAutor().length() > 100) {
            throw new IllegalArgumentException("autor muito longo");
        }
    }
    
    public static Livro BuscarLivro(Livro livro) {
        if (livro == null) {
            throw new IllegalArgumentException("livro obrigatório");
        }
        if(livro.getId() <= 0) {
            throw new IllegalArgumentException("id inválido");
        }
        
        Livro l = LivroRepository.buscarPorId(livro.getId());
        
        if(l == null) {
            throw new IllegalStateException("Livro não encontrado");
        }
        
        return l;
    }
    
    public static void InserirLivro(Livro livro) {
        if (livro == null) {
            throw new IllegalArgumentException("livro obrigatório");
        }
        if (livro.getTitulo() == null || livro.getTitulo().isBlank()) {
            throw new IllegalArgumentException("título obrigatório");
        }
        
        try {
            LivroRepository repository = new LivroRepository();
            repository.inserir(livro);
        } catch (RuntimeException e) {
            throw new RuntimeException("Erro ao inserir livro", e);
        }
        
    }
}
