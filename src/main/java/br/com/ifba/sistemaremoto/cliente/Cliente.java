package br.com.ifba.sistemaremoto.cliente;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {

    private static final String IP_SERVIDOR = "192.168.56.10";
    private static final int PORTA = 9000;

    public static void main(String[] args) {

        try (
                Socket socket = new Socket(IP_SERVIDOR, PORTA);
                BufferedReader entrada = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                PrintWriter saida = new PrintWriter(
                        socket.getOutputStream(), true);
                Scanner teclado = new Scanner(System.in)
        ) {

            System.out.println("Conectado ao servidor!");

            // Recebe a mensagem inicial do servidor
            System.out.println(entrada.readLine());

            // Recebe "Digite seu USUARIO:"
            System.out.println(entrada.readLine());

            // Digita e envia o usuário
            String usuario = teclado.nextLine();
            saida.println(usuario);

            // Recebe "Digite sua SENHA:"
            System.out.println(entrada.readLine());

            // Digita e envia a senha
            String senha = teclado.nextLine();
            saida.println(senha);

            // Recebe a resposta da autenticação
            String resposta = entrada.readLine();
            System.out.println(resposta);

            // Se a autenticação falhar, encerra
            if (resposta.startsWith("ERRO:")) {
                return;
            }

            // Loop para enviar mensagens/comandos
            while (true) {

                System.out.print("> ");
                String mensagem = teclado.nextLine();

                // Cliente usa /exit, mas o servidor espera SAIR
                if ("/exit".equalsIgnoreCase(mensagem.trim())) {
                    saida.println("SAIR");

                    System.out.println(entrada.readLine());
                    break;
                }

                // Envia mensagem para o servidor
                saida.println(mensagem);

                // Mostra resposta do servidor
                System.out.println(entrada.readLine());
            }

        } catch (IOException e) {
            System.err.println("Erro ao conectar ao servidor: " + e.getMessage());
        }
    }
}