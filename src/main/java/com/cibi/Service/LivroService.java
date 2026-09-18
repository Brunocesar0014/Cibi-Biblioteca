package com.cibi.Service;

import com.cibi.model.Livro;
import com.cibi.repository.LivroRepository;

public class LivroService {
    
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
