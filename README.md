Sistema de Comandos Remotos

Sistema cliente/servidor desenvolvido em Java para simular uma sessão de
terminal remoto utilizando Sockets TCP. O projeto foi desenvolvido
como atividade da disciplina de Redes de Computadores, utilizando duas
máquinas virtuais Debian no VirtualBox.

1. Objetivo

O sistema permite que um cliente se conecte a um servidor por meio de
uma conexão TCP, realize autenticação e, após o acesso ser autorizado,
envie comandos para serem executados no sistema operacional do servidor.

A saída dos comandos é enviada de volta ao cliente através do mesmo
socket.

2. Tecnologias utilizadas

Java

Maven

Sockets TCP

Debian Linux

VirtualBox

Git/GitHub

3. Arquitetura

O projeto utiliza a arquitetura Cliente/Servidor:

+------------------+              TCP               +------------------+
|     CLIENTE      | ----------------------------> |     SERVIDOR     |
| 192.168.10.2     |          porta 9000           | 192.168.10.1     |
|                  | <---------------------------- |                  |
| Envia comandos   |        Saída dos comandos     | Executa comandos |
+------------------+                               +------------------+

As máquinas virtuais utilizam uma rede interna do VirtualBox chamada
internet.

4. Configuração das máquinas

Servidor

Sistema: Debian

IP da rede interna: 192.168.10.1

Porta TCP: 9000

Cliente

Sistema: Debian

IP da rede interna: 192.168.10.2

Servidor: 192.168.10.1

Porta TCP: 9000

5. Autenticação

O servidor exige autenticação antes de permitir a execução de comandos.

Credenciais utilizadas no projeto:

Usuário: admin
Senha: 1234

Caso a autenticação não seja realizada com sucesso, o cliente não recebe
permissão para executar comandos.

6. Como executar

6.1 Configurar o Servidor

Na máquina virtual do servidor:

su -
ip addr add 192.168.10.1/24 dev enp0s8
ip route add 192.168.10.0/24 dev enp0s8

Entre na pasta do projeto:

cd ~/SistemaComandosRemotos

Inicie o servidor:

java -cp target/classes br.com.ifba.sistemaremoto.servidor.ServidorTcp

Ao iniciar corretamente, será exibido:

Servidor TCP iniciado

Deixe o terminal do servidor aberto.

6.2 Configurar o Cliente

Na máquina virtual do cliente:

su -
ip addr add 192.168.10.2/24 dev enp0s8
ip route add 192.168.10.0/24 dev enp0s8

Teste a comunicação com o servidor:

ping -c 3 192.168.10.1

Teste a porta TCP:

nc -vz 192.168.10.1 9000

O resultado esperado é:

open

Entre na pasta do projeto:

cd ~/SistemaComandosRemotos

Execute o cliente:

java -cp target/classes br.com.ifba.sistemaremoto.cliente.Cliente

Informe:

Usuário: admin
Senha: 1234

Após a autenticação, será possível utilizar o terminal remoto.

7. Exemplo de utilização

Após a autenticação:

remoto> echo teste
teste
Código de saída: 0

Nesse processo:

O cliente recebe o comando digitado pelo usuário.

O comando é enviado pelo socket TCP.

O servidor recebe o comando.

O servidor executa o comando no sistema operacional.

A saída gerada é enviada de volta pelo socket.

O cliente exibe a resposta no terminal.

8. Encerramento

A sessão pode ser encerrada utilizando a palavra-chave definida pelo
cliente:

/exit

9. Controle de comandos

O servidor possui uma filtragem para impedir a execução de determinados
comandos considerados perigosos no contexto do projeto.

Entre os comandos/padrões bloqueados estão:

rm

shutdown

reboot

mkfs

mv

Quando um comando bloqueado é identificado, o servidor retorna uma
mensagem de comando negado em vez de executá-lo.

10. Estrutura do projeto

A aplicação é organizada em dois módulos principais:

src/
└── main/
    └── java/
        └── br/
            └── com/
                └── ifba/
                    └── sistemaremoto/
                        ├── servidor/
                        │   ├── ServidorTcp
                        │   ├── Autenticacao
                        │   └── ExecutorComandos
                        │
                        └── cliente/
                            └── Cliente

Servidor

O pacote do servidor é responsável por:

abrir o ServerSocket;

aceitar conexões;

autenticar usuários;

receber comandos;

executar comandos no sistema operacional;

enviar as respostas ao cliente.

Cliente

O cliente é responsável por:

estabelecer a conexão TCP;

enviar usuário e senha;

enviar comandos;

receber as respostas;

exibir os resultados no terminal.

11. Compilação

O projeto utiliza Maven.

Para compilar e gerar os arquivos da aplicação:

./mvnw clean package

Após a compilação, os arquivos compilados ficam disponíveis em:

target/classes

12. Requisitos atendidos

O projeto contempla os principais requisitos da atividade:

Utilização do VirtualBox

Cliente e servidor em máquinas virtuais diferentes

Debian Linux

Cliente e servidor na mesma rede

Comunicação utilizando TCP

Autenticação obrigatória

Execução de comandos no servidor

Retorno da saída dos comandos para o cliente

Comunicação persistente durante a sessão

Encerramento da conexão

Filtragem de comandos perigosos (funcionalidade opcional)

13. Repositório

Código-fonte do projeto:

GitHub: https://github.com/kauealecrim/SistemaComandosRemotos

14. Demonstração

Para demonstrar o funcionamento:

Iniciar a VM do servidor.

Configurar o IP 192.168.10.1.

Iniciar o ServidorTcp.

Iniciar a VM do cliente.

Configurar o IP 192.168.10.2.

Testar a comunicação com ping.

Testar a porta 9000 com nc.

Iniciar o Cliente.

Realizar a autenticação.

Executar um comando remoto, como echo teste.
