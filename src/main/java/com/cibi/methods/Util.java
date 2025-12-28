package com.cibi.methods;

//mostrar painel
import javax.swing.JPanel;
import java.awt.BorderLayout;

public class Util {
    
    public void MostrarPainel(JPanel p, JPanel conteudo) {
        p.setSize(980, 700);//315,194
        p.setLocation(0,0);
        conteudo.removeAll();
        conteudo.add(p, BorderLayout.CENTER);
        conteudo.revalidate();
        conteudo.repaint();
    }
    
}
