package com.cibi.Service;

import com.cibi.model.Aluno;
import com.cibi.repository.AlunoRepository;
import java.util.List;
import java.util.Map;

public class AlunoService {
    
    public static Aluno BuscarAlunoPorMaticula(long matricula){
        if (matricula <= 0 ){
            throw new IllegalArgumentException("matrícula deve ser positiva");
        }
        
        Aluno aluno = AlunoRepository.buscarPorMatricula(matricula);
        
        if (aluno == null) {
            throw new IllegalStateException("Aluno não encontrado");
        }
        
        return aluno;
    }

    public static byte[] buscarFotoCida(Aluno aluno) {
        return AlunoRepository.buscarFotoCida(aluno.getMatricula());
    }

    public static Map<Long, byte[]> buscarFotosCida(List<Long> matriculas) {
        return AlunoRepository.buscarFotosCida(matriculas);
    }
}
