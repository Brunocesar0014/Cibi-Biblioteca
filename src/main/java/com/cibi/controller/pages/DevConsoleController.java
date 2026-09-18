package com.cibi.controller.pages;

import com.cibi.util.DevTools;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;

public class DevConsoleController {
    
    @FXML
    private CheckBox checkIgnorarLogin;
    
    @FXML
    private TextArea txtLog;
    
    private Runnable iniciarAplicacaoCallback;
    
    public void setIniciarAplicacaoCallback(Runnable callback) {
        this.iniciarAplicacaoCallback = callback;
    }
    
    @FXML
    private void iniciarCIBI() {
        
        DevTools.setIgnorarLogin(checkIgnorarLogin.isSelected());
        
        adicionarLog("Configurações aplicadas");
        adicionarLog("Iniciando CIBI...");
        
        if (iniciarAplicacaoCallback != null) {
            iniciarAplicacaoCallback.run();
        }
    }
    
    @FXML
    private void encerrar() {
        Platform.exit();
    }
    
    private void adicionarLog(String mensagem) {
        txtLog.appendText(mensagem + "\n");
    }
    
}
