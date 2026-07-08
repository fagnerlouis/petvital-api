# Modelagem de Dados - PetVital (MVP)

## Arquitetura: Multi-Inquilino (Multitenancy)

O sistema **PetVital** será construído com uma arquitetura Multi-Inquilino, onde um único banco de dados PostgreSQL servirá a múltiplas clínicas. O isolamento de dados será garantido por uma abordagem de dupla camada:
1. **Filtro Lógico na Aplicação:** Filtro automático do Hibernate/Spring Data injetando `clinica_id` em todas as consultas.
2. **Row Level Security (RLS) no PostgreSQL:** Política nativa do banco de dados restringindo linhas ao `clinica_id` da sessão ativa, prevenindo vazamento de dados acidental.

## Entidades Essenciais do MVP

A modelagem será focada nas entidades necessárias para o Produto Mínimo Viável (MVP):

1.  **Clinica (Tenant):** A entidade principal para o Multitenancy.
2.  **Usuario:** Gerenciamento de acesso e perfis.
3.  **Tutor:** Cadastro de clientes.
4.  **Animal:** Cadastro de pacientes.
5.  **Agendamento:** Controle de consultas.
6.  **Consulta (Prontuário Básico):** Registro do atendimento veterinário.

---

## 1. Tabela: clinica

Armazena os dados de cada clínica (inquilino) ou veterinário autônomo que utiliza o sistema. Serve como entidade raiz (Tenant) do sistema de Multitenancy.

> **Status:** ✅ Implementado — `V1__criar_tabela_clinica.sql`

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PRIMARY KEY | Identificador único do inquilino. |
| `nome` | VARCHAR(255) | NOT NULL | Razão Social (PJ) ou Nome Completo (PF). |
| `nome_fantasia` | VARCHAR(255) | | Nome Comercial. Opcional, apenas para PJ. |
| `tipo_tenant` | VARCHAR(2) | NOT NULL, CHECK (PF,PJ) | Tipo de Inquilino: 'PJ' (Clínica) ou 'PF' (Autônomo). |
| `documento_fiscal` | VARCHAR(14) | UNIQUE, NOT NULL | CNPJ (PJ) ou CPF (PF), somente dígitos. |
| `email` | VARCHAR(255) | | E-mail de contato principal. |
| `whatsapp_api_token` | VARCHAR(255) | | Token da API do WhatsApp (criptografado pela aplicação — RN014). |
| `ativo` | BOOLEAN | NOT NULL, DEFAULT TRUE | Indica se o inquilino está ativo. |
| `data_add` | TIMESTAMP | NOT NULL, DEFAULT NOW() | Data de criação do registro. Preenchida automaticamente. |
| `data_alt` | TIMESTAMP | | Data da última alteração. Preenchida automaticamente. |

### 1.1. Tabela: clinica_telefone

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PRIMARY KEY | Identificador único. |
| `clinica_id` | BIGINT | FK (clinica), CASCADE DELETE | Clínica dona do telefone. |
| `numero` | VARCHAR(20) | NOT NULL | Número de telefone. |
| `tipo` | VARCHAR(50) | NOT NULL | Ex: WHATSAPP, CELULAR, FIXO. |
| `is_principal` | BOOLEAN | NOT NULL, DEFAULT FALSE | Indica o telefone principal de contato. |

### 1.2. Tabela: clinica_endereco

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PRIMARY KEY | Identificador único. |
| `clinica_id` | BIGINT | FK (clinica), CASCADE DELETE | Clínica dona do endereço. |
| `cep` | VARCHAR(10) | NOT NULL | CEP. |
| `logradouro` | VARCHAR(255) | NOT NULL | Rua, Avenida, etc. |
| `numero` | VARCHAR(50) | | Número do imóvel. |
| `complemento` | VARCHAR(255) | | Complemento (apto, bloco). |
| `bairro` | VARCHAR(100) | NOT NULL | Bairro. |
| `cidade` | VARCHAR(100) | NOT NULL | Cidade. |
| `estado` | VARCHAR(2) | NOT NULL | Estado (UF). |

---

## 2. Tabela: usuario

Armazena os usuários com acesso ao sistema. Quando `master = true`, o usuário é um Super Administrador da plataforma (RN026) e não fica vinculado a nenhuma clínica.

> **Status:** ✅ Implementado — `V2__criar_tabela_usuario.sql`

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PRIMARY KEY | Identificador único do usuário. |
| `clinica_id` | BIGINT | FK (clinica), NULLABLE | **Chave de Multitenancy.** Nulo quando `master = true`. |
| `nome` | VARCHAR(255) | NOT NULL | Nome completo do usuário. |
| `email` | VARCHAR(255) | UNIQUE, NOT NULL | E-mail de login. |
| `senha_hash` | VARCHAR(255) | NOT NULL | Hash BCrypt da senha. Nunca texto puro. |
| `perfil` | VARCHAR(50) | NOT NULL, CHECK | Perfil: ADMIN, VETERINARIO, RECEPCAO, AUXILIAR, FINANCEIRO. |
| `master` | BOOLEAN | NOT NULL, DEFAULT FALSE | Super Admin da plataforma. Quando `true`, acessa todos os tenants (RN026, RN027). |
| `ativo` | BOOLEAN | NOT NULL, DEFAULT TRUE | Indica se o usuário está ativo. |
| `data_add` | TIMESTAMP | NOT NULL, DEFAULT NOW() | Data de criação do registro. Preenchida automaticamente. |
| `data_alt` | TIMESTAMP | | Data da última alteração. Preenchida automaticamente. |

---

## 3. Tabela: tutor

Armazena os dados dos tutores (proprietários) dos animais. O CPF é único dentro da mesma clínica, permitindo que o mesmo tutor exista em clínicas diferentes (multitenancy correto).

> **Status:** ✅ Implementado — `V3__criar_tabela_tutor.sql`

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PRIMARY KEY | Identificador único do tutor. |
| `clinica_id` | BIGINT | FK (clinica), NOT NULL | **Chave de Multitenancy.** Liga o tutor à sua clínica. |
| `nome` | VARCHAR(255) | NOT NULL | Nome completo do tutor. |
| `cpf` | VARCHAR(11) | NOT NULL | CPF (apenas dígitos). Único por clínica: UNIQUE(clinica_id, cpf). |
| `email` | VARCHAR(255) | | E-mail para contato. |
| `data_nascimento` | DATE | | Data de nascimento. |
| `aceita_comunicacao_informativa` | BOOLEAN | NOT NULL, DEFAULT FALSE | Consentimento LGPD para receber mensagens informativas (RN010). |
| `ativo` | BOOLEAN | NOT NULL, DEFAULT TRUE | Indica se o tutor está ativo. |
| `data_add` | TIMESTAMP | NOT NULL, DEFAULT NOW() | Data de criação do registro. Preenchida automaticamente. |
| `data_alt` | TIMESTAMP | | Data da última alteração. Preenchida automaticamente. |

### 3.1. Tabela: tutor_telefone

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PRIMARY KEY | Identificador único. |
| `tutor_id` | BIGINT | FK (tutor), CASCADE DELETE | Tutor dono do telefone. |
| `numero` | VARCHAR(20) | NOT NULL | Número de telefone. |
| `tipo` | VARCHAR(50) | NOT NULL | Ex: WHATSAPP, CELULAR, FIXO. |
| `is_principal` | BOOLEAN | NOT NULL, DEFAULT FALSE | Indica o telefone principal de contato. |

### 3.2. Tabela: tutor_endereco

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PRIMARY KEY | Identificador único. |
| `tutor_id` | BIGINT | FK (tutor), CASCADE DELETE | Tutor dono do endereço. |
| `cep` | VARCHAR(10) | NOT NULL | CEP. |
| `logradouro` | VARCHAR(255) | NOT NULL | Rua, Avenida, etc. |
| `numero` | VARCHAR(50) | | Número do imóvel. |
| `complemento` | VARCHAR(255) | | Complemento (apto, bloco). |
| `bairro` | VARCHAR(100) | NOT NULL | Bairro. |
| `cidade` | VARCHAR(100) | NOT NULL | Cidade. |
| `estado` | VARCHAR(2) | NOT NULL | Estado (UF). |

---

## 4. Tabela: animal

Armazena os dados dos animais (pacientes) vinculados a um tutor.

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PRIMARY KEY | Identificador único do animal. |
| `clinica_id` | BIGINT | FOREIGN KEY (Clinica) | **Chave de Multitenancy.** Liga o animal à sua clínica. |
| `tutor_principal_id` | BIGINT | FOREIGN KEY (Tutor) | Tutor principal responsável. |
| `nome` | VARCHAR(255) | NOT NULL | Nome do animal. |
| `especie` | VARCHAR(50) | NOT NULL | Ex: 'Cão', 'Gato'. |
| `raca` | VARCHAR(100) | | Raça do animal. |
| `sexo` | VARCHAR(10) | NOT NULL | 'Macho' ou 'Fêmea'. |
| `cor` | VARCHAR(50) | | Cor predominante. |
| `pelagem` | VARCHAR(50) | | Tipo de pelagem. |
| `data_nascimento` | DATE | | Data de nascimento ou estimativa. |
| `microchip` | VARCHAR(50) | UNIQUE | Número do microchip. |
| `alergias` | TEXT | | Alergias conhecidas. |
| `doencas_cronicas` | TEXT | | Doenças crônicas e medicações contínuas. |
| `foto_url` | VARCHAR(255) | | URL da foto de perfil do animal (armazenada no S3/GCS). |
| `data_add` | TIMESTAMP | DEFAULT NOW() | Data de criação do registro. |
| `data_alt` | TIMESTAMP | DEFAULT NOW() | Data da última alteração. |

---

## 5. Tabela: tutor_animal (Relacionamento N:N)

Permite vincular um animal a múltiplos tutores (tutores adicionais).

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `tutor_id` | BIGINT | FOREIGN KEY (Tutor) | Chave estrangeira para a tabela Tutor. |
| `animal_id` | BIGINT | FOREIGN KEY (Animal) | Chave estrangeira para a tabela Animal. |
| `relacao` | VARCHAR(50) | | Ex: 'Tutor Secundário', 'Passeador'. |
| `data_add` | TIMESTAMP | DEFAULT NOW() | Data de criação do registro. |
| `data_alt` | TIMESTAMP | DEFAULT NOW() | Data da última alteração. |
| **Chave Composta** | | PRIMARY KEY (tutor_id, animal_id) | Garante a unicidade do vínculo. |

---

## 6. Tabela: agendamento

Controla o fluxo de agendamentos de consultas, vacinas, etc.

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PRIMARY KEY | Identificador único do agendamento. |
| `clinica_id` | BIGINT | FOREIGN KEY (Clinica) | **Chave de Multitenancy.** |
| `animal_id` | BIGINT | FOREIGN KEY (Animal) | Animal agendado. |
| `veterinario_id` | BIGINT | FOREIGN KEY (Usuario) | Veterinário responsável pelo atendimento. |
| `data_hora_inicio` | TIMESTAMP | NOT NULL | Início do agendamento. |
| `data_hora_fim` | TIMESTAMP | NOT NULL | Fim do agendamento. |
| `tipo_atendimento` | VARCHAR(50) | NOT NULL | Ex: 'CONSULTA', 'RETORNO', 'VACINA', 'CIRURGIA'. |
| `status` | VARCHAR(50) | NOT NULL | Ex: 'AGENDADO', 'CONFIRMADO', 'ATENDIDO', 'FALTOU', 'CANCELADO'. |
| `observacoes` | TEXT | | Notas sobre o agendamento. |
| `data_add` | TIMESTAMP | DEFAULT NOW() | Data de criação do registro. |
| `data_alt` | TIMESTAMP | DEFAULT NOW() | Data da última alteração. |

---

## 7. Tabela: consulta ✅ Implementado (`V6__criar_tabelas_consulta.sql`)

Representa o registro do atendimento veterinário, vinculado a um agendamento. Imutável (RN006).

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PRIMARY KEY | Identificador único da consulta. |
| `clinica_id` | BIGINT | FOREIGN KEY (Clinica) | **Chave de Multitenancy.** |
| `agendamento_id` | BIGINT | FOREIGN KEY (Agendamento) | Agendamento que gerou a consulta. |
| `veterinario_id` | BIGINT | FOREIGN KEY (Usuario) | Veterinário que realizou o atendimento. |
| `motivo_consulta` | TEXT | NOT NULL | Motivo da visita. |
| `anamnese` | TEXT | | Histórico e informações coletadas. |
| `exame_fisico` | TEXT | | Resultados do exame físico (FC, FR, Tº, etc.). |
| `diagnostico` | TEXT | | Diagnóstico(s) do caso. |
| `conduta` | TEXT | | Plano de tratamento e orientações. |
| `historico_previo` | TEXT | | Histórico prévio e comorbidades relevantes. |
| `versao` | INTEGER | NOT NULL DEFAULT 1 | Versão atual do prontuário (RN006). |
| `data_add` | TIMESTAMP | DEFAULT NOW() | Data de criação do registro. |
| `data_alt` | TIMESTAMP | DEFAULT NOW() | Data da última alteração. |

---

## 7.1. Tabela: consulta_historico ✅ Implementado (`V6__criar_tabelas_consulta.sql`)

Armazena o histórico de alterações do prontuário para garantir a imutabilidade (RN006).

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PRIMARY KEY | Identificador único do registro de histórico. |
| `clinica_id` | BIGINT | FOREIGN KEY (Clinica) | **Chave de Multitenancy.** |
| `consulta_id` | BIGINT | FOREIGN KEY (Consulta) | Consulta original sendo versionada. |
| `usuario_alteracao_id` | BIGINT | FOREIGN KEY (Usuario) | Usuário que realizou a alteração. |
| `motivo_consulta` | TEXT | NOT NULL | Estado anterior do motivo. |
| `anamnese` | TEXT | | Estado anterior da anamnese. |
| `exame_fisico` | TEXT | | Estado anterior do exame físico. |
| `diagnostico` | TEXT | | Estado anterior do diagnóstico. |
| `conduta` | TEXT | | Estado anterior da conduta. |
| `historico_previo` | TEXT | | Estado anterior do histórico prévio. |
| `versao` | INTEGER | NOT NULL | Versão salva. |
| `data_alteracao` | TIMESTAMP | DEFAULT NOW() | Data em que a versão foi arquivada. |

---

## 8. Tabela: anexo

Armazena referências a arquivos (imagens, PDFs, vídeos) vinculados a uma consulta.

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PRIMARY KEY | Identificador único do anexo. |
| `clinica_id` | BIGINT | FOREIGN KEY (Clinica) | **Chave de Multitenancy.** |
| `consulta_id` | BIGINT | FOREIGN KEY (Consulta) | Consulta à qual o anexo pertence. |
| `nome_arquivo` | VARCHAR(255) | NOT NULL | Nome original do arquivo. |
| `url_armazenamento` | VARCHAR(512) | NOT NULL | URL do arquivo no S3/GCS. |
| `tipo_mime` | VARCHAR(100) | | Tipo MIME do arquivo (ex: image/jpeg, application/pdf). |
| `categoria` | VARCHAR(50) | NOT NULL | Categoria do anexo (ex: 'IDENTIFICACAO_PACIENTE', 'EXAME_PDF', 'FOTO_CLINICA'). |
| `deletado` | BOOLEAN | DEFAULT FALSE | Flag de exclusão lógica (Soft Delete). |
| `data_add` | TIMESTAMP | DEFAULT NOW() | Data de criação do registro. |
| `data_alt` | TIMESTAMP | DEFAULT NOW() | Data da última alteração. |

---

## 9. Tabela: peso_historico ✅ Implementado (`V7__criar_tabela_peso_historico.sql`)

Armazena o histórico de peso e Escala de Condição Corporal (ECC) do animal.

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PRIMARY KEY | Identificador único do registro. |
| `clinica_id` | BIGINT | FOREIGN KEY (Clinica) | **Chave de Multitenancy.** |
| `animal_id` | BIGINT | FOREIGN KEY (Animal) | Animal que teve o peso registrado. |
| `peso_kg` | NUMERIC(5, 2) | NOT NULL | Peso em quilogramas. |
| `ecc` | INTEGER | | Escala de Condição Corporal (1 a 9). |
| `usuario_registro_id` | BIGINT | FOREIGN KEY (Usuario) | Usuário que fez o registro. |
| `data_add` | TIMESTAMP | DEFAULT NOW() | Data de criação do registro. |
| `data_alt` | TIMESTAMP | DEFAULT NOW() | Data da última alteração. |

---

## 10. Tabela: vacina_aplicada

Armazena o registro de cada vacina aplicada no animal.

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PRIMARY KEY | Identificador único da aplicação. |
| `clinica_id` | BIGINT | FOREIGN KEY (Clinica) | **Chave de Multitenancy.** |
| `animal_id` | BIGINT | FOREIGN KEY (Animal) | Animal vacinado. |
| `nome_vacina` | VARCHAR(100) | NOT NULL | Nome comercial da vacina. |
| `data_aplicacao` | DATE | NOT NULL | Data em que a vacina foi aplicada. |
| `data_proximo_reforco` | DATE | | Data prevista para o próximo reforço. |
| `lote` | VARCHAR(50) | | Número do lote da vacina. |
| `fabricante` | VARCHAR(100) | | Fabricante da vacina. |
| `validade_vacina` | DATE | | Validade do produto. |
| `profissional_id` | BIGINT | FOREIGN KEY (Usuario) | Profissional que aplicou. |
| `data_add` | TIMESTAMP | DEFAULT NOW() | Data de criação do registro. |
| `data_alt` | TIMESTAMP | DEFAULT NOW() | Data da última alteração. |

---

## 11. Tabela: prescricao

Armazena o cabeçalho de uma prescrição (receita) gerada em uma consulta.

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PRIMARY KEY | Identificador único da prescrição. |
| `clinica_id` | BIGINT | FOREIGN KEY (Clinica) | **Chave de Multitenancy.** |
| `consulta_id` | BIGINT | FOREIGN KEY (Consulta) | Consulta que gerou a prescrição. |
| `veterinario_id` | BIGINT | FOREIGN KEY (Usuario) | Veterinário que prescreveu. |
| `tipo_receita` | VARCHAR(50) | NOT NULL | Ex: 'COMUM', 'CONTROLADA'. |
| `data_add` | TIMESTAMP | DEFAULT NOW() | Data de criação do registro. |
| `data_alt` | TIMESTAMP | DEFAULT NOW() | Data da última alteração. |
| `instrucoes_gerais` | TEXT | | Instruções adicionais ao tutor. |

---

## 12. Tabela: prescricao_item

Armazena os itens (medicamentos) de uma prescrição.

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PRIMARY KEY | Identificador único do item. |
| `prescricao_id` | BIGINT | FOREIGN KEY (Prescricao) | Prescrição à qual o item pertence. |
| `produto_id` | BIGINT | FOREIGN KEY (Produto) | Produto/Medicamento prescrito. |
| `dosagem` | VARCHAR(100) | NOT NULL | Dosagem (Ex: 5mg/kg). |
| `frequencia` | VARCHAR(100) | NOT NULL | Frequência (Ex: a cada 12h). |
| `duracao` | VARCHAR(100) | | Duração do tratamento. |
| `observacoes` | TEXT | | Observações específicas do item. |

---

## 13. Tabela: produto

Armazena o cadastro de medicamentos, materiais e produtos para venda/uso.

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PRIMARY KEY | Identificador único do produto. |
| `clinica_id` | BIGINT | FOREIGN KEY (Clinica) | **Chave de Multitenancy.** |
| `nome` | VARCHAR(255) | NOT NULL | Nome do produto/medicamento. |
| `tipo` | VARCHAR(50) | NOT NULL | Ex: 'MEDICAMENTO', 'MATERIAL', 'SERVICO'. |
| `unidade_medida` | VARCHAR(20) | | Ex: 'COMP', 'ML', 'UN'. |
| `estoque_minimo` | NUMERIC(10, 2) | DEFAULT 0 | Nível de alerta de estoque. |
| `preco_venda` | NUMERIC(10, 2) | | Preço de venda ao cliente. |
| `ativo` | BOOLEAN | DEFAULT TRUE | Indica se o produto está ativo. |

---

## 14. Tabela: estoque_movimento

Registra todas as entradas e saídas de estoque (baixa automática por vacina, uso em cirurgia, venda, compra).

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PRIMARY KEY | Identificador único do movimento. |
| `clinica_id` | BIGINT | FOREIGN KEY (Clinica) | **Chave de Multitenancy.** |
| `produto_id` | BIGINT | FOREIGN KEY (Produto) | Produto movimentado. |
| `tipo_movimento` | VARCHAR(50) | NOT NULL | Ex: 'ENTRADA', 'SAIDA', 'AJUSTE'. |
| `quantidade` | NUMERIC(10, 2) | NOT NULL | Quantidade movimentada. |
| `lote` | VARCHAR(50) | | Lote do produto (se aplicável). |
| `validade` | DATE | | Validade do produto (se aplicável). |
| `referencia_id` | BIGINT | | ID da transação que gerou o movimento (Ex: `consulta_id`, `venda_id`). |
| `data_add` | TIMESTAMP | DEFAULT NOW() | Data de criação do registro. |
| `data_alt` | TIMESTAMP | DEFAULT NOW() | Data da última alteração. |
| `usuario_id` | BIGINT | FOREIGN KEY (Usuario) | Usuário que registrou o movimento. |

---

## 15. Tabela: fatura

Representa o registro financeiro de um serviço ou venda.

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PRIMARY KEY | Identificador único da fatura. |
| `clinica_id` | BIGINT | FOREIGN KEY (Clinica) | **Chave de Multitenancy.** |
| `tutor_id` | BIGINT | FOREIGN KEY (Tutor) | Tutor responsável pelo pagamento. |
| `data_emissao` | TIMESTAMP | DEFAULT NOW() | Data de emissão da fatura. |
| `valor_total` | NUMERIC(10, 2) | NOT NULL | Valor total da fatura. |
| `status` | VARCHAR(50) | NOT NULL | Ex: 'PENDENTE', 'PAGO', 'CANCELADO'. |
| `referencia_id` | BIGINT | | ID da transação que gerou a fatura (Ex: `consulta_id`, `cirurgia_id`). |

---

## 16. Tabela: pagamento

Registra cada pagamento efetuado para uma fatura.

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PRIMARY KEY | Identificador único do pagamento. |
| `fatura_id` | BIGINT | FOREIGN KEY (Fatura) | Fatura paga. |
| `valor_pago` | NUMERIC(10, 2) | NOT NULL | Valor do pagamento. |
| `forma_pagamento` | VARCHAR(50) | NOT NULL | Ex: 'PIX', 'CARTAO_CREDITO', 'DINHEIRO'. |
| `data_add` | TIMESTAMP | DEFAULT NOW() | Data de criação do registro. |
| `data_alt` | TIMESTAMP | DEFAULT NOW() | Data da última alteração. |
| `usuario_registro_id` | BIGINT | FOREIGN KEY (Usuario) | Usuário que registrou o pagamento. |

---

## 17. Tabela: configuracao

Armazena configurações específicas da clínica (protocolos vacinais, textos padrão, logo).

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PRIMARY KEY | Identificador único da configuração. |
| `clinica_id` | BIGINT | FOREIGN KEY (Clinica) | **Chave de Multitenancy.** |
| `chave` | VARCHAR(100) | NOT NULL | Chave da configuração (Ex: 'PROTOCOLO_VACINAL_CAO', 'LEMBRETE_VACINA_DIAS_ANTECEDENCIA'). |
| `valor` | JSONB | | Valor da configuração (JSON para flexibilidade). Ex: `{"dias_antecedencia": 3}`. |
| **Chave Composta** | | UNIQUE (clinica_id, chave) | Garante que cada clínica tenha apenas uma configuração por chave. |

---

## 18. Tabela: auditoria_log

Armazena o histórico de alterações em dados sensíveis (LGPD e segurança). **Tabela de auditoria imutável (Append-Only), com triggers de banco de dados bloqueando qualquer UPDATE ou DELETE.**

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PRIMARY KEY | Identificador único do log. |
| `clinica_id` | BIGINT | FOREIGN KEY (Clinica) | **Chave de Multitenancy.** |
| `usuario_id` | BIGINT | FOREIGN KEY (Usuario) | Usuário que realizou a ação. |
| `tabela_afetada` | VARCHAR(100) | NOT NULL | Tabela onde a alteração ocorreu. |
| `registro_id` | BIGINT | NOT NULL | ID do registro afetado. |
| `tipo_acao` | VARCHAR(10) | NOT NULL | Ex: 'INSERT', 'UPDATE', 'DELETE'. |
| `dados_antigos` | JSONB | | Dados antes da alteração. |
| `dados_novos` | JSONB | | Dados após a alteração. |
| `data_add` | TIMESTAMP | DEFAULT NOW() | Data de criação do registro. |
| `data_alt` | TIMESTAMP | DEFAULT NOW() | Data da última alteração. |

---

## 19. Tabela: comunicacao_template

Armazena os modelos de mensagens automáticas (ex: lembrete de vacina, lembrete de consulta).

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PRIMARY KEY | Identificador único do template. |
| `clinica_id` | BIGINT | FOREIGN KEY (clinica) | Chave de Multitenancy. |
| `chave` | VARCHAR(100) | NOT NULL | Chave de identificação (Ex: 'LEMBRETE_VACINA_3_DIAS'). |
| `titulo` | VARCHAR(255) | NOT NULL | Título do template. |
| `conteudo` | TEXT | NOT NULL | Conteúdo do template (com placeholders como {animal_nome}). |
| `data_add` | TIMESTAMP | DEFAULT NOW() | Data de criação do registro. |
| `data_alt` | TIMESTAMP | DEFAULT NOW() | Data da última alteração. |

---

## 20. Tabela: comunicacao_massa

Armazena as mensagens criadas pelos veterinários/administradores para envio em massa.

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PRIMARY KEY | Identificador único da mensagem em massa. |
| `clinica_id` | BIGINT | FOREIGN KEY (clinica) | Chave de Multitenancy. |
| `usuario_criacao_id` | BIGINT | FOREIGN KEY (usuario) | Usuário que criou a mensagem. |
| `titulo` | VARCHAR(255) | NOT NULL | Título interno da campanha. |
| `conteudo_base` | TEXT | NOT NULL | Conteúdo da mensagem antes da personalização. |
| `status_campanha` | VARCHAR(50) | NOT NULL | Ex: 'RASCUNHO', 'AGENDADA', 'ENVIANDO', 'CONCLUIDA'. |
| `data_agendamento` | TIMESTAMP | | Data e hora para envio. |
| `data_add` | TIMESTAMP | DEFAULT NOW() | Data de criação do registro. |
| `data_alt` | TIMESTAMP | DEFAULT NOW() | Data da última alteração. |

---

## 21. Tabela: comunicacao_historico

Armazena o histórico de todas as mensagens enviadas (automáticas e em massa) aos tutores.

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PRIMARY KEY | Identificador único do histórico. |
| `clinica_id` | BIGINT | FOREIGN KEY (clinica) | Chave de Multitenancy. |
| `tutor_id` | BIGINT | FOREIGN KEY (tutor) | Tutor que recebeu a mensagem. |
| `tipo_mensagem` | VARCHAR(50) | NOT NULL | Ex: 'VACINA_LEMBRETE', 'CONSULTA_LEMBRETE', 'MENSAGEM_MASSA'. |
| `conteudo` | TEXT | NOT NULL | Conteúdo completo da mensagem enviada. |
| `status_envio` | VARCHAR(50) | NOT NULL | Ex: 'ENVIADO', 'FALHA', 'LIDO'. |
| `referencia_id` | BIGINT | | ID da transação que gerou a mensagem (Ex: `vacina_aplicada.id`). |
| `data_add` | TIMESTAMP | DEFAULT NOW() | Data de envio da mensagem. |
| `data_alt` | TIMESTAMP | DEFAULT NOW() | Data da última alteração (status). |

---

## Relacionamentos Chave (Resumo)

*   **Multitenancy:** Todas as tabelas transacionais (Tutor, Animal, Agendamento, Consulta, etc.) se relacionam com `Clinica(id)` via `clinica_id`.
*   **Tutor-Animal:** `Animal` se relaciona com `Tutor` via `tutor_principal_id`. O relacionamento N:N é feito pela tabela `TutorAnimal`.
*   **Atendimento:** `Agendamento` -> `Consulta` -> `Prescricao` -> `PrescricaoItem`.
*   **Estoque:** `PrescricaoItem` e outras transações se relacionam com `Produto`. `EstoqueMovimento` registra as mudanças no estoque.
*   **Financeiro:** `Fatura` se relaciona com `Tutor`. `Pagamento` se relaciona com `Fatura`.

Este modelo de dados cobre todos os requisitos do MVP (Cadastro, Agenda, Prontuário Básico, Vacinas, Peso, Prescrição, Estoque Básico e Financeiro Básico) e está pronto para ser implementado no PostgreSQL.

---

## 22. Tabela: consulta_historico

Armazena o histórico de alterações do prontuário (tabela `consulta`), garantindo rastreabilidade e auditoria detalhada. **Tabela de histórico imutável (Append-Only), com triggers de banco de dados bloqueando qualquer UPDATE ou DELETE.**

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PRIMARY KEY | Identificador único do registro de histórico. |
| `clinica_id` | BIGINT | FOREIGN KEY (clinica) | Chave de Multitenancy. |
| `consulta_id` | BIGINT | FOREIGN KEY (consulta) | Consulta original que foi alterada. |
| `usuario_alteracao_id` | BIGINT | FOREIGN KEY (usuario) | Usuário que realizou a alteração. |
| `motivo_alteracao` | TEXT | | Breve descrição do motivo da alteração. |
| `dados_anteriores` | JSONB | NOT NULL | Cópia completa dos dados da consulta antes da alteração. |
| `data_add` | TIMESTAMP | DEFAULT NOW() | Data e hora da alteração. |
| `data_alt` | TIMESTAMP | DEFAULT NOW() | Data da última alteração. |

---

## 23. Tabela: plano

Armazena as definições de planos comercializados na plataforma SaaS.

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PRIMARY KEY | Identificador único do plano. |
| `nome` | VARCHAR(100) | UNIQUE, NOT NULL | Nome comercial (ex: 'Bronze', 'Prata', 'Ouro'). |
| `preco` | NUMERIC(10, 2) | NOT NULL | Valor mensal da assinatura. |
| `limite_usuarios` | INTEGER | | Limite de usuários ativos na clínica. |
| `limite_armazenamento_bytes` | BIGINT | | Limite de bytes para arquivos de anexos. |
| `limite_mensagens` | INTEGER | | Limite de envios de mensagens mensais via WhatsApp. |
| `ativo` | BOOLEAN | DEFAULT TRUE | Indica se o plano está sendo comercializado. |
| `deletado` | BOOLEAN | DEFAULT FALSE | Flag de exclusão lógica (Soft Delete). |

---

## 24. Tabela: assinatura

Armazena as assinaturas ativas e histórico comercial das clínicas contratantes.

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PRIMARY KEY | Identificador único da assinatura. |
| `clinica_id` | BIGINT | FOREIGN KEY (Clinica) | Clínica associada à assinatura. |
| `plano_id` | BIGINT | FOREIGN KEY (Plano) | Plano contratado. |
| `status` | VARCHAR(50) | NOT NULL | Status ('ATIVA', 'TRIAL', 'ATRASADA', 'BLOQUEADA', 'CANCELADA'). |
| `data_inicio` | TIMESTAMP | NOT NULL | Data de início da vigência. |
| `data_fim_vigencia` | TIMESTAMP | | Data de término da vigência atual. |
| `data_fim_trial` | TIMESTAMP | | Data de término da fase gratuita de testes. |
| `deletado` | BOOLEAN | DEFAULT FALSE | Flag de exclusão lógica (Soft Delete). |
| `data_add` | TIMESTAMP | DEFAULT NOW() | Data de criação do registro. |
| `data_alt` | TIMESTAMP | DEFAULT NOW() | Data da última alteração. |

---

## 25. Tabela: recuperacao_senha_token

Armazena os tokens temporários gerados para fluxo de recuperação de credenciais de usuários.

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PRIMARY KEY | Identificador único do token. |
| `usuario_id` | BIGINT | FOREIGN KEY (Usuario) | Usuário que solicitou a recuperação. |
| `token` | VARCHAR(255) | UNIQUE, NOT NULL | Token criptograficamente seguro gerado pela aplicação. |
| `data_expiracao` | TIMESTAMP | NOT NULL | Data e hora limite para uso. |
| `usado` | BOOLEAN | DEFAULT FALSE | Indica se o token já foi consumido. |
| `data_add` | TIMESTAMP | DEFAULT NOW() | Data de solicitação. |

---

## 26. Tabela: log_erro

Armazena o registro de exceções técnicas ocorridas na execução da API para suporte e depuração.

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PRIMARY KEY | Identificador único do log. |
| `clinica_id` | BIGINT | FOREIGN KEY (Clinica), Nullable | Clínica ativa no momento do erro. |
| `usuario_id` | BIGINT | FOREIGN KEY (Usuario), Nullable | Usuário que disparou a ação. |
| `classe_excecao` | VARCHAR(255) | NOT NULL | Nome da classe Java da exceção. |
| `mensagem` | TEXT | | Mensagem amigável de erro. |
| `stack_trace` | TEXT | | Stack trace completo da falha técnica. |
| `endpoint` | VARCHAR(255) | | Caminho do endpoint acessado (ex: '/api/animais'). |
| `metodo_http` | VARCHAR(10) | | Método da requisição HTTP (ex: 'GET', 'POST'). |
| `ip_cliente` | VARCHAR(50) | | IP de onde partiu a requisição. |
| `data_add` | TIMESTAMP | DEFAULT NOW() | Data e hora do registro da falha. |

---

## 27. Tabela: caixa_diario

Controla o estado de abertura, fechamento e fluxo financeiro diário de cada clínica.

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PRIMARY KEY | Identificador único da sessão de caixa. |
| `clinica_id` | BIGINT | FOREIGN KEY (Clinica) | Clínica proprietária do caixa. |
| `usuario_abertura_id` | BIGINT | FOREIGN KEY (Usuario) | Usuário que realizou a abertura do dia. |
| `usuario_fechamento_id` | BIGINT | FOREIGN KEY (Usuario), Nullable | Usuário que encerrou o caixa do dia. |
| `saldo_inicial` | NUMERIC(10, 2) | NOT NULL | Saldo de abertura em caixa. |
| `saldo_final` | NUMERIC(10, 2) | | Saldo no encerramento (verificado). |
| `status` | VARCHAR(20) | NOT NULL | Status do caixa ('ABERTO', 'FECHADO'). |
| `data_abertura` | TIMESTAMP | DEFAULT NOW() | Data/Hora de abertura. |
| `data_fechamento` | TIMESTAMP | | Data/Hora de encerramento. |
| `deletado` | BOOLEAN | DEFAULT FALSE | Flag de exclusão lógica (Soft Delete). |

---

## 28. Tabela: caixa_movimentacao

Registra as entradas e saídas detalhadas de dinheiro associadas a um caixa diário ativo.

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PRIMARY KEY | Identificador único da movimentação. |
| `clinica_id` | BIGINT | FOREIGN KEY (Clinica) | Clínica da movimentação. |
| `caixa_diario_id` | BIGINT | FOREIGN KEY (caixa_diario) | Sessão de caixa ativa correspondente. |
| `usuario_id` | BIGINT | FOREIGN KEY (Usuario) | Operador responsável pelo lançamento. |
| `tipo_movimentacao` | VARCHAR(20) | NOT NULL | 'ENTRADA' (recebimento, reforço) ou 'SAIDA' (sangria, despesa). |
| `valor` | NUMERIC(10, 2) | NOT NULL | Valor monetário movimentado. |
| `motivo` | VARCHAR(255) | NOT NULL | Descrição ou observações da movimentação. |
| `referencia_pagamento_id` | BIGINT | FOREIGN KEY (Pagamento), Nullable | Conexão com o pagamento de fatura, se aplicável. |
| `deletado` | BOOLEAN | DEFAULT FALSE | Flag de exclusão lógica (Soft Delete). |
| `data_add` | TIMESTAMP | DEFAULT NOW() | Data e hora do lançamento. |

---

## 29. Tabela: servico

Cadastro de procedimentos e consultas veterinárias oferecidos comercialmente pela clínica.

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PRIMARY KEY | Identificador único do serviço. |
| `clinica_id` | BIGINT | FOREIGN KEY (Clinica) | Clínica que oferece o serviço. |
| `nome` | VARCHAR(255) | NOT NULL | Nome do serviço/procedimento. |
| `preco` | NUMERIC(10, 2) | NOT NULL | Preço padrão cobrado pela clínica. |
| `ativo` | BOOLEAN | DEFAULT TRUE | Indica se o serviço está ativo para novos agendamentos. |
| `deletado` | BOOLEAN | DEFAULT FALSE | Flag de exclusão lógica (Soft Delete). |
| `data_add` | TIMESTAMP | DEFAULT NOW() | Data de cadastro. |
| `data_alt` | TIMESTAMP | DEFAULT NOW() | Data da última alteração. |

---

## 30. Tabela: documento_emitido

Registra e rastreia os documentos e laudos médicos gerados em formato PDF pelo sistema.

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PRIMARY KEY | Identificador único da emissão. |
| `clinica_id` | BIGINT | FOREIGN KEY (Clinica) | Clínica emissora. |
| `usuario_id` | BIGINT | FOREIGN KEY (Usuario) | Usuário que solicitou a geração do PDF. |
| `tipo_documento` | VARCHAR(50) | NOT NULL | Tipo ('RECEITA', 'LAUDO', 'FATURA'). |
| `referencia_id` | BIGINT | NOT NULL | ID do registro referenciado (ex: `consulta.id` ou `fatura.id`). |
| `hash_documento` | VARCHAR(255) | NOT NULL | Hash sha-256 do arquivo gerado para auditoria e controle de integridade. |
| `deletado` | BOOLEAN | DEFAULT FALSE | Flag de exclusão lógica (Soft Delete). |
| `data_add` | TIMESTAMP | DEFAULT NOW() | Data de geração do documento. |

---

## Relacionamentos Chave (Resumo - Atualizado)

*   **Multitenancy:** Todas as tabelas transacionais e operacionais de inquilinos se relacionam com `clinica(id)` via `clinica_id`.
*   **Comercialização:** `assinatura` vincula `clinica` com seu `plano` contratado e limita seus recursos.
*   **Controle de Caixa:** `caixa_movimentacao` depende de um `caixa_diario` aberto, que por sua vez se conecta às operações de recebimento financeiro (`pagamento`).
*   **Auditoria de PDFs:** `documento_emitido` armazena referências aos laudos e receitas clínicas gerados, mantendo o controle de autenticidade dos dados impressos/exportados.
*   **Logs Técnicos:** `log_erro` armazena stack traces e dados técnicos das falhas para monitoramento, sem vincular rigidamente ao tenant (nulo em erros de sistema global).

Este modelo de dados, agora com **30 tabelas**, está completo e robusto para o sistema comercial multi-inquilino do **PetVital**.
