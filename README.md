![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-green)
<!-- ![Build](https://img.shields.io/github/actions/workflow/status/<org>/<repo>/maven.yml) -->

# 🩸 Sistema Ubíquo de Análise de Hemogramas

Este projeto implementa um sistema para análise de hemogramas usando **FHIR** e **computação ubíqua**, desenvolvido no contexto da disciplina de **Sistemas Ubíquos**.

## 📌 Visão Geral

O sistema é responsável por:

- Receber mensagens FHIR via subscription (**Marco 1**)  
- Realizar análise individual de hemogramas com detecção de desvios (**Marco 2**)  
- Armazenar dados localmente (**Marco 3**)  
- Implementar análise coletiva com janelas deslizantes (**Marco 4**)  
- Fornecer API REST para consulta de alertas  
- Suportar aplicativo móvel Android com notificações  

## 🛠️ Tecnologias Utilizadas

- **Spring Boot 3.2.0** – Framework principal  
- **Java 17** – Linguagem de programação  
- **H2 Database** – Banco de dados em memória (desenvolvimento)  
- **HAPI FHIR** – Manipulação de recursos FHIR  
- **SpringDoc OpenAPI** – Documentação automática da API  
- **JUnit 5** – Testes unitários  

## 📂 Estrutura do Projeto

```
src/
??? main/
?   ??? java/br/ufg/inf/hemograma/
?   ?   ??? controller/          # Controllers REST
?   ?   ??? dto/                 # Data Transfer Objects
?   ?   ??? model/               # Entidades JPA
?   ?   ??? repository/          # Repositórios de dados
?   ?   ??? service/             # Lógica de negócio
?   ?   ??? config/              # Configurações
?   ?   ??? exception/           # Exceções customizadas
?   ??? resources/
?       ??? application.yml      # Configurações da aplicação
??? test/                        # Testes unitários
```

## 📅 Marcos do Projeto    

- [] **Receptor FHIR via Subscription/Observation**
- [] **Análise individual de hemogramas**
- [] **Base de dados consolidada**
- [] **Análise coletiva de hemogramas**
- [] **API REST com endpoints funcionais**
- [] **Aplicativo Android com notificações push**
- [] **Testes automatizados**
- [] **Medidas de segurança**

## 📂 Gestão do projeto
Feita utilizando Github Projects, com um quadro Kanban. O projeto pode ser acessado para [visualização aqui](https://github.com/users/eadaianne/projects/1)

## Endpoints API Rest
  Os endpoints podem ser encontrados no arquivo [endpoints-api-REST.md](https://github.com/eadaianne/projeto-computacao-ubiqua-2025-2/blob/master/endpoints-api-REST.md)

## 🚀 Como Executar

### Pré-requisitos
- Java 17 ou superior
- Maven 3.6 ou superior

### Executando a aplicação
```bash
# Clonar o repositório
git clone <url-do-repositorio>
cd projeto-computacao-ubiqua-2025-2

# Compilar e executar
mvn spring-boot:run
```

### Acessando a aplicação
- **API Base URL**: http://localhost:8080/hemograma-api
- **Swagger UI**: http://localhost:8080/hemograma-api/swagger-ui.html
- **Console H2**: http://localhost:8080/hemograma-api/h2-console

### Executando os testes
```bash
mvn test
```

## 📖 Documentação da API

A documentação completa da API está disponível através do Swagger UI quando a aplicação estiver em execução.

## 🔒 Segurança (planejado)

- [] Comunicação via HTTPS
- [] Autenticação mTLS entre serviços
- [] Não armazenamento de dados pessoais identificáveis

## Contribuição

Este projeto faz parte da disciplina de Sistemas Ubíquos. Contribuições devem ser feitas seguindo as orientações em [contributing.md](https://github.com/eadaianne/projeto-computacao-ubiqua-2025-2/blob/master/contributing.md)
