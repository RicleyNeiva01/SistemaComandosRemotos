package br.com.ifba.sistemaremoto.servidor;

public class Autenticacao {

    private static final String USUARIO_CORRETO = "admin";
    private static final String SENHA_CORRETA = "1234";

    public boolean autenticar(String usuario, String senha) {
        if (usuario == null || senha == null) {
            return false;
        }
        return USUARIO_CORRETO.equals(usuario) && SENHA_CORRETA.equals(senha);
    }
}
