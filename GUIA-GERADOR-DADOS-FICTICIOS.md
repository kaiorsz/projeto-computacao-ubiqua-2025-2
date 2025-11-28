# Guia do Gerador de Dados Ficticios

## **O QUE FAZ:**

O gerador cria automaticamente:
- **10 pacientes** ficticios (nomes, genero, idade aleatorios)
- **5 hemogramas por paciente** (total: 50 hemogramas)
- **40% dos hemogramas com desvios** (incluindo anemia)
- **Dados realistas** com valores dentro e fora das faixas normais

---

## **COMO USAR:**

### **Pre-requisitos:**
1. HAPI-FHIR rodando (`docker-compose up -d`)
2. Aplicacao Spring Boot rodando (`mvn spring-boot:run`)
3. Subscription criada
4. Python 3.x instalado

---

### **Opcao 1: Script Automatico (Windows)**

```bash
cd scripts
gerar-hemogramas.bat
```

O script ira:
1. Verificar se Python esta instalado
2. Instalar dependencias (requests)
3. Executar o gerador

---

### **Opcao 2: Python Direto**

```bash
cd scripts
python gerar-hemogramas-ficticios.py
```

---

## **SAIDA ESPERADA:**

```
============================================================
GERADOR DE HEMOGRAMAS FICTICIOS
Sistema de Analise de Hemogramas - UFG
============================================================

[INFO] Verificando conectividade com HAPI-FHIR...
[OK] HAPI-FHIR acessivel em http://localhost:8080/fhir

============================================================
CRIANDO PACIENTES
============================================================

[1/10] Criando paciente: Maria Santos
  [OK] Paciente criado com ID: 1

[2/10] Criando paciente: Joao Silva
  [OK] Paciente criado com ID: 2

... (continua para todos os 10 pacientes)

============================================================
CRIANDO HEMOGRAMAS
============================================================

[Paciente 1 - Maria Santos]
  Hemograma 1/5: Valores NORMAIS
    Leucocitos: 7500 /uL
    Hemoglobina: 14.2 g/dL
    Plaquetas: 250000 /uL
    Hematocrito: 42%
  [OK] Observation criada com ID: 1

  Hemograma 2/5: Valores com DESVIO (Anemia)
    Leucocitos: 6800 /uL
    Hemoglobina: 9.5 g/dL  [BAIXO]
    Plaquetas: 180000 /uL
    Hematocrito: 32%  [BAIXO]
  [OK] Observation criada com ID: 2

... (continua para todos os hemogramas)

============================================================
RESUMO
============================================================

Total de pacientes criados: 10
Total de hemogramas criados: 50
Hemogramas com valores normais: 30 (60%)
Hemogramas com desvios: 20 (40%)

Tipos de desvios gerados:
  - Anemia (hemoglobina baixa): 8
  - Leucocitose (leucocitos altos): 5
  - Leucopenia (leucocitos baixos): 4
  - Trombocitopenia (plaquetas baixas): 3

============================================================
GERACAO CONCLUIDA COM SUCESSO!
============================================================
```

---

## **VALORES DE REFERENCIA USADOS:**

| Parametro | Minimo | Maximo | Unidade |
|-----------|--------|--------|---------|
| Leucocitos | 4.000 | 11.000 | /uL |
| Hemoglobina | 12.0 | 17.5 | g/dL |
| Plaquetas | 150.000 | 450.000 | /uL |
| Hematocrito | 36 | 52 | % |

---

## **TIPOS DE DESVIOS GERADOS:**

### **Anemia:**
- Hemoglobina < 12.0 g/dL
- Hematocrito < 36%

### **Leucocitose:**
- Leucocitos > 11.000 /uL

### **Leucopenia:**
- Leucocitos < 4.000 /uL

### **Trombocitopenia:**
- Plaquetas < 150.000 /uL

### **Trombocitose:**
- Plaquetas > 450.000 /uL

---

## **VERIFICAR DADOS GERADOS:**

### **No HAPI-FHIR:**

```bash
# Listar todos os pacientes
curl "http://localhost:8080/fhir/Patient?_pretty=true"

# Listar todas as observations
curl "http://localhost:8080/fhir/Observation?_pretty=true"

# Buscar observations de um paciente especifico
curl "http://localhost:8080/fhir/Observation?subject=Patient/1&_pretty=true"
```

### **No Console H2:**

```sql
-- Ver todos os pacientes
SELECT * FROM PACIENTES;

-- Ver todos os hemogramas
SELECT * FROM HEMOGRAMAS;

-- Ver todos os desvios detectados
SELECT * FROM DESVIOS;

-- Ver hemogramas com desvios
SELECT h.*, d.tipo_parametro, d.severidade 
FROM HEMOGRAMAS h 
JOIN DESVIOS d ON h.id = d.hemograma_id;
```

---

## **PERSONALIZACAO:**

### **Alterar quantidade de pacientes:**

Edite o arquivo `scripts/gerar-hemogramas-ficticios.py`:

```python
# Linha ~20
NUM_PACIENTES = 10  # Altere para o numero desejado
```

### **Alterar quantidade de hemogramas por paciente:**

```python
# Linha ~21
HEMOGRAMAS_POR_PACIENTE = 5  # Altere para o numero desejado
```

### **Alterar percentual de desvios:**

```python
# Linha ~22
PERCENTUAL_DESVIOS = 0.4  # 40% - Altere para o percentual desejado
```

---

## **SOLUCAO DE PROBLEMAS:**

### **Erro: "Connection refused"**

O HAPI-FHIR nao esta rodando.

**Solucao:**
```bash
docker-compose up -d
# Aguarde 30 segundos
curl http://localhost:8080/fhir/metadata
```

### **Erro: "Python not found"**

Python nao esta instalado ou nao esta no PATH.

**Solucao:**
1. Instale Python 3.x de https://python.org
2. Marque "Add Python to PATH" durante a instalacao
3. Reinicie o terminal

### **Erro: "ModuleNotFoundError: requests"**

Biblioteca requests nao esta instalada.

**Solucao:**
```bash
pip install requests
```

### **Erro: "Subscription not found"**

A subscription nao foi criada.

**Solucao:**
```bash
curl -X POST "http://localhost:8081/hemograma-api/fhir-management/subscription/criar"
```

---

## **LIMPAR DADOS E RECOMECAR:**

### **Limpar apenas HAPI-FHIR:**

```bash
# Parar containers
docker-compose down

# Remover volume do PostgreSQL
docker volume rm hapi-fhir-postgres-data

# Reiniciar
docker-compose up -d
```

### **Limpar tudo (incluindo aplicacao):**

```bash
docker-compose down -v
docker-compose up --build -d
```

---

## **INTEGRACAO COM A APLICACAO:**

Quando o gerador cria uma Observation no HAPI-FHIR:

1. HAPI-FHIR detecta a nova Observation
2. HAPI-FHIR envia notificacao para a aplicacao (via Subscription)
3. Aplicacao recebe a notificacao no endpoint `/hemogramas/receber`
4. Aplicacao faz parsing da Observation usando HAPI-FHIR
5. Aplicacao analisa os valores e detecta desvios
6. Aplicacao salva no banco H2
7. Se houver desvio critico, gera alerta

**Verifique os logs da aplicacao:**
```bash
docker-compose logs -f hemograma-app
```

---

## **RESUMO DE COMANDOS:**

```bash
# Gerar dados (Windows)
cd scripts
gerar-hemogramas.bat

# Gerar dados (Python direto)
python scripts/gerar-hemogramas-ficticios.py

# Ver logs da aplicacao
docker-compose logs -f hemograma-app

# Listar pacientes criados
curl "http://localhost:8080/fhir/Patient?_pretty=true"

# Listar observations criadas
curl "http://localhost:8080/fhir/Observation?_pretty=true"
```

---

**Execute o gerador e verifique os logs da aplicacao!**

