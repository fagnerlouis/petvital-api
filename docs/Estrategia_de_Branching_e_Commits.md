# Estratégia de Branching e Commits (PetVital)

Este documento define o fluxo de trabalho de versionamento de código (Git) para o projeto PetVital, focado em agilidade e entregas contínuas.

## 1. Modelo Adotado: GitHub Flow
Para o MVP, utilizaremos um modelo simplificado baseado no **GitHub Flow**. Evitaremos a complexidade do Git Flow tradicional (sem branches `develop` ou `release`).

### A Regra de Ouro
> **A branch `main` é sagrada.** O código presente na `main` deve estar sempre estável, funcional e pronto para ser publicado em produção a qualquer momento. Nunca faça commits diretos na `main`.

---

## 2. Padrões de Nomenclatura de Branches

Toda nova alteração (seja uma funcionalidade ou correção) deve ser feita em uma branch temporária, criada a partir da `main`.

| Prefixo | Quando usar | Exemplo de nome |
| :--- | :--- | :--- |
| `feat/` | Criação de uma nova funcionalidade (Feature). | `feat/crud-clinica` |
| `fix/` | Correção de um bug ou erro no sistema. | `fix/erro-login-jwt` |
| `chore/` | Tarefas de manutenção, atualização de pacotes, ou configurações que não afetam o código do usuário final. | `chore/atualiza-spring-boot` |
| `refactor/` | Refatoração de código (melhoria de estrutura sem mudar comportamento). | `refactor/otimizacao-queries` |
| `docs/` | Atualização exclusiva de documentação. | `docs/estrategia-branching` |

### Como criar uma branch:
```bash
# Certifique-se de estar na main e com ela atualizada
git checkout main
git pull origin main

# Crie a nova branch e mude para ela
git checkout -b feat/nome-da-sua-branch
```

---

## 3. Padrão de Commits (Conventional Commits)

Os commits devem seguir o padrão internacional [Conventional Commits](https://www.conventionalcommits.org/pt-br/v1.0.0-beta.4/), o que facilita a leitura do histórico e a geração automática de changelogs no futuro.

**Formato:**
`tipo(escopo-opcional): descrição curta em minúsculas e no imperativo`

**Exemplos:**
*   `feat: adiciona endpoint para criacao de clinica`
*   `fix(auth): corrige token jwt expirando em 5 minutos`
*   `chore: adiciona configuracao do docker compose`
*   `docs: cria documento de estrategia de branching`

---

## 4. O Ciclo de Vida de uma Tarefa (Workflow)

1.  **Criação:** Crie uma branch a partir da `main` com o prefixo correto (`feat/` ou `fix/`).
2.  **Desenvolvimento:** Faça seus commits padronizados.
3.  **Sincronização:** Antes de finalizar, puxe as alterações mais recentes da main para evitar conflitos (`git pull origin main`).
4.  **Pull Request (PR):** Envie sua branch para o GitHub (`git push origin nome-da-branch`) e abra um Pull Request apontando para a `main`.
5.  **Revisão e Merge:** Após aprovação e garantia de que a aplicação compila sem erros, o PR é mesclado (*Merged*) na `main`.
6.  **Limpeza:** Delete a branch temporária do GitHub e localmente.
