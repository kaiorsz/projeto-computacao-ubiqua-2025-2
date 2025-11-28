# Teste Rapido do Sistema

## Passo 1: Iniciar Servicos

Escolha UMA das opcoes abaixo:

### OPCAO A: Subir TUDO pelo Docker (Recomendado)

```bash
# Subir todos os servicos (HAPI-FHIR + PostgreSQL + Aplicacao)
docker-compose up --build -d

# Aguardar ate todos estarem "healthy" (1-2 minutos)
docker-compose ps

# Ver logs em tempo real
docker-compose logs -f
```

**Resultado esperado do `docker-compose ps`:**
```
NAME                      STATUS                   PORTS
hapi-fhir-jpaserver       Up (healthy)             0.0.0.0:8080->8080/tcp
hapi-fhir-postgres        Up (healthy)             5432/tcp
hemograma-ubiquo-app      Up (healthy)             0.0.0.0:8081->8081/tcp
```

### OPCAO B: Docker + Aplicacao Local

```bash
# Terminal 1: Iniciar HAPI-FHIR e PostgreSQL
docker-compose up -d fhir db

# Aguardar 30 segundos para o HAPI-FHIR iniciar

# Terminal 2: Iniciar aplicacao localmente
mvn spring-boot:run
```

---

## Passo 2: Testar Conectividade

### Testar HAPI-FHIR:
```bash
curl http://localhost:8080/fhir/metadata
```

**Resposta esperada:** JSON grande com metadados do servidor FHIR

### Testar Aplicacao:
```bash
curl http://localhost:8081/hemograma-api/actuator/health
```

**Resposta esperada:**
```json
{"status":"UP"}
```

---

## Passo 3: Criar Subscription

```bash
curl -X POST http://localhost:8081/hemograma-api/fhir-management/subscription/criar
```

**Resposta esperada:**
```json
{"status":"success","subscriptionId":"1","message":"Subscription criada com sucesso"}
```

---

## Passo 4: Criar Dados de Teste Manualmente

### 4.1 Criar Paciente:

```bash
curl -X POST "http://localhost:8080/fhir/Patient" -H "Content-Type: application/fhir+json" -d "{\"resourceType\":\"Patient\",\"name\":[{\"family\":\"Silva\",\"given\":[\"Joao\"]}],\"gender\":\"male\",\"birthDate\":\"1985-03-15\"}"
```

**Resposta esperada:** JSON com o paciente criado e um ID

### 4.2 Criar Observation (Hemograma com valor NORMAL):

```bash
curl -X POST "http://localhost:8080/fhir/Observation" -H "Content-Type: application/fhir+json" -d "{\"resourceType\":\"Observation\",\"status\":\"final\",\"category\":[{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/observation-category\",\"code\":\"laboratory\"}]}],\"code\":{\"coding\":[{\"system\":\"http://loinc.org\",\"code\":\"718-7\",\"display\":\"Hemoglobin\"}]},\"subject\":{\"reference\":\"Patient/1\"},\"valueQuantity\":{\"value\":14.5,\"unit\":\"g/dL\"}}"
```

### 4.3 Criar Observation (Hemograma com ANEMIA - valor baixo):

```bash
curl -X POST "http://localhost:8080/fhir/Observation" -H "Content-Type: application/fhir+json" -d "{\"resourceType\":\"Observation\",\"status\":\"final\",\"category\":[{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/observation-category\",\"code\":\"laboratory\"}]}],\"code\":{\"coding\":[{\"system\":\"http://loinc.org\",\"code\":\"718-7\",\"display\":\"Hemoglobin\"}]},\"subject\":{\"reference\":\"Patient/1\"},\"valueQuantity\":{\"value\":8.5,\"unit\":\"g/dL\"}}"
```

### 4.4 Criar Observation (Leucocitos ALTO):

```bash
curl -X POST "http://localhost:8080/fhir/Observation" -H "Content-Type: application/fhir+json" -d "{\"resourceType\":\"Observation\",\"status\":\"final\",\"category\":[{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/observation-category\",\"code\":\"laboratory\"}]}],\"code\":{\"coding\":[{\"system\":\"http://loinc.org\",\"code\":\"6690-2\",\"display\":\"Leukocytes\"}]},\"subject\":{\"reference\":\"Patient/1\"},\"valueQuantity\":{\"value\":15000,\"unit\":\"/uL\"}}"
```

---

## Passo 5: Gerar Dados em Massa (Opcional)

```bash
cd scripts
python gerar-hemogramas-ficticios.py
```

Ou no Windows:
```bash
cd scripts
gerar-hemogramas.bat
```

---

## Passo 6: Verificar Resultados

### 6.1 Ver Logs da Aplicacao

**Se usando Docker:**
```bash
docker-compose logs -f hemograma-app
```

**Se rodando localmente:** veja o terminal onde executou `mvn spring-boot:run`

**Logs esperados:**
```
NOTIFICACAO RECEBIDA (PUT com path)!
Resource: Observation/1
Processamento assincrono iniciado
Processando Observation FHIR
Observation ID: 1
Codigo: 718-7 | Sistema: http://loinc.org
Paciente: Patient/1
Valor: 14.5 g/dL
Observation processada com sucesso
```

### 6.2 Consultar Dados no HAPI-FHIR

**Listar todos os pacientes:**
```bash
curl "http://localhost:8080/fhir/Patient?_pretty=true"
```

**Listar todas as observations:**
```bash
curl "http://localhost:8080/fhir/Observation?_pretty=true"
```

**Buscar observations de um paciente especifico:**
```bash
curl "http://localhost:8080/fhir/Observation?subject=Patient/1&_pretty=true"
```

---

## Passo 7: Acessar o Banco de Dados H2

### 7.1 Abrir Console H2

Acesse no navegador:
```
http://localhost:8081/hemograma-api/h2-console
```

### 7.2 Configurar Conexao

**Se usando Docker (OPCAO A):**
- JDBC URL: `jdbc:h2:file:/app/data/hemograma_db`
- User: `sa`
- Password: *(deixar em branco)*

**Se rodando localmente (OPCAO B):**
- JDBC URL: `jdbc:h2:mem:hemograma_db`
- User: `sa`
- Password: *(deixar em branco)*

Clique em **Connect**

### 7.3 Consultas SQL Uteis

**Ver todas as tabelas:**
```sql
SHOW TABLES;
```

**Ver todos os pacientes:**
```sql
SELECT * FROM PACIENTES;
```

**Ver todos os hemogramas:**
```sql
SELECT * FROM HEMOGRAMAS;
```

**Ver todos os parametros de hemograma:**
```sql
SELECT * FROM PARAMETROS_HEMOGRAMA;
```

**Ver todos os desvios detectados:**
```sql
SELECT * FROM DESVIOS;
```

**Ver desvios agrupados por tipo:**
```sql
SELECT TIPO_PARAMETRO, COUNT(*) AS TOTAL
FROM DESVIOS
GROUP BY TIPO_PARAMETRO;
```

**Ver casos de anemia (hemoglobina baixa):**
```sql
SELECT * FROM DESVIOS WHERE TIPO_PARAMETRO = 'HEMOGLOBINA';
```

**Ver hemogramas com seus desvios:**
```sql
SELECT h.ID, h.DATA_COLETA, d.TIPO_PARAMETRO, d.SEVERIDADE, d.VALOR_ENCONTRADO
FROM HEMOGRAMAS h
LEFT JOIN DESVIOS d ON h.ID = d.HEMOGRAMA_ID
ORDER BY h.ID;
```

**Ver alertas coletivos:**
```sql
SELECT * FROM ALERTAS_COLETIVOS ORDER BY DATA_CRIACAO DESC;
```

---

## Teste de Analise Coletiva

### Gerar muitos desvios

Edite `scripts/gerar-hemogramas-ficticios.py`:
```python
NUM_PACIENTES = 30
NUM_HEMOGRAMAS_POR_PACIENTE = 10
```

Execute:
```bash
python gerar-hemogramas-ficticios.py
```

### Aguardar 5 minutos

A analise coletiva executa automaticamente a cada 5 minutos.

### Verificar alertas coletivos

```sql
SELECT * FROM ALERTAS_COLETIVOS ORDER BY DATA_CRIACAO DESC;
```

Procure nos logs:
```
ALERTA COLETIVO: X desvios de Hemoglobina detectados
NOTIFICACAO CRITICA: Alerta coletivo
```

---

## Comandos Uteis

### Parar tudo (Docker)
```bash
docker-compose down
```

### Parar tudo e limpar dados
```bash
docker-compose down -v
```

### Reiniciar apenas a aplicacao
```bash
docker-compose restart hemograma-app
```

### Reconstruir a aplicacao
```bash
docker-compose up --build -d hemograma-app
```

### Ver logs do HAPI-FHIR
```bash
docker-compose logs -f fhir
```

### Ver logs da aplicacao
```bash
docker-compose logs -f hemograma-app
```

### Ver todos os logs
```bash
docker-compose logs -f
```

### Ver status dos containers
```bash
docker-compose ps
```

### Ver uso de recursos
```bash
docker stats
```

---

## Resumo das URLs

| Servico | URL |
|---------|-----|
| HAPI-FHIR | http://localhost:8080/fhir |
| Aplicacao | http://localhost:8081/hemograma-api |
| Swagger UI | http://localhost:8081/hemograma-api/swagger-ui.html |
| Console H2 | http://localhost:8081/hemograma-api/h2-console |
| Health Check | http://localhost:8081/hemograma-api/actuator/health |

---

## Codigos LOINC para Testes

| Parametro | Codigo LOINC | Valor Normal |
|-----------|--------------|--------------|
| Hemoglobina | 718-7 | 12.0 - 17.5 g/dL |
| Leucocitos | 6690-2 | 4000 - 11000 /uL |
| Plaquetas | 777-3 | 150000 - 450000 /uL |
| Hematocrito | 4544-3 | 36 - 52 % |

