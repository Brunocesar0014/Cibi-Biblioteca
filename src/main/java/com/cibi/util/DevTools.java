package com.cibi.util;

public final class DevTools {
    
    private DevTools() {
    }
    
    //public static boolean modoDesenvolvedor = Boolean.parseBoolean(System.getenv("CIBI_DEV"));
    public static boolean modoDesenvolvedor = true;
    
    public static boolean ignorarLogin = false;

    private static boolean iniciarPrimeiroAcesso;
    
    public static boolean isModoDesenvolvedor(){
        return modoDesenvolvedor;
    }
    
    public static void setModoDesenvolvedor(boolean valor){
        modoDesenvolvedor = valor;
    }
    
    public static boolean isIgnorarLogin(){
        return ignorarLogin;
    }
    
    public static void setIgnorarLogin(boolean valor){
        ignorarLogin = valor;
    }

    public static boolean isIniciarPrimeiroAcesso() {
        return iniciarPrimeiroAcesso;
    }

    public static void setIniciarPrimeiroAcesso(boolean valor) {
        iniciarPrimeiroAcesso = valor;
    }
    
}
