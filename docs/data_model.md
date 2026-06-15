# Modelagem de Dados - VetSync (MVP)

## Arquitetura: Multi-Inquilino (Multitenancy)

O sistema VetSync será construído com uma arquitetura Multi-Inquilino, onde um único banco de dados PostgreSQL servirá a múltiplas clínicas. O isolamento de dados será garantido pela chave estrangeira `clinica_id` em todas as tabelas transacionais.

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

Armazena os dados de cada clínica (inquilino) que utiliza o sistema.

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | SERIAL | PRIMARY KEY | Identificador único do inquilino (clínica/autônomo). |
| `tipo_tenant` | VARCHAR(20) | NOT NULL | Tipo de Inquilino: 'PJ' (Clínica) ou 'PF' (Autônomo). |
| `nome` | VARCHAR(255) | NOT NULL | Nome fantasia (PJ) ou Nome completo (PF). |
| `razao_social` | VARCHAR(255) | | Razão social (Apenas para PJ). |
| `documento_fiscal` | VARCHAR(18) | UNIQUE, NOT NULL | CNPJ (se PJ) ou CPF (se PF). |
| `crmv` | VARCHAR(20) | | CRMV (Apenas para PF/Autônomo). |
| `email` | VARCHAR(255) | UNIQUE | E-mail de contato principal. |
| `telefone` | VARCHAR(20) | | Telefone de contato. |
| `ativo` | BOOLEAN | DEFAULT TRUE | Indica se o inquilino está ativo no sistema. |
| `whatsapp_api_token` | VARCHAR(255) | | Token de acesso à API do WhatsApp Business. |
| `whatsapp_numero` | VARCHAR(20) | | Número de telefone configurado para a API. |
| `data_add` | TIMESTAMP | DEFAULT NOW() | Data de criação do registro. |
| `data_alt` | TIMESTAMP | DEFAULT NOW() | Data da última alteração. |

---

## 2. Tabela: usuario

Armazena os dados dos usuários que acessam o sistema (Administrador, Veterinário, Auxiliar, Recepção, Financeiro).

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | SERIAL | PRIMARY KEY | Identificador único do usuário. |
| `clinica_id` | INTEGER | FOREIGN KEY (Clinica) | **Chave de Multitenancy.** Liga o usuário à sua clínica. |
| `nome` | VARCHAR(255) | NOT NULL | Nome completo do usuário. |
| `email` | VARCHAR(255) | UNIQUE, NOT NULL | E-mail de login. |
| `senha_hash` | VARCHAR(255) | NOT NULL | Hash da senha (nunca a senha em texto claro). |
| `perfil` | VARCHAR(50) | NOT NULL | Perfil de acesso (Ex: 'ADMIN', 'VETERINARIO', 'RECEPCAO'). |
| `crmv` | VARCHAR(20) | | Número do CRMV (apenas para Veterinários). |
| `ativo` | BOOLEAN | DEFAULT TRUE | Indica se o usuário está ativo. |
| `data_add` | TIMESTAMP | DEFAULT NOW() | Data de criação do registro. |
| `data_alt` | TIMESTAMP | DEFAULT NOW() | Data da última alteração. |

---

**Próximo Passo:** Modelar as tabelas `Tutor` e `Animal`, garantindo que ambas possuam a chave `clinica_id` para manter o isolamento de dados.

---

## 3. Tabela: tutor

Armazena os dados completos dos tutores (clientes) da clínica.

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | SERIAL | PRIMARY KEY | Identificador único do tutor. |
| `clinica_id` | INTEGER | FOREIGN KEY (Clinica) | **Chave de Multitenancy.** Liga o tutor à sua clínica. |
| `nome` | VARCHAR(255) | NOT NULL | Nome completo do tutor. |
| `cpf` | VARCHAR(14) | UNIQUE | CPF do tutor (para NF e identificação). |
| `rg` | VARCHAR(20) | | RG do tutor. |
| `data_nascimento` | DATE | | Data de nascimento. |
| `telefone_principal` | VARCHAR(20) | NOT NULL | Telefone principal (WhatsApp). |
| `email` | VARCHAR(255) | | E-mail para contato. |
| `endereco_logradouro` | VARCHAR(255) | | Rua, Avenida, etc. |
| `endereco_numero` | VARCHAR(20) | | Número do imóvel. |
| `endereco_complemento` | VARCHAR(100) | | Complemento (apto, bloco). |
| `endereco_bairro` | VARCHAR(100) | | Bairro. |
| `endereco_cidade` | VARCHAR(100) | | Cidade. |
| `endereco_estado` | VARCHAR(2) | | Estado (UF). |
| `endereco_cep` | VARCHAR(10) | | CEP. |
| `preferencia_contato` | VARCHAR(50) | DEFAULT 'WHATSAPP' | Ex: 'WHATSAPP', 'EMAIL', 'TELEFONE'. |
| `observacoes` | TEXT | | Alertas ou restrições importantes. |
| `aceita_comunicacao_informativa` | BOOLEAN | DEFAULT TRUE | Flag para mensagens não essenciais (propaganda, conteúdo). |
| `data_add` | TIMESTAMP | DEFAULT NOW() | Data de criação do registro. |
| `data_alt` | TIMESTAMP | DEFAULT NOW() | Data da última alteração. |

---

## 4. Tabela: animal

Armazena os dados dos animais (pacientes) vinculados a um tutor.

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | SERIAL | PRIMARY KEY | Identificador único do animal. |
| `clinica_id` | INTEGER | FOREIGN KEY (Clinica) | **Chave de Multitenancy.** Liga o animal à sua clínica. |
| `tutor_principal_id` | INTEGER | FOREIGN KEY (Tutor) | Tutor principal responsável. |
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
| `foto_url` | VARCHAR(255) | | URL da foto de perfil do animal (armazenada no S3/GCS)| `data_add` | TIMESTAMP | DEFAULT NOW() | Data de criação do registro. |
| `data_alt` | TIMESTAMP | DEFAULT NOW() | Data da última alteração. || `data_alt` | TIMESTAMP | DEFAULT NOW() | Data da última alteração. |

---

## 5. Tabela: tutor_animal (Relacionamento N:N)

Permite vincular um animal a múltiplos tutores (tutores adicionais).

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `tutor_id` | INTEGER | FOREIGN KEY (Tutor) | Chave estrangeira para a tabela Tutor. |
| `animal_id` | INTEGER | FOREIGN KEY (Animal) | Chave estrangeira para a tabela Animal. |
| `relacao` | VARCHAR(50) | | Ex: 'Tutor Secundário', 'Passeador'. |
| `data_add` | TIMESTAMP | DEFAULT NOW() | Data de criação do registro. |
| `data_alt` | TIMESTAMP | DEFAULT NOW() | Data da última alteração. |
| **Chave Composta** | | PRIMARY KEY (tutor_id, animal_id) | Garante a unicidade do vínculo. |

---

## 6. Tabela: agendamento

Controla o fluxo de agendamentos de consultas, vacinas, etc.

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | SERIAL | PRIMARY KEY | Identificador único do agendamento. |
| `clinica_id` | INTEGER | FOREIGN KEY (Clinica) | **Chave de Multitenancy.** |
| `animal_id` | INTEGER | FOREIGN KEY (Animal) | Animal agendado. |
| `veterinario_id` | INTEGER | FOREIGN KEY (Usuario) | Veterinário responsável pelo atendimento. |
| `data_hora_inicio` | TIMESTAMP | NOT NULL | Início do agendamento. |
| `data_hora_fim` | TIMESTAMP | NOT NULL | Fim do agendamento. |
| `tipo_atendimento` | VARCHAR(50) | NOT NULL | Ex: 'CONSULTA', 'RETORNO', 'VACINA', 'CIRURGIA'. |
| `status` | VARCHAR(50) | NOT NULL | Ex: 'AGENDADO', 'CONFIRMADO', 'ATENDIDO', 'FALTOU', 'CANCELADO'. |
| `observacoes` | TEXT | | Notas sobre o agendamento. |
| `data_add` | TIMESTAMP | DEFAULT NOW() | Data de criação do registro. |
| `data_alt` | TIMESTAMP | DEFAULT NOW() | Data da última alteração. |

---

## 7. Tabela: consulta

Representa o registro do atendimento veterinário, vinculado a um agendamento.

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | SERIAL | PRIMARY KEY | Identificador único da consulta. |
| `clinica_id` | INTEGER | FOREIGN KEY (Clinica) | **Chave de Multitenancy.** |
| `agendamento_id` | INTEGER | FOREIGN KEY (Agendamento) | Agendamento que gerou a consulta. |
| `veterinario_id` | INTEGER | FOREIGN KEY (Usuario) | Veterinário que realizou o atendimento. |
| `data_add` | TIMESTAMP | DEFAULT NOW() | Data de criação do registro. |
| `data_alt` | TIMESTAMP | DEFAULT NOW() | Data da última alteração. |
| `motivo_consulta` | TEXT | NOT NULL | Motivo da visita. |
| `anamnese` | TEXT | | Histórico e informações coletadas. |
| `exame_fisico` | TEXT | | Resultados do exame físico (FC, FR, Tº, etc.). |
| `diagnostico` | TEXT | | Diagnóstico(s) do caso. |
| `conduta` | TEXT | | Plano de tratamento e orientações. |
| `historico_previo` | TEXT | | Histórico prévio e comorbidades relevantes. |
| `data_alt` | TIMESTAMP | DEFAULT NOW() | Data da última alteração. |

---

## 8. Tabela: anexo

Armazena referências a arquivos (imagens, PDFs, vídeos) vinculados a uma consulta.

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | SERIAL | PRIMARY KEY | Identificador único do anexo. |
| `clinica_id` | INTEGER | FOREIGN KEY (Clinica) | **Chave de Multitenancy.** |
| `consulta_id` | INTEGER | FOREIGN KEY (Consulta) | Consulta à qual o anexo pertence. |
| `nome_arquivo` | VARCHAR(255) | NOT NULL | Nome original do arquivo. |
| `url_armazenamento` | VARCHAR(512) | NOT NULL | URL do arquivo no S3/GCS. |
| `tipo_mime` | VARCHAR(100) | | Tipo MIME do arquivo (ex: image/jpeg, application/pdf). |
| `data_add` | TIMESTAMP | DEFAULT NOW() | Data de criação do registro. |
| `data_alt` | TIMESTAMP | DEFAULT NOW() | Data da última alteração. |

---

## 9. Tabela: peso_historico

Armazena o histórico de peso e Escala de Condição Corporal (ECC) do animal.

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | SERIAL | PRIMARY KEY | Identificador único do registro. |
| `clinica_id` | INTEGER | FOREIGN KEY (Clinica) | **Chave de Multitenancy.** |
| `animal_id` | INTEGER | FOREIGN KEY (Animal) | Animal que teve o peso registrado. |
| `peso_kg` | NUMERIC(5, 2) | NOT NULL | Peso em quilogramas. |
| `ecc` | INTEGER | | Escala de Condição Corporal (1 a 9). | `data_add` | TIMESTAMP | DEFAULT NOW() | Data de criação do registro. |
| `data_alt` | TIMESTAMP | DEFAULT NOW() | Data da última alteração. | `usuario_registro_id` | INTEGER | FOREIGN KEY (Usuario) | Usuário que fez o registro. |

---

## 10. Tabela: vacina_aplicada

Armazena o registro de cada vacina aplicada no animal.

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | SERIAL | PRIMARY KEY | Identificador único da aplicação. |
| `clinica_id` | INTEGER | FOREIGN KEY (Clinica) | **Chave de Multitenancy.** |
| `animal_id` | INTEGER | FOREIGN KEY (Animal) | Animal vacinado. |
| `nome_vacina` | VARCHAR(100) | NOT NULL | Nome comercial da vacina. |
| `data_aplicacao` | DATE | NOT NULL | Data em que a vacina foi aplicada. |
| `data_proximo_reforco` | DATE | | Data prevista para o próximo reforço. |
| `lote` | VARCHAR(50) | | Número do lote da vacina. |
| `fabricante` | VARCHAR(100) | | Fabricante da vacina. |
| `validade_vacina` | DATE | | Validade do produto. |
| `profissional_id` | INTEGER | FOREIGN KEY (Usuario) | Profissional que aplicou. |
| `data_add` | TIMESTAMP | DEFAULT NOW() | Data de criação do registro. |
| `data_alt` | TIMESTAMP | DEFAULT NOW() | Data da última alteração. |

---

## 11. Tabela: prescricao

Armazena o cabeçalho de uma prescrição (receita) gerada em uma consulta.

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | SERIAL | PRIMARY KEY | Identificador único da prescrição. |
| `clinica_id` | INTEGER | FOREIGN KEY (Clinica) | **Chave de Multitenancy.** |
| `consulta_id` | INTEGER | FOREIGN KEY (Consulta) | Consulta que gerou a prescrição. |
| `veterinario_id` | INTEGER | FOREIGN KEY (Usuario) | Veterinário que prescreveu. |
| `tipo_receita` | VARCHAR(50) | NOT NULL | Ex: 'COMUM', 'CONTROLADA'. |
| `data_add` | TIMESTAMP | DEFAULT NOW() | Data de criação do registro. |
| `data_alt` | TIMESTAMP | DEFAULT NOW() | Data da última alteração. |
| `instrucoes_gerais` | TEXT | | Instruções adicionais ao tutor. |

---

## 12. Tabela: prescricao_item

Armazena os itens (medicamentos) de uma prescrição.

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | SERIAL | PRIMARY KEY | Identificador único do item. |
| `prescricao_id` | INTEGER | FOREIGN KEY (Prescricao) | Prescrição à qual o item pertence. |
| `produto_id` | INTEGER | FOREIGN KEY (Produto) | Produto/Medicamento prescrito. |
| `dosagem` | VARCHAR(100) | NOT NULL | Dosagem (Ex: 5mg/kg). |
| `frequencia` | VARCHAR(100) | NOT NULL | Frequência (Ex: a cada 12h). |
| `duracao` | VARCHAR(100) | | Duração do tratamento. |
| `observacoes` | TEXT | | Observações específicas do item. |

---

## 13. Tabela: produto

Armazena o cadastro de medicamentos, materiais e produtos para venda/uso.

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | SERIAL | PRIMARY KEY | Identificador único do produto. |
| `clinica_id` | INTEGER | FOREIGN KEY (Clinica) | **Chave de Multitenancy.** |
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
| `id` | SERIAL | PRIMARY KEY | Identificador único do movimento. |
| `clinica_id` | INTEGER | FOREIGN KEY (Clinica) | **Chave de Multitenancy.** |
| `produto_id` | INTEGER | FOREIGN KEY (Produto) | Produto movimentado. |
| `tipo_movimento` | VARCHAR(50) | NOT NULL | Ex: 'ENTRADA', 'SAIDA', 'AJUSTE'. |
| `quantidade` | NUMERIC(10, 2) | NOT NULL | Quantidade movimentada. |
| `lote` | VARCHAR(50) | | Lote do produto (se aplicável). |
| `validade` | DATE | | Validade do produto (se aplicável). |
| `referencia_id` | INTEGER | | ID da transação que gerou o movimento (Ex: `consulta_id`, `venda_id`). |
| `data_add` | TIMESTAMP | DEFAULT NOW() | Data de criação do registro. |
| `data_alt` | TIMESTAMP | DEFAULT NOW() | Data da última alteração. |
| `usuario_id` | INTEGER | FOREIGN KEY (Usuario) | Usuário que registrou o movimento. |

---

## 15. Tabela: fatura

Representa o registro financeiro de um serviço ou venda.

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | SERIAL | PRIMARY KEY | Identificador único da fatura. |
| `clinica_id` | INTEGER | FOREIGN KEY (Clinica) | **Chave de Multitenancy.** |
| `tutor_id` | INTEGER | FOREIGN KEY (Tutor) | Tutor responsável pelo pagamento. |
| `data_emissao` | TIMESTAMP | DEFAULT NOW() | Data de emissão da fatura. |
| `valor_total` | NUMERIC(10, 2) | NOT NULL | Valor total da fatura. |
| `status` | VARCHAR(50) | NOT NULL | Ex: 'PENDENTE', 'PAGO', 'CANCELADO'. |
| `referencia_id` | INTEGER | | ID da transação que gerou a fatura (Ex: `consulta_id`, `cirurgia_id`). |

---

## 16. Tabela: pagamento

Registra cada pagamento efetuado para uma fatura.

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | SERIAL | PRIMARY KEY | Identificador único do pagamento. |
| `fatura_id` | INTEGER | FOREIGN KEY (Fatura) | Fatura paga. |
| `valor_pago` | NUMERIC(10, 2) | NOT NULL | Valor do pagamento. |
| `forma_pagamento` | VARCHAR(50) | NOT NULL | Ex: 'PIX', 'CARTAO_CREDITO', 'DINHEIRO'. |
| `data_add` | TIMESTAMP | DEFAULT NOW() | Data de criação do registro. |
| `data_alt` | TIMESTAMP | DEFAULT NOW() | Data da última alteração. |
| `usuario_registro_id` | INTEGER | FOREIGN KEY (Usuario) | Usuário que registrou o pagamento. |

---

## 17. Tabela: configuracao

Armazena configurações específicas da clínica (protocolos vacinais, textos padrão, logo).

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | SERIAL | PRIMARY KEY | Identificador único da configuração. |
| `clinica_id` | INTEGER | FOREIGN KEY (Clinica) | **Chave de Multitenancy.** |
| `chave` | VARCHAR(100) | NOT NULL | Chave da configuração (Ex: 'PROTOCOLO_VACINAL_CAO', 'LEMBRETE_VACINA_DIAS_ANTECEDENCIA'). |}],path:
| `valor` | JSONB | | Valor da configuração (JSON para flexibilidade). Ex: `{"dias_antecedencia": 3}`. |}],path:
| **Chave Composta** | | UNIQUE (clinica_id, chave) | Garante que cada clínica tenha apenas uma configuração por chave. |

---

## 18. Tabela: auditoria_log

Armazena o histórico de alterações em dados sensíveis (LGPD e segurança).

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | SERIAL | PRIMARY KEY | Identificador único do log. |
| `clinica_id` | INTEGER | FOREIGN KEY (Clinica) | **Chave de Multitenancy.** |
| `usuario_id` | INTEGER | FOREIGN KEY (Usuario) | Usuário que realizou a ação. |
| `tabela_afetada` | VARCHAR(100) | NOT NULL | Tabela onde a alteração ocorreu. |
| `registro_id` | INTEGER | NOT NULL | ID do registro afetado. |
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
| `id` | SERIAL | PRIMARY KEY | Identificador único do template. |
| `clinica_id` | INTEGER | FOREIGN KEY (clinica) | Chave de Multitenancy. |
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
| `id` | SERIAL | PRIMARY KEY | Identificador único da mensagem em massa. |
| `clinica_id` | INTEGER | FOREIGN KEY (clinica) | Chave de Multitenancy. |
| `usuario_criacao_id` | INTEGER | FOREIGN KEY (usuario) | Usuário que criou a mensagem. |
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
| `id` | SERIAL | PRIMARY KEY | Identificador único do histórico. |
| `clinica_id` | INTEGER | FOREIGN KEY (clinica) | Chave de Multitenancy. |
| `tutor_id` | INTEGER | FOREIGN KEY (tutor) | Tutor que recebeu a mensagem. |
| `tipo_mensagem` | VARCHAR(50) | NOT NULL | Ex: 'VACINA_LEMBRETE', 'CONSULTA_LEMBRETE', 'MENSAGEM_MASSA'. |
| `conteudo` | TEXT | NOT NULL | Conteúdo completo da mensagem enviada. |
| `status_envio` | VARCHAR(50) | NOT NULL | Ex: 'ENVIADO', 'FALHA', 'LIDO'. |
| `referencia_id` | INTEGER | | ID da transação que gerou a mensagem (Ex: `vacina_aplicada.id`). |
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

Armazena o histórico de alterações do prontuário (tabela `consulta`), garantindo rastreabilidade e auditoria detalhada.

| Coluna | Tipo de Dado | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | SERIAL | PRIMARY KEY | Identificador único do registro de histórico. |
| `clinica_id` | INTEGER | FOREIGN KEY (clinica) | Chave de Multitenancy. |
| `consulta_id` | INTEGER | FOREIGN KEY (consulta) | Consulta original que foi alterada. |
| `usuario_alteracao_id` | INTEGER | FOREIGN KEY (usuario) | Usuário que realizou a alteração. |
| `motivo_alteracao` | TEXT | | Breve descrição do motivo da alteração. |
| `dados_anteriores` | JSONB | NOT NULL | Cópia completa dos dados da consulta antes da alteração. |
| `data_add` | TIMESTAMP | DEFAULT NOW() | Data e hora da alteração. |
| `data_alt` | TIMESTAMP | DEFAULT NOW() | Data da última alteração. |

---

## Relacionamentos Chave (Resumo - Atualizado)

*   **Multitenancy:** Todas as tabelas transacionais (tutor, animal, agendamento, consulta, etc.) se relacionam com `clinica(id)` via `clinica_id`.
*   **Tutor-Animal:** `animal` se relaciona com `tutor` via `tutor_principal_id`. O relacionamento N:N é feito pela tabela `tutor_animal`.
*   **Atendimento:** `agendamento` -> `consulta` -> `prescricao` -> `prescricao_item`.
*   **Auditoria:** `consulta_historico` rastreia as alterações na tabela `consulta`.
*   **Comunicação:** `comunicacao_historico` rastreia mensagens enviadas, usando `comunicacao_template` e `comunicacao_massa`.
*   **Estoque:** `prescricao_item` e outras transações se relacionam com `produto`. `estoque_movimento` registra as mudanças no estoque.
*   **Financeiro:** `fatura` se relaciona com `tutor`. `pagamento` se relaciona com `fatura`.

Este modelo de dados, agora com **22 tabelas**, está completo e robusto para o MVP do **PetVital**.
