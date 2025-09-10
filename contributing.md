## 🤝 Orientações para contribuição

### Contribuição
1. Crie um fork do projeto.
2. Crie uma branch para sua feature, seguindo as [orientações](https://github.com/eadaianne/projeto-computacao-ubiqua-2025-2/blob/master/contributing.md#branchs).
3. Faça commits organizados e descritivos, seguindo as [orientações](https://github.com/eadaianne/projeto-computacao-ubiqua-2025-2/blob/master/contributing.md#commits)
4. Envie para o repositório remoto.
5. Abra um Pull Request, seguindo as [orientações](https://github.com/eadaianne/projeto-computacao-ubiqua-2025-2/blob/master/contributing.md#pull-requests)
---
</br>

## 📝 Branchs

1\. Sempre mantenha suas branchs atualizadas, principalmente a branch main.
```
git checkout main
git pull origin main
```
2\. Crie uma nova branch **descritiva** para a sua feature. Siga a referência de nomeação abaixo:

`feature/` → novas funcionalidades   
`fix/` → correções de bugs  
`refactor/` → melhorias no código sem alterar comportamento  
`docs/` → alterações de documentação  
`test/` → novos testes ou ajustes em testes existentes  

```
git checkout -b feature/minha-feature
```
ou

```
git branch feature/minha-feature
git checkout feature/minha-feature
```

## 📝 Commits
1\. Crie commits com mensagens **descritivas** e **breves**
- Mensagens devem ser **curtas, descritivas e em português**.
- Use **imperativo** (ex: "Adiciona validação", "Corrige bug", "Implementa endpoint").
- Inclua **detalhes adicionais no corpo** se necessário.

#### Estrutura de commit: 
git commit -m `prefixo`: `mensagem curta e descritiva` -m `mais detalhes se necessário`

#### Exemplo:
```
git commit -m "docs: atualiza README com instruções de execução" -m "Inclui comandos para rodar aplicação localmente e acessar Swagger UI."
```

2\. Evite commits muito longos, com muitas funcionalidades não relacionadas umas às outras.

## 📝 Pull Requests
- Sempre abrir PR para `develop` (não direto em `main`).
- PR deve ter descrição clara do que foi feito.
- Marcar colegas para revisão.
- Não dar merge nos próprios PR's na main, esperar pela revisão entre pares.

1\. **Antes de abrir um Pull Request (PR):**

- Teste sua branch localmente (mvn test)
- Certifique-se de que o código está seguindo os padrões definidos
- Atualize a documentação caso tenha alterado APIs ou estrutura do sistema

2\. **Ao abrir o PR:**

- Descreva o que foi feito
- Cite issues relacionadas (ex: Closes #12)
- Marque colegas para revisão
---
### 📂 Organização do Projeto

- `main` → branch estável, sempre funcional
- `develop` → branch de integração de features
- `feature/<nome>` → branches individuais para novas funcionalidades
- `hotfix/<nome>` → correções urgentes
---

### 📂 Gestão do projeto

 **Backlog** organizado no GitHub Projects (Kanban).  
  - **To Do** → tarefas não iniciadas  
  - **In Progress** → em desenvolvimento  
  - **Review** → aguardando PR/revisão  
  - **Done** → concluídas  

- Cada sprint terá **responsável por feature** (ex: Marco 1 = Fulano, Marco 2 = Ciclano).
- Atualizar o board a cada avanço.
--- 

### 📚 Documentação

- **README.md** → visão geral, execução, endpoints
- **docs/** → diagramas de arquitetura, notas de design
- **Swagger** → documentação da API
- Atualizar documentação sempre que código novo alterar comportamento

---

### ✅ Testes

- Cobertura mínima recomendada: **70%**
- Escrever testes unitários (JUnit 5)
- Rodar `mvn test` antes de cada PR

---

Seguindo estas práticas, garantimos que o projeto será organizado e colaborativo.

> **Obrigado por contribuir com este projeto!**