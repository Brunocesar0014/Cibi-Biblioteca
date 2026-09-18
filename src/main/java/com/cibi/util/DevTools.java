package com.cibi.util;

public final class DevTools {
    
    private DevTools() {
    }
    
    public static boolean modoDesenvolvedor = true;
    
    public static boolean ignorarLogin = false;
    
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
    
}
