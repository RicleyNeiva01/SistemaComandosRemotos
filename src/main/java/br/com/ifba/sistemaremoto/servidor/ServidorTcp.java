package br.com.ifba.sistemaremoto.servidor;

import java.io.*;
import java.net.*;

public class ServidorTcp {

    private static final Autenticacao servicoAutenticacao = new Autenticacao();

    public static void main(String[] args) {
        int porta = 9000;

        try (ServerSocket servidor = new ServerSocket(porta)) {
            System.out.println("Servidor TCP iniciado e escutando na porta " + porta);

            // Loop infinito para manter o servidor rodando e aceitando novas conexões
            while (true) {
                Socket cliente = servidor.accept();
                System.out.println("Novo cliente conectado: " + cliente.getInetAddress().getHostAddress());

                // Repassa o cliente para o método de atendimento e autenticação
                atenderCliente(cliente);
            }

        } catch (IOException e) {
            System.err.println("Erro ao iniciar o servidor: " + e.getMessage());
        }
    }

    // Método que cuida da autenticação e das mensagens do cliente
    private static void atenderCliente(Socket cliente) {
        try (
                BufferedReader entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                PrintWriter saida = new PrintWriter(cliente.getOutputStream(), true)
        ) {
            // --- 1. ETAPA DE AUTENTICAÇÃO ---
            saida.println("--- BEM-VINDO AO SERVIDOR TCP ---");
            saida.println("Digite seu USUARIO:");
            String usuario = entrada.readLine();

            saida.println("Digite sua SENHA:");
            String senha = entrada.readLine();

            // Usa a classe externa para validar as credenciais
            if (!servicoAutenticacao.autenticar(usuario, senha)) {
                saida.println("ERRO: Usuario ou senha incorretos. Conexao encerrada.");
                System.out.println("Falha de autenticacao para: " + cliente.getInetAddress().getHostAddress());
                return; // Encerra o atendimento e vai direto para o finally fechar a conexão
            }

            // --- 2. ACESSO LIBERADO ---
            saida.println("SUCESSO: Autenticado! Digite 'SAIR' para encerrar a conexao.");
            System.out.println("Usuario '" + usuario + "' autenticado com sucesso.");

            // --- 3. LOOP DE MENSAGENS ---
            String mensagem;
            while ((mensagem = entrada.readLine()) != null) {
                // Permite que o cliente encerre a conexão digitando "SAIR"
                if ("SAIR".equalsIgnoreCase(mensagem.trim())) {
                    saida.println("Sessao encerrada pelo usuario.");
                    break;
                }

                System.out.println("[" + usuario + "] disse: " + mensagem);
                saida.println("Servidor recebeu: " + mensagem);
            }

        } catch (IOException e) {
            System.err.println("Erro na comunicacao com o cliente: " + e.getMessage());
        } finally {
            // Garante que a conexão será fechada mesmo se der erro
            try {
                System.out.println("Cliente desconectado.");
                cliente.close();
            } catch (IOException e) {
                System.err.println("Erro ao fechar a conexao atual.");
            }
        }
    }
}