package com.cibi.Service;

import com.cibi.model.Livro;
import com.cibi.repository.LivroRepository;

public class LivroService {
    
    public static Livro BuscarLivro(Livro livro) {
        if(livro.getId() == 0) {
            throw new IllegalArgumentException("id inválido");
        }
        
        Livro l = new Livro();
        l = LivroRepository.buscarPorId(livro.getId());
        
        if(l.getTitulo() == null) {
            throw new IllegalStateException("Livro não encontrado");
        }
        
        return l;
        
    }
}
