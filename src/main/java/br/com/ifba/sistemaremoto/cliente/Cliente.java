package br.com.ifba.sistemaremoto.cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {

    private static final String IP_SERVIDOR = "192.168.56.10";
    private static final int PORTA = 9000;

    public static void main(String[] args) {

        try (
                Socket socket = new Socket(IP_SERVIDOR, PORTA);

                BufferedReader entrada = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );

                PrintWriter saida = new PrintWriter(
                        socket.getOutputStream(), true
                );

                Scanner teclado = new Scanner(System.in)
        ) {

            // ========================================
            // CABEÇALHO
            // ========================================

            System.out.println();
            System.out.println("========================================");
            System.out.println("     SISTEMA DE COMANDOS REMOTOS");
            System.out.println("========================================");
            System.out.println();

            // ========================================
            // 1. RECEBE MENSAGEM DE BOAS-VINDAS
            // ========================================

            System.out.println(entrada.readLine());
            System.out.println(entrada.readLine());

            // ========================================
            // 2. USUÁRIO
            // ========================================

            System.out.print("Usuário: ");
            String usuario = teclado.nextLine();

            saida.println(usuario);

            // ========================================
            // 3. SENHA
            // ========================================

            System.out.println(entrada.readLine());

            System.out.print("Senha: ");
            String senha = teclado.nextLine();

            saida.println(senha);

            // ========================================
            // 4. RESPOSTA DA AUTENTICAÇÃO
            // ========================================

            String respostaLogin = entrada.readLine();

            System.out.println();
            System.out.println(respostaLogin);

            // Se a autenticação falhar, encerra
            if (respostaLogin == null
                    || respostaLogin.startsWith("ERRO:")) {

                System.out.println();
                System.out.println("Conexão encerrada.");

                return;
            }

            // ========================================
            // 5. TERMINAL DE COMANDOS
            // ========================================

            System.out.println();
            System.out.println("========================================");
            System.out.println("        TERMINAL REMOTO");
            System.out.println("========================================");
            System.out.println();

            while (true) {

                System.out.print("remoto> ");

                String comando = teclado.nextLine().trim();

                // ====================================
                // ENCERRAR CLIENTE
                // ====================================

                if ("/exit".equalsIgnoreCase(comando)) {

                    // O servidor espera "SAIR"
                    saida.println("SAIR");

                    // Lê a resposta do servidor
                    lerResposta(entrada);

                    System.out.println();
                    System.out.println("Conexão encerrada.");

                    break;
                }

                // ====================================
                // NÃO ENVIA COMANDO VAZIO
                // ====================================

                if (comando.isEmpty()) {
                    System.out.println("Digite um comando.");
                    continue;
                }

                // ====================================
                // ENVIA COMANDO AO SERVIDOR
                // ====================================

                saida.println(comando);

                // ====================================
                // RECEBE RESULTADO COMPLETO
                // ====================================

                lerResposta(entrada);
            }

        } catch (IOException e) {

            System.err.println();
            System.err.println(
                    "Erro ao conectar ao servidor: "
                            + e.getMessage()
            );
        }
    }

    /**
     * Lê todas as linhas enviadas pelo servidor
     * até encontrar o marcador FIM_RESPOSTA.
     */
    private static void lerResposta(BufferedReader entrada)
            throws IOException {

        String linha;

        while ((linha = entrada.readLine()) != null) {

            if ("FIM_RESPOSTA".equals(linha)) {
                break;
            }

            System.out.println(linha);
        }
    }
}
