package br.com.ifba.sistemaremoto.servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ExecutorComandos {

    // ARRAY COM OS COMANDOS BLOQUEADOS (Requisito Opcional)
    private static final String[] COMANDOS_PROIBIDOS = {"rm ", "shutdown", "reboot", "mkfs", "mv "};

    public String executar(String comando) {

        if (comando == null || comando.trim().isEmpty()) {
            return "ERRO: Comando vazio.";
        }

        // FILTRO DE SEGURANÇA
        String comandoMin = comando.toLowerCase();
        for (String proibido : COMANDOS_PROIBIDOS) {
            if (comandoMin.contains(proibido)) {
                return "ERRO: Comando Negado (Política de Segurança).";
            }
        }

        try {
            ProcessBuilder processBuilder =
                    new ProcessBuilder("bash", "-c", comando);

            // Junta stdout e stderr na mesma saída
            processBuilder.redirectErrorStream(true);

            Process processo = processBuilder.start();

            BufferedReader leitor = new BufferedReader(
                    new InputStreamReader(processo.getInputStream())
            );

            StringBuilder resultado = new StringBuilder();

            String linha;

            while ((linha = leitor.readLine()) != null) {
                resultado.append(linha).append("\n");
            }

            int codigoSaida = processo.waitFor();

            if (resultado.isEmpty()) {
                resultado.append("Comando executado sem saída.\n");
            }

            resultado.append("Código de saída: ")
                    .append(codigoSaida);

            return resultado.toString();

        } catch (IOException e) {
            return "ERRO ao executar comando: " + e.getMessage();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "ERRO: execução do comando foi interrompida.";
        }
    }
}