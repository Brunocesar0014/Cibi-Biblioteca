package com.cibi.Service;

import com.cibi.repository.FuncionarioRepository;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.regex.Pattern;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class FuncionarioService {

    private static final Pattern EMAIL = Pattern.compile("[^\\s@]+@[^\\s@.]+(?:\\.[^\\s@.]+)+");
    private static final SecureRandom ALEATORIO = new SecureRandom();

    private FuncionarioService() {
    }

    public static boolean cadastrarPrimeiro(String nome, String email, String telefone,
            String usuario, String senha, String confirmacao) {
        nome = nome.strip();
        email = email.strip();
        usuario = usuario.strip();
        telefone = telefone.strip();

        if (nome.isEmpty() || nome.length() > 150) {
            throw new IllegalArgumentException("Informe um nome completo de até 150 caracteres.");
        }
        if (email.length() > 254 || !EMAIL.matcher(email).matches()) {
            throw new IllegalArgumentException("Informe um e-mail válido.");
        }
        String digitosTelefone = telefone.replaceAll("[^0-9]", "");
        if (!telefone.isEmpty() && (!telefone.matches("[0-9() .-]+")
                || digitosTelefone.length() < 10 || digitosTelefone.length() > 11)) {
            throw new IllegalArgumentException("Informe um telefone com DDD.");
        }
        if (usuario.isEmpty() || usuario.length() > 50 || usuario.codePoints().anyMatch(Character::isWhitespace)) {
            throw new IllegalArgumentException("Informe um usuário sem espaços, com até 50 caracteres.");
        }
        if (senha.isBlank() || senha.length() > 128) {
            throw new IllegalArgumentException("Informe uma senha de até 128 caracteres.");
        }
        if (!senha.equals(confirmacao)) {
            throw new IllegalArgumentException("As senhas não coincidem.");
        }

        byte[] sal = new byte[16];
        ALEATORIO.nextBytes(sal);
        var codificador = Base64.getEncoder();
        String senhaHash = codificador.encodeToString(sal) + ":" + codificador.encodeToString(derivarSenha(senha, sal));
        return FuncionarioRepository.inserirPrimeiro(nome, email, digitosTelefone.isEmpty() ? null : digitosTelefone,
                usuario, senhaHash);
    }

    static String autenticar(String usuario, String senha) {
        var credenciais = FuncionarioRepository.buscarCredenciais(usuario.strip());
        if (credenciais == null) {
            return null;
        }
        String[] partes = credenciais.senhaHash().split(":", 2);
        var decodificador = Base64.getDecoder();
        byte[] sal = decodificador.decode(partes[0]);
        byte[] esperado = decodificador.decode(partes[1]);
        return MessageDigest.isEqual(derivarSenha(senha, sal), esperado) ? credenciais.nome() : null;
    }

    private static byte[] derivarSenha(String senha, byte[] sal) {
        PBEKeySpec parametros = new PBEKeySpec(senha.toCharArray(), sal, 600_000, 256);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(parametros).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Erro ao proteger a senha", e);
        } finally {
            parametros.clearPassword();
        }
    }
}
