package br.com.ifba.sistemaremoto.servidor;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class ServidorTcp {

    private static final Autenticacao servicoAutenticacao =
            new Autenticacao();

    private static final ExecutorComandos executorComandos =
            new ExecutorComandos();

    public static void main(String[] args) {

        Scanner teclado = new Scanner(System.in);

        // ========================================
        // CONFIGURAÇÃO DO SERVIDOR
        // ========================================

        System.out.print("Digite o IP do servidor: ");
        String ipServidor = teclado.nextLine();

        System.out.print("Digite a porta do servidor: ");
        int porta = Integer.parseInt(teclado.nextLine());

        try (
                ServerSocket servidor = new ServerSocket(
                        porta,
                        50,
                        InetAddress.getByName(ipServidor)
                )
        ) {

            System.out.println("========================================");
            System.out.println("       SERVIDOR TCP INICIADO");
            System.out.println("========================================");
            System.out.println("IP: " + ipServidor);
            System.out.println("Porta: " + porta);
            System.out.println("Aguardando conexoes...");
            System.out.println();

            while (true) {

                Socket cliente = servidor.accept();

                System.out.println("----------------------------------------");
                System.out.println(
                        "Novo cliente conectado: "
                                + cliente.getInetAddress()
                                .getHostAddress()
                );

                atenderCliente(cliente);
            }

        } catch (NumberFormatException e) {

            System.err.println();
            System.err.println(
                    "Erro: a porta deve ser um número."
            );

        } catch (IOException e) {

            System.err.println(
                    "Erro ao iniciar o servidor: "
                            + e.getMessage()
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

        BufferedWriter logWriter = null;

        try (
                BufferedReader entrada =
                        new BufferedReader(
                                new InputStreamReader(
                                        cliente.getInputStream(),
                                        StandardCharsets.UTF_8
                                )
                        );

                PrintWriter saida =
                        new PrintWriter(
                                new OutputStreamWriter(
                                        cliente.getOutputStream(),
                                        StandardCharsets.UTF_8
                                ),
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

            // ========================================
            // VERIFICA USUÁRIO E SENHA
            // ========================================

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
            // 3. CRIAÇÃO DO LOG DA SESSÃO
            // ========================================

            Path pastaLogs =
                    Paths.get("logs");

            Files.createDirectories(pastaLogs);

            String timestamp =
                    LocalDateTime.now()
                            .format(
                                    DateTimeFormatter.ofPattern(
                                            "yyyyMMdd_HHmmss_SSS"
                                    )
                            );

            String ipFormatado =
                    enderecoCliente.replace(".", "-");

            String nomeArquivo =
                    "log_cliente_"
                            + ipFormatado
                            + "_"
                            + timestamp
                            + ".txt";

            Path caminhoLog =
                    pastaLogs.resolve(nomeArquivo);

            logWriter =
                    Files.newBufferedWriter(
                            caminhoLog,
                            StandardCharsets.UTF_8
                    );

            // Cabeçalho do log
            logWriter.write("========================================");
            logWriter.newLine();
            logWriter.write("       LOG DE SESSAO REMOTA");
            logWriter.newLine();
            logWriter.write("========================================");
            logWriter.newLine();
            logWriter.write("Cliente: " + enderecoCliente);
            logWriter.newLine();
            logWriter.write("Usuario: " + usuario);
            logWriter.newLine();
            logWriter.write(
                    "Inicio: "
                            + LocalDateTime.now()
            );
            logWriter.newLine();
            logWriter.write("========================================");
            logWriter.newLine();
            logWriter.newLine();

            logWriter.flush();

            System.out.println(
                    "Log da sessão criado em: "
                            + caminhoLog
            );

            // ========================================
            // 4. LOOP DE COMANDOS
            // ========================================

            String mensagem;

            while ((mensagem = entrada.readLine()) != null) {

                mensagem = mensagem.trim();

                // ====================================
                // COMANDO VAZIO
                // ====================================

                if (mensagem.isEmpty()) {

                    String resultado =
                            "ERRO: Comando vazio.";

                    saida.println(resultado);
                    saida.println("FIM_RESPOSTA");

                    registrarLog(
                            logWriter,
                            mensagem,
                            resultado
                    );

                    continue;
                }

                // ====================================
                // ENCERRAMENTO
                // ====================================

                if ("SAIR".equalsIgnoreCase(mensagem)) {

                    String resultado =
                            "Sessao encerrada pelo usuario.";

                    saida.println(resultado);
                    saida.println("FIM_RESPOSTA");

                    registrarLog(
                            logWriter,
                            "SAIR",
                            resultado
                    );

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
                // EXECUTA / FILTRA O COMANDO
                // ====================================

                String resultado =
                        executorComandos.executar(mensagem);

                // ====================================
                // SALVA COMANDO E SAÍDA NO LOG
                // ====================================

                registrarLog(
                        logWriter,
                        mensagem,
                        resultado
                );

                // ====================================
                // ENVIA RESULTADO PARA O CLIENTE
                // ====================================

                String[] linhas =
                        resultado.split("\n", -1);

                for (String linha : linhas) {

                    saida.println(linha);
                }

                saida.println("FIM_RESPOSTA");

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

            // ========================================
            // FECHA O LOG
            // ========================================

            if (logWriter != null) {

                try {

                    logWriter.write("----------------------------------------");
                    logWriter.newLine();
                    logWriter.write(
                            "Fim da sessão: "
                                    + LocalDateTime.now()
                    );
                    logWriter.newLine();
                    logWriter.close();

                } catch (IOException e) {

                    System.err.println(
                            "Erro ao fechar arquivo de log: "
                                    + e.getMessage()
                    );
                }
            }

            // ========================================
            // FECHA A CONEXÃO
            // ========================================

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

    /**
     * Registra o comando e sua saída no arquivo
     * de log da sessão.
     */
    private static void registrarLog(
            BufferedWriter logWriter,
            String comando,
            String resultado
    ) {

        if (logWriter == null) {
            return;
        }

        try {

            logWriter.write("========================================");
            logWriter.newLine();

            logWriter.write(
                    "Data/Hora: "
                            + LocalDateTime.now()
            );
            logWriter.newLine();

            logWriter.write("Comando:");
            logWriter.newLine();

            logWriter.write(comando);
            logWriter.newLine();

            logWriter.newLine();

            logWriter.write("Saída:");
            logWriter.newLine();

            logWriter.write(resultado);
            logWriter.newLine();

            logWriter.newLine();

            logWriter.flush();

        } catch (IOException e) {

            System.err.println(
                    "Erro ao salvar no arquivo de log: "
                            + e.getMessage()
            );
        }
    }
}