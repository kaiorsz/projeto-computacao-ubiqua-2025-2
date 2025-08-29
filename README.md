# Sistema Ubíquo de Análise de Hemogramas

Este projeto implementa um sistema para análise de hemogramas usando FHIR e computação ubíqua, conforme especificado na disciplina de Sistemas Ubíquos.

## Visão Geral

O sistema é responsável por:
- Receber mensagens FHIR via subscription (Marco 1)
- Realizar análise individual de hemogramas com detecção de desvios (Marco 2)
- Armazenar dados localmente (Marco 3)
- Implementar análise coletiva com janelas deslizantes (Marco 4)
- Fornecer API REST para consulta de alertas
- Suportar aplicativo móvel Android com notificações

## Tecnologias Utilizadas

- **Spring Boot 3.2.0** - Framework principal
- **Java 17** - Linguagem de programação
- **H2 Database** - Banco de dados em memória (desenvolvimento)
- **HAPI FHIR** - Biblioteca para manipulação de recursos FHIR
- **SpringDoc OpenAPI** - Documentação automática da API
- **JUnit 5** - Testes unitários

## Estrutura do Projeto

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

## API REST - Endpoint de Alertas

### Endpoints Disponíveis

#### 1. Listar Alertas
```http
GET /api/v1/alertas
```

**Parâmetros de consulta:**
- `page` (int): Número da página (padrão: 0)
- `size` (int): Tamanho da página (padrão: 20)
- `sortBy` (string): Campo para ordenação (padrão: dataCriacao)
- `sortDir` (string): Direção da ordenação - ASC/DESC (padrão: DESC)
- `severidade` (enum): Filtrar por severidade (BAIXA, MEDIA, ALTA, CRITICA)
- `parametro` (enum): Filtrar por parâmetro (LEUCOCITOS, HEMOGLOBINA, PLAQUETAS, HEMATOCRITO)
- `patientId` (string): Filtrar por ID do paciente
- `dataInicio` (datetime): Data inicial para filtro
- `dataFim` (datetime): Data final para filtro
- `apenasNaoProcessados` (boolean): Filtrar apenas alertas não processados

**Exemplo de resposta:**
```json
{
  "content": [
    {
      "id": 1,
      "hemogramaId": 100,
      "fhirObservationId": "obs-hemograma-001",
      "patientId": "patient-001",
      "parametro": "LEUCOCITOS",
      "nomeParametro": "Leucócitos",
      "unidadeParametro": "/µL",
      "valorEncontrado": 15000.0,
      "valorMinimoReferencia": 4000.0,
      "valorMaximoReferencia": 11000.0,
      "tipoDesvio": "ACIMA_MAXIMO",
      "severidade": "ALTA",
      "dataCriacao": "2025-08-29T10:30:00",
      "dataColeta": "2025-08-29T08:00:00",
      "processado": false,
      "percentualDesvio": 36.4,
      "descricao": "Leucócitos acima do valor máximo de referência (15000.00 /µL)"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "size": 20,
  "number": 0
}
```

#### 2. Buscar Alerta por ID
```http
GET /api/v1/alertas/{id}
```

#### 3. Marcar Alerta como Processado
```http
PUT /api/v1/alertas/{id}/processar
```

#### 4. Obter Resumo de Alertas
```http
GET /api/v1/alertas/resumo
```

**Exemplo de resposta:**
```json
{
  "totalAlertas": 150,
  "alertasNaoProcessados": 25,
  "dataInicio": "2025-08-22T10:30:00",
  "dataFim": "2025-08-29T10:30:00",
  "alertasPorSeveridade": {
    "ALTA": 45,
    "MEDIA": 60,
    "BAIXA": 30,
    "CRITICA": 15
  },
  "alertasPorParametro": {
    "LEUCOCITOS": 40,
    "HEMOGLOBINA": 35,
    "PLAQUETAS": 45,
    "HEMATOCRITO": 30
  },
  "alertasPorTipoDesvio": {
    "ACIMA_MAXIMO": 85,
    "ABAIXO_MINIMO": 65
  },
  "totalPacientesComAlertas": 45,
  "mediaAlertasPorPaciente": 3.33,
  "parametroMaisFrequente": "PLAQUETAS",
  "severidadeMaisComum": "MEDIA"
}
```

#### 5. Listar Alertas por Paciente
```http
GET /api/v1/alertas/paciente/{patientId}
```

## Valores de Referência

Conforme especificação do projeto, os seguintes valores são utilizados para detecção de desvios:

| Parâmetro    | Unidade | Valor Mínimo | Valor Máximo |
|--------------|---------|--------------|--------------|
| Leucócitos   | /µL     | 4.000        | 11.000       |
| Hemoglobina  | g/dL    | 12.0         | 17.5         |
| Plaquetas    | /µL     | 150.000      | 450.000      |
| Hematócrito  | %       | 36           | 52           |

## Como Executar

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

## Documentação da API

A documentação completa da API está disponível através do Swagger UI quando a aplicação estiver em execução.

## Próximos Passos

Para completar os marcos do projeto:

1. **Marco 1**: Implementar receptor FHIR Subscription
2. **Marco 3**: Configurar banco de dados persistente
3. **Marco 4**: Implementar análise coletiva com janelas deslizantes
4. **Aplicativo Móvel**: Desenvolver app Android
5. **Segurança**: Implementar HTTPS com mTLS

## Contribuição

Este projeto faz parte da disciplina de Sistemas Ubíquos. Para contribuir:

1. Faça um fork do projeto
2. Crie uma branch para sua feature
3. Commit suas mudanças
4. Push para a branch
5. Abra um Pull Request
