package br.com.ifba.sistemaremoto.servidor;

import java.io.*;
import java.net.*;

public class ServidorTcp {

    public static void main(String[] args) {
        int porta = 8080;

        try (ServerSocket servidor = new ServerSocket(porta)) {
            System.out.println("Servidor TCP iniciado e escutando na porta " + porta);

            // Loop infinito para manter o servidor rodando e aceitando novas conexões
            while (true) {
                // O método accept() bloqueia a execução até que um cliente se conecte
                Socket cliente = servidor.accept();
                System.out.println("Novo cliente conectado: " + cliente.getInetAddress().getHostAddress());

                // Streams para ler e enviar dados para o cliente
                BufferedReader entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                PrintWriter saida = new PrintWriter(cliente.getOutputStream(), true);

                String mensagem;
                // Lê as mensagens do cliente linha por linha
                while ((mensagem = entrada.readLine()) != null) {
                    System.out.println("Cliente disse: " + mensagem);

                    // Envia a resposta de volta ao cliente
                    saida.println("Servidor recebeu: " + mensagem);
                }

                System.out.println("Cliente desconectado.");
                cliente.close(); // Fecha a conexão atual
            }

        } catch (IOException e) {
            System.err.println("Erro ao iniciar o servidor: " + e.getMessage());
        }
    }
}