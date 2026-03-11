package com.cibi.Service;

import com.cibi.model.Aluno;
import com.cibi.repository.AlunoRepository;

public class AlunoService {
    
    public static Aluno BuscarAlunoPorMaticula(long matricula){
        if (matricula == 0 ){
            throw new IllegalArgumentException("matricula não pode ser 0");
        }
        
        AlunoRepository dao = new AlunoRepository();
        
        Aluno aluno = dao.buscarPorMatricula(matricula);
        
        if (aluno.getNome() == null) {
            throw new IllegalStateException("Aluno não encontrado");
        }
        
        return aluno;
    }
}
