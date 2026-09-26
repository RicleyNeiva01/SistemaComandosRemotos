🖥️ Sistema de Comandos Remotos

Sistema Cliente/Servidor para execução controlada de comandos
remotos utilizando Sockets TCP.

Projeto desenvolvido para a disciplina de Redes de Computadores, com
o objetivo de aplicar conceitos de comunicação em rede, autenticação,
gerenciamento de sessão, comunicação persistente e execução de processos
no sistema operacional.

📌 Sumário

🎯 Objetivo

🧰 Tecnologias

🏗️ Arquitetura

🌐 Configuração da Rede

🔐 Autenticação

⚙️ Como Executar

💻 Funcionamento

🛡️ Controle de Comandos

📁 Estrutura do Projeto

🔨 Compilação

✅ Requisitos Atendidos

🎬 Demonstração

👥 Autores

🎯 Objetivo

O sistema simula uma sessão de terminal remoto.

O cliente se conecta ao servidor através de uma conexão TCP, realiza
a autenticação e, após o acesso ser autorizado, pode enviar comandos
para execução no sistema operacional da máquina servidora.

O resultado da execução é enviado de volta ao cliente através da mesma
conexão.

Fluxo principal

┌───────────────┐        Socket TCP        ┌────────────────┐
│    CLIENTE    │ ───────────────────────► │    SERVIDOR    │
│ 192.168.10.2  │        Porta 9000        │ 192.168.10.1  │
│               │ ◄─────────────────────── │                │
│ Envia comando │       Retorna saída      │ Executa comando│
└───────────────┘                          └────────────────┘

🧰 Tecnologias

Tecnologia            Utilização

☕ Java           Desenvolvimento da aplicação
🔌 Socket TCP     Comunicação entre cliente e servidor
🐧 Debian Linux   Sistema operacional das VMs
📦 Maven          Compilação e gerenciamento do projeto
📦 VirtualBox     Criação das máquinas virtuais
🐙 Git/GitHub     Versionamento e hospedagem do código

🏗️ Arquitetura

O projeto utiliza a arquitetura Cliente/Servidor.

🖥️ Servidor

O servidor:

Abre a porta TCP 9000;

Aguarda conexões;

Solicita autenticação;

Valida usuário e senha;

Recebe comandos;

Executa os comandos no sistema operacional;

Envia a saída de volta ao cliente.

💻 Cliente

O cliente:

Estabelece a conexão com o servidor;

Envia as credenciais;

Aguarda a autenticação;

Exibe o terminal remoto;

Envia comandos;

Recebe e exibe os resultados;

Permite encerrar a sessão.

🌐 Configuração da Rede

As máquinas virtuais foram configuradas no VirtualBox utilizando uma
Rede Interna chamada:

internet

🖥️ Servidor

Sistema: Debian Linux
IP:      192.168.10.1
Porta:   9000

💻 Cliente

Sistema: Debian Linux
IP:      192.168.10.2
Servidor: 192.168.10.1
Porta:    9000

Observação: os IPs da rede interna são configurados manualmente
nas VMs e podem precisar ser configurados novamente após reiniciar as
máquinas.

🔐 Autenticação

A autenticação é obrigatória antes da execução de comandos.

Credenciais utilizadas

Usuário: admin
Senha:   1234

O fluxo é:

Cliente
   │
   ├── Conecta ao servidor
   │
   ├── Envia usuário e senha
   │
   ▼
Servidor
   │
   ├── Valida credenciais
   │
   ├── ❌ Inválidas → acesso negado
   │
   └── ✅ Válidas → acesso liberado
   │
   ▼
Terminal Remoto

⚙️ Como Executar

1. 🖥️ Iniciar o Servidor

Na VM Servidor1, abra o terminal e execute:

su -

Configure o IP da rede interna:

ip addr add 192.168.10.1/24 dev enp0s8

Configure a rota:

ip route add 192.168.10.0/24 dev enp0s8

Entre na pasta do projeto:

cd ~/SistemaComandosRemotos

Inicie o servidor:

java -cp target/classes br.com.ifba.sistemaremoto.servidor.ServidorTcp

Mensagem esperada:

Servidor TCP iniciado

⚠️ Mantenha o terminal do servidor aberto enquanto o cliente estiver
utilizando o sistema.

2. 💻 Iniciar o Cliente

Na VM Cliente, abra o terminal:

su -

Configure o IP:

ip addr add 192.168.10.2/24 dev enp0s8

Configure a rota:

ip route add 192.168.10.0/24 dev enp0s8

Testar a comunicação

ping -c 3 192.168.10.1

O esperado é:

3 packets transmitted, 3 received, 0% packet loss

Testar a porta TCP

nc -vz 192.168.10.1 9000

O resultado esperado contém:

open

Executar o cliente

cd ~/SistemaComandosRemotos
java -cp target/classes br.com.ifba.sistemaremoto.cliente.Cliente

Informe as credenciais:

Usuário: admin
Senha: 1234

💻 Funcionamento

Após a autenticação, o cliente disponibiliza um terminal remoto.

Exemplo

remoto> echo teste

teste
Código de saída: 0

O comando segue o seguinte caminho:

1. Usuário digita o comando
             ↓
2. Cliente envia pelo Socket TCP
             ↓
3. Servidor recebe o comando
             ↓
4. Servidor executa no sistema operacional
             ↓
5. Servidor captura a saída
             ↓
6. Saída é enviada pelo Socket TCP
             ↓
7. Cliente exibe o resultado

A conexão permanece aberta para permitir múltiplas operações de
comando/resposta durante a mesma sessão.

🛡️ Controle de Comandos

Como funcionalidade adicional de segurança, o servidor possui uma
filtragem para impedir a execução de determinados comandos considerados
perigosos no contexto do projeto.

Entre os padrões bloqueados estão:

rm
shutdown
reboot
mkfs
mv

Quando um comando bloqueado é identificado, ele não é executado e o
sistema informa que a operação foi negada.

Essa funcionalidade corresponde ao opcional de filtragem de
comandos proposto na atividade.

🚪 Encerramento da Sessão

O cliente possui uma palavra-chave para solicitar o encerramento da
conexão:

/exit

📁 Estrutura do Projeto

SistemaComandosRemotos/
│
├── src/
│   └── main/
│       └── java/
│           └── br/
│               └── com/
│                   └── ifba/
│                       └── sistemaremoto/
│                           │
│                           ├── cliente/
│                           │   └── Cliente.java
│                           │
│                           └── servidor/
│                               ├── ServidorTcp.java
│                               ├── Autenticacao.java
│                               └── ExecutorComandos.java
│
├── target/
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md

📌 Principais classes

Cliente.java - Conecta ao servidor; - Envia credenciais; - Envia
comandos; - Recebe respostas; - Exibe os resultados.

ServidorTcp.java - Abre o ServerSocket; - Aceita conexões; -
Gerencia a sessão; - Realiza a autenticação; - Recebe e responde aos
comandos.

Autenticacao.java - Mantém e valida as credenciais permitidas.

ExecutorComandos.java - Executa os comandos no sistema
operacional; - Captura stdout e stderr; - Retorna o resultado e o
código de saída; - Aplica a filtragem de comandos.

🔨 Compilação

O projeto utiliza Maven.

Para limpar, compilar e gerar os arquivos da aplicação:

./mvnw clean package

Após a compilação, as classes ficam disponíveis em:

target/classes

Os comandos de execução utilizados são:

Servidor

java -cp target/classes br.com.ifba.sistemaremoto.servidor.ServidorTcp

Cliente

java -cp target/classes br.com.ifba.sistemaremoto.cliente.Cliente

✅ Requisitos Atendidos

Requisito                                     Status

VirtualBox                                      ✅
Debian Linux                                    ✅
Cliente e servidor em VMs diferentes            ✅
Cliente e servidor na mesma rede                ✅
Comunicação TCP                                 ✅
Autenticação obrigatória                        ✅
Lista de usuários/senhas                        ✅
Execução de comandos no servidor                ✅
Retorno da saída para o cliente                 ✅
Comunicação persistente durante a sessão        ✅
Encerramento da conexão                         ✅
Filtragem de comandos perigosos             ✅ Opcional
Repositório Git                                 ✅
README de execução                              ✅

🎬 Demonstração

Para apresentar o funcionamento do sistema, recomenda-se seguir esta
ordem:

1️⃣ Ambiente

Mostrar as duas máquinas virtuais:

Servidor1

Cliente

2️⃣ Rede

Mostrar:

IP do servidor: 192.168.10.1

IP do cliente: 192.168.10.2

ping funcionando

3️⃣ Servidor

Mostrar:

Servidor TCP iniciado

4️⃣ Conectividade TCP

Mostrar:

nc -vz 192.168.10.1 9000

com resultado:

open

5️⃣ Autenticação

Mostrar:

Usuário: admin
Senha: 1234

e a mensagem de autenticação bem-sucedida.

6️⃣ Execução remota

Demonstrar:

remoto> echo teste
teste
Código de saída: 0

7️⃣ Segurança

Demonstrar, se desejado, um comando bloqueado pelo servidor.

📸 Evidências da Demonstração

Os principais registros da apresentação são:

VirtualBox: Cliente e Servidor ligados;

Servidor: IP 192.168.10.1;

Cliente: IP 192.168.10.2;

Ping: comunicação entre as VMs;

Servidor TCP: serviço iniciado;

Porta 9000: conexão TCP disponível;

Autenticação: acesso autorizado;

Comando remoto: comando executado e resultado retornado;

Segurança: comando bloqueado, caso apresentado.

🌐 Repositório

Código-fonte do projeto:

GitHub:
https://github.com/RicleyNeiva01/SistemaComandosRemotos.git

👥 Autores

Projeto desenvolvido pelos alunos para a disciplina de Redes de
Computadores.
