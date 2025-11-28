# Guia Docker - Sistema de Analise de Hemogramas

## Visao Geral

O sistema agora roda **completamente em Docker** com 3 servicos:

| Servico | Container | Porta | Descricao |
|---------|-----------|-------|-----------|
| **fhir** | hapi-fhir-jpaserver | 8080 | Servidor HAPI-FHIR |
| **db** | hapi-fhir-postgres | 5432 | PostgreSQL (para HAPI-FHIR) |
| **hemograma-app** | hemograma-ubiquo-app | 8081 | Aplicacao Spring Boot |

---

## PASSO A PASSO COMPLETO

### **PASSO 1: Pre-requisitos**

Certifique-se de ter instalado:
- Docker Desktop
- Docker Compose

Verifique:
```bash
docker --version
docker-compose --version
```

---

### **PASSO 2: Navegar ate o diretorio do projeto**

```bash
cd "D:/Software Para Computacao Ubiqua/projeto-computacao-ubiqua-2025-2"
```

---

### **PASSO 3: Construir e Iniciar TODOS os Servicos**

```bash
docker-compose up --build -d
```

**Explicacao:**
- `--build`: Reconstroi a imagem da aplicacao
- `-d`: Roda em background (detached)

**Primeira execucao pode demorar ~5-10 minutos** (download de imagens + build Maven)

---

### **PASSO 4: Verificar Status dos Containers**

```bash
docker-compose ps
```

**Resultado esperado:**
```
NAME                      STATUS                   PORTS
hapi-fhir-jpaserver       Up (healthy)             0.0.0.0:8080->8080/tcp
hapi-fhir-postgres        Up (healthy)             5432/tcp
hemograma-ubiquo-app      Up (healthy)             0.0.0.0:8081->8081/tcp
```

**Aguarde ate todos estarem "healthy"** (pode levar 1-2 minutos)

---

### **PASSO 5: Verificar Logs**

#### **Ver logs de todos os servicos:**
```bash
docker-compose logs -f
```

#### **Ver logs apenas da aplicacao:**
```bash
docker-compose logs -f hemograma-app
```

#### **Ver logs apenas do HAPI-FHIR:**
```bash
docker-compose logs -f fhir
```

**Pressione `Ctrl+C` para sair dos logs**

---

### **PASSO 6: Testar os Servicos**

#### **6.1 Testar HAPI-FHIR:**
```bash
curl http://localhost:8080/fhir/metadata
```

**Resposta esperada:** JSON com metadados do servidor FHIR

#### **6.2 Testar Aplicacao Spring Boot:**
```bash
curl http://localhost:8081/hemograma-api/actuator/health
```

**Resposta esperada:**
```json
{"status":"UP"}
```

#### **6.3 Acessar Swagger UI:**

Abra no navegador:
```
http://localhost:8081/hemograma-api/swagger-ui.html
```

#### **6.4 Acessar Console H2:**

Abra no navegador:
```
http://localhost:8081/hemograma-api/h2-console
```

**Credenciais:**
- JDBC URL: `jdbc:h2:file:/app/data/hemograma_db`
- User: `sa`
- Password: *(vazio)*

---

### **PASSO 7: Criar Subscription e Testar Fluxo**

#### **7.1 Criar Subscription:**
```bash
curl -X POST "http://localhost:8081/hemograma-api/fhir-management/subscription/criar"
```

**Resposta esperada:**
```json
{
  "subscriptionId": "1",
  "message": "Subscription criada com sucesso",
  "status": "success"
}
```

#### **7.2 Criar Paciente:**
```bash
curl -X POST "http://localhost:8080/fhir/Patient" \
  -H "Content-Type: application/fhir+json" \
  -d '{
    "resourceType": "Patient",
    "name": [{"family": "Silva", "given": ["Joao"]}],
    "gender": "male",
    "birthDate": "1985-03-15"
  }'
```

#### **7.3 Criar Observation (Hemograma):**
```bash
curl -X POST "http://localhost:8080/fhir/Observation" \
  -H "Content-Type: application/fhir+json" \
  -d '{
    "resourceType": "Observation",
    "status": "final",
    "category": [{"coding": [{"system": "http://terminology.hl7.org/CodeSystem/observation-category","code": "laboratory"}]}],
    "code": {"coding": [{"system": "http://loinc.org","code": "718-7","display": "Hemoglobin"}]},
    "subject": {"reference": "Patient/1"},
    "valueQuantity": {"value": 14.5, "unit": "g/dL"}
  }'
```

#### **7.4 Verificar Logs da Aplicacao:**
```bash
docker-compose logs -f hemograma-app
```

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

---

## COMANDOS UTEIS

### **Iniciar servicos:**
```bash
docker-compose up -d
```

### **Parar servicos:**
```bash
docker-compose down
```

### **Parar e remover volumes (limpa dados):**
```bash
docker-compose down -v
```

### **Reconstruir apenas a aplicacao:**
```bash
docker-compose up --build -d hemograma-app
```

### **Reiniciar um servico especifico:**
```bash
docker-compose restart hemograma-app
```

### **Ver logs em tempo real:**
```bash
docker-compose logs -f
```

### **Entrar no container da aplicacao:**
```bash
docker exec -it hemograma-ubiquo-app sh
```

### **Ver uso de recursos:**
```bash
docker stats
```

---

## SOLUCAO DE PROBLEMAS

### **Problema 1: Container nao inicia**

**Verificar logs:**
```bash
docker-compose logs hemograma-app
```

**Possiveis causas:**
- Erro de compilacao Maven
- Porta ja em uso
- Falta de memoria

### **Problema 2: Erro de conexao entre containers**

**Verificar rede:**
```bash
docker network ls
docker network inspect projeto-computacao-ubiqua-2025-2_hemograma-network
```

### **Problema 3: Build muito lento**

O primeiro build baixa todas as dependencias Maven. Builds subsequentes sao mais rapidos devido ao cache.

**Forcar rebuild sem cache:**
```bash
docker-compose build --no-cache
```

### **Problema 4: Subscription nao funciona**

Verifique se a aplicacao esta acessivel pelo HAPI-FHIR:
```bash
# Entrar no container do HAPI-FHIR
docker exec -it hapi-fhir-jpaserver sh

# Testar conexao com a aplicacao
wget -qO- http://hemograma-app:8081/hemograma-api/actuator/health
```

---

## ARQUITETURA DOCKER

```
+-------------------------------------------------------------+
|                    Docker Network                            |
|                  (hemograma-network)                         |
|                                                              |
|  +-----------------+  +-----------------+  +--------------+  |
|  |   PostgreSQL    |  |   HAPI-FHIR     |  |  Hemograma   |  |
|  |   (db)          |  |   (fhir)        |  |  App         |  |
|  |                 |  |                 |  |              |  |
|  |  Port: 5432     |  |  Port: 8080     |  |  Port: 8081  |  |
|  |  (interno)      |  |  (externo)      |  |  (externo)   |  |
|  +--------+--------+  +--------+--------+  +------+-------+  |
|           |                    |                  |          |
|           +--------------------+------------------+          |
|                                |                             |
+--------------------------------+-----------------------------+
                                 |
                    +------------+------------+
                    |      Host Machine       |
                    |                         |
                    |  localhost:8080 -> FHIR |
                    |  localhost:8081 -> App  |
                    +-------------------------+
```

---

## CHECKLIST DE VALIDACAO

- [ ] `docker-compose up --build -d` executou sem erros
- [ ] `docker-compose ps` mostra todos os containers "Up"
- [ ] Todos os containers estao "healthy"
- [ ] HAPI-FHIR acessivel em http://localhost:8080/fhir
- [ ] Aplicacao acessivel em http://localhost:8081/hemograma-api
- [ ] Health check retorna `{"status":"UP"}`
- [ ] Swagger UI acessivel
- [ ] Subscription criada com sucesso
- [ ] Observation processada corretamente
- [ ] Logs mostram "Observation processada com sucesso"

---

## RESUMO DE COMANDOS

```bash
# Subir tudo
docker-compose up --build -d

# Ver status
docker-compose ps

# Ver logs
docker-compose logs -f

# Parar tudo
docker-compose down

# Limpar tudo (incluindo dados)
docker-compose down -v
```

---

**Execute o PASSO 3 e verifique se funcionou!**

