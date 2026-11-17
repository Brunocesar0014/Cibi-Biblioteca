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
    private CheckBox checkIniciarPrimeiroAcesso;
    
    @FXML
    private TextArea txtLog;
    
    private Runnable iniciarAplicacaoCallback;

    @FXML
    private void initialize() {
        checkIgnorarLogin.disableProperty().bind(checkIniciarPrimeiroAcesso.selectedProperty());
    }
    
    public void setIniciarAplicacaoCallback(Runnable callback) {
        this.iniciarAplicacaoCallback = callback;
    }
    
    @FXML
    private void iniciarCIBI() {
        
        DevTools.setIniciarPrimeiroAcesso(checkIniciarPrimeiroAcesso.isSelected());
        DevTools.setIgnorarLogin(!checkIgnorarLogin.isDisabled() && checkIgnorarLogin.isSelected());
        
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
