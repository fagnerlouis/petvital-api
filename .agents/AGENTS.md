# Regras do Agente — PetVital API

## Manutenção da Collection do Insomnia

**REGRA OBRIGATÓRIA:** Sempre que um novo endpoint REST for implementado ou um endpoint existente for modificado (URL, método, body ou headers), o arquivo `docs/insomnia_collection.json` **DEVE** ser atualizado na mesma sessão de trabalho, antes de finalizar a tarefa.

### O que atualizar na collection:
- Novos endpoints: adicionar novo `request` dentro da pasta (`request_group`) do módulo correspondente.
- Endpoint removido: remover o `request` da collection.
- Mudança de URL ou método: atualizar o `request` existente.
- Novo módulo/entidade: criar uma nova `request_group` com o emoji e nome do módulo.

### Estrutura de IDs:
- Pastas: `fld_<modulo>` (ex: `fld_agendamento`)
- Requests: `req_<modulo>_<acao>` (ex: `req_agendamento_criar`)

### Módulos atuais na collection:
- 🔐 Autenticação (`fld_auth`)
- 🏥 Clínica (`fld_clinica`)
- 👤 Usuário (`fld_usuario`)
- 🧑 Tutor (`fld_tutor`)
- 🐾 Animal (`fld_animal`)

---

## Manutenção da Documentação

**REGRA OBRIGATÓRIA:** Sempre que uma tabela do banco de dados for criada ou alterada (via migration Flyway), o arquivo `docs/data_model.md` **DEVE** ser atualizado para refletir a estrutura real implementada.

## Estratégia de Branches

Seguir a estratégia documentada em `docs/Estrategia_de_Branching_e_Commits.md`:
- Branches de feature: `feat/<nome-da-feature>`
- Branches de correção: `fix/<descricao>`
- Commits no padrão Conventional Commits: `feat:`, `fix:`, `docs:`, `refactor:`
