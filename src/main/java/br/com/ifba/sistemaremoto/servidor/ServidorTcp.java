package br.com.ifba.sistemaremoto.servidor;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ServidorTcp {

    private static final int PORTA = 9000;

    private static final Autenticacao servicoAutenticacao =
            new Autenticacao();

    private static final ExecutorComandos executorComandos =
            new ExecutorComandos();

    public static void main(String[] args) {

        try (ServerSocket servidor = new ServerSocket(PORTA)) {

            System.out.println("========================================");
            System.out.println("       SERVIDOR TCP INICIADO");
            System.out.println("========================================");
            System.out.println("Porta: " + PORTA);
            System.out.println("Aguardando conexoes...");
            System.out.println();

            // Mantém o servidor funcionando continuamente
            while (true) {

                Socket cliente = servidor.accept();

                System.out.println("----------------------------------------");
                System.out.println(
                        "Novo cliente conectado: "
                                + cliente.getInetAddress().getHostAddress()
                );

                atenderCliente(cliente);
            }

        } catch (IOException e) {

            System.err.println(
                    "Erro ao iniciar o servidor: " + e.getMessage()
            );
        }
    }

    /**
     * Responsável por autenticar o cliente
     * e processar os comandos recebidos.
     */
    private static void atenderCliente(Socket cliente) {

        String enderecoCliente =
                cliente.getInetAddress().getHostAddress();

        try (
                BufferedReader entrada =
                        new BufferedReader(
                                new InputStreamReader(
                                        cliente.getInputStream()
                                )
                        );

                PrintWriter saida =
                        new PrintWriter(
                                cliente.getOutputStream(),
                                true
                        )
        ) {

            // ========================================
            // 1. AUTENTICAÇÃO
            // ========================================

            saida.println("--- BEM-VINDO AO SERVIDOR TCP ---");
            saida.println("Digite seu USUARIO:");

            String usuario = entrada.readLine();

            if (usuario == null) {
                return;
            }

            saida.println("Digite sua SENHA:");

            String senha = entrada.readLine();

            if (senha == null) {
                return;
            }

            // Verifica usuário e senha
            if (!servicoAutenticacao.autenticar(usuario, senha)) {

                saida.println(
                        "ERRO: Usuario ou senha incorretos. Conexao encerrada."
                );

                System.out.println(
                        "Falha de autenticacao para: "
                                + enderecoCliente
                );

                return;
            }

            // ========================================
            // 2. AUTENTICAÇÃO BEM-SUCEDIDA
            // ========================================

            saida.println(
                    "SUCESSO: Autenticado! "
                            + "Digite 'SAIR' para encerrar a conexao."
            );

            System.out.println(
                    "Usuario '" + usuario
                            + "' autenticado com sucesso."
            );

            // ========================================
            // CONFIGURAÇÃO DO LOG DA SESSÃO (Ponto Extra)
            // ========================================
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            // Troca os pontos do IP por hifens para evitar problemas com nomes de arquivos no sistema operacional
            String ipFormatado = enderecoCliente.replace(".", "-");
            String arquivoLog = "log_cliente_" + ipFormatado + "_" + timestamp + ".txt";

            System.out.println("Log da sessão será salvo em: " + arquivoLog);

            // ========================================
            // 3. LOOP DE COMANDOS
            // ========================================

            String mensagem;

            while ((mensagem = entrada.readLine()) != null) {

                mensagem = mensagem.trim();

                // Ignora mensagem vazia
                if (mensagem.isEmpty()) {
                    saida.println("ERRO: Comando vazio.");
                    saida.println("FIM_RESPOSTA");
                    continue;
                }

                // ====================================
                // ENCERRAMENTO
                // ====================================

                if ("SAIR".equalsIgnoreCase(mensagem)) {

                    saida.println(
                            "Sessao encerrada pelo usuario."
                    );

                    saida.println("FIM_RESPOSTA");

                    System.out.println(
                            "Usuario '" + usuario
                                    + "' encerrou a sessao."
                    );

                    break;
                }

                // ====================================
                // EXIBE O COMANDO NO SERVIDOR
                // ====================================

                System.out.println(
                        "[" + usuario + "] Comando: "
                                + mensagem
                );

                // ====================================
                // EXECUTA O COMANDO
                // ====================================

                String resultado =
                        executorComandos.executar(mensagem);

                // ====================================
                // SALVA NO ARQUIVO DE LOG
                // ====================================

                try (FileWriter logWriter = new FileWriter(arquivoLog, true)) {
                    logWriter.write("Comando: " + mensagem + "\n");
                    logWriter.write("Saída:\n" + resultado + "\n");
                    logWriter.write("----------------------------------------\n");
                } catch (IOException e) {
                    System.err.println("Erro ao salvar no arquivo de log: " + e.getMessage());
                }

                // ====================================
                // ENVIA RESULTADO PARA O CLIENTE
                // ====================================

                String[] linhas = resultado.split("\n", -1);

                for (String linha : linhas) {
                    saida.println(linha);
                }

                // Marcador que informa ao cliente
                // que a resposta terminou
                saida.println("FIM_RESPOSTA");

                // Exibe o resultado também no servidor
                System.out.println(
                        "Resultado do comando enviado ao cliente."
                );

                System.out.println("----------------------------------------");
            }

        } catch (IOException e) {

            System.err.println(
                    "Erro na comunicacao com o cliente: "
                            + e.getMessage()
            );

        } finally {

            try {

                cliente.close();

                System.out.println(
                        "Cliente desconectado: "
                                + enderecoCliente
                );

                System.out.println();

            } catch (IOException e) {

                System.err.println(
                        "Erro ao fechar a conexao atual: "
                                + e.getMessage()
                );
            }
        }
    }
}