package com.cibi.methods;

//classes Java
import javax.swing.table.DefaultTableModel;
import java.util.List;

//objetos
import com.cibi.model.bean.Livro;
import com.cibi.model.bean.Conta;

public class Tabelas {
    public static DefaultTableModel montarTabelaLivros(List<Livro> lista) {

        String[] colunas = {"ID", "Título", "Autor"};

        DefaultTableModel model = new DefaultTableModel(colunas, 0);

        for (Livro l : lista) {
            
            String autor = (l.getAutor() == null || l.getAutor().trim().isEmpty()) 
                            ? "-" 
                            : l.getAutor();
            
            Object[] linha = {
                l.getId(),
                l.getTitulo(),
                autor
            };
            model.addRow(linha);
        }

        return model;
    }
    
    public static DefaultTableModel montarTabelaContas(List<Conta> lista) {

        String[] colunas = {"ID", "Usuario", "Perfil"};

        DefaultTableModel model = new DefaultTableModel(colunas, 0);

        for (Conta c : lista) {
            
            Object[] linha = {
                c.getId(),
                c.getUsuario(),
                c.getPerfil()
            };
            model.addRow(linha);
        }

        return model;
    }
}
