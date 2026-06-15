# Documento de Requisitos do Sistema PetVital (MVP)

## 1. Introdução

Este documento formaliza os requisitos de negócio, funcionais e não funcionais para o desenvolvimento do **PetVital**, um sistema de gestão veterinária moderno, baseado em arquitetura **Multi-Inquilino (SaaS)**, focado em clínicas e veterinários autônomos.

O objetivo é fornecer uma ferramenta robusta que centralize o gerenciamento de pacientes, prontuários, agendamentos, estoque e comunicação, garantindo a rastreabilidade e a conformidade com as melhores práticas clínicas e de segurança de dados.

---

## 2. Definição de Regras de Negócio (DRN)

As regras de negócio (RN) definem as políticas e restrições que governam o sistema, garantindo a integridade dos dados e a conformidade com os processos clínicos.

### 2.1. Regras de Multitenancy e Acesso

| ID | Regra de Negócio | Detalhes |
| :--- | :--- | :--- |
| **RN001** | **Isolamento de Dados (Multitenancy)** | Nenhum usuário de uma clínica (Tenant) pode acessar, visualizar ou modificar dados pertencentes a outra clínica. O filtro por `clinica_id` deve ser aplicado em todas as consultas transacionais. |
| **RN002** | **Tipos de Inquilino** | A entidade principal (`clinica`) deve suportar dois tipos de inquilino: **PJ** (Pessoa Jurídica - Clínica) e **PF** (Pessoa Física - Veterinário Autônomo). |
| **RN003** | **Identificação Fiscal** | Para inquilinos PJ, o campo `documento_fiscal` deve ser um CNPJ válido. Para inquilinos PF, deve ser um CPF válido. Ambos devem ser únicos no sistema. |
| **RN004** | **Controle de Acesso** | O acesso ao sistema deve ser controlado por perfis (`ADMIN`, `VETERINARIO`, `RECEPCAO`, `AUXILIAR`, `FINANCEIRO`), e as permissões devem ser aplicadas estritamente (Ex: Apenas `VETERINARIO` pode emitir receitas). |

### 2.2. Regras de Prontuário e Histórico Clínico

| ID | Regra de Negócio | Detalhes |
| :--- | :--- | :--- |
| **RN005** | **Vínculo Tutor-Animal** | Todo `animal` deve estar vinculado a pelo menos um `tutor` principal. Um animal pode ter múltiplos tutores secundários (relacionamento N:N). |
| **RN006** | **Imutabilidade do Prontuário** | Após a criação, o registro de uma `consulta` (prontuário) não pode ser excluído. Qualquer alteração deve gerar um novo registro na tabela `consulta_historico`, mantendo a versão anterior intacta para fins de auditoria. |
| **RN007** | **Registro de Peso** | O registro de peso (`peso_historico`) deve ser sempre associado a um `animal` e incluir a Escala de Condição Corporal (ECC). |
| **RN008** | **Prescrição por Veterinário** | A emissão de uma `prescricao` deve ser obrigatoriamente vinculada a um `usuario` com o perfil `VETERINARIO`. |

### 2.3. Regras de Comunicação e Agendamento

| ID | Regra de Negócio | Detalhes |
| :--- | :--- | :--- |
| **RN009** | **Lembrete de Vacina** | O sistema deve calcular a data de envio do lembrete de reforço subtraindo os dias de antecedência configurados (na tabela `configuracao`) da `data_proximo_reforco` registrada na `vacina_aplicada`. |
| **RN010** | **Consentimento de Comunicação** | Mensagens de saúde e segurança (Ex: Lembretes de vacina, retorno) são obrigatórias e serão sempre enviadas. Mensagens informativas/promocionais (Massa) só podem ser enviadas se a flag `aceita_comunicacao_informativa` do `tutor` estiver marcada como `TRUE`. |
| **RN011** | **Rastreabilidade de Mensagens** | Todo envio de mensagem (automática ou em massa) deve ser registrado na tabela `comunicacao_historico`, incluindo o conteúdo e o status de envio. |
| **RN012** | **Registro de Vacina** | O registro de uma `vacina_aplicada` é um ato clínico e não exige vínculo obrigatório com o estoque, permitindo o uso por veterinários autônomos. |

---

## 3. Requisitos Funcionais (RF)

Os requisitos funcionais descrevem as funções que o sistema deve executar para atender às necessidades do usuário.

| ID | Módulo | Requisito Funcional |
| :--- | :--- | :--- |
| **RF001** | **Cadastros** | O sistema deve permitir o cadastro completo de `tutor` e `animal`, incluindo dados pessoais, endereço, contato e informações clínicas (alergias, doenças crônicas). |
| **RF002** | **Agenda** | O sistema deve exibir uma agenda com visualização diária/semanal/mensal, permitindo o agendamento de diferentes tipos de atendimento (consulta, vacina, retorno). |
| **RF003** | **Prontuário** | O sistema deve permitir a criação de um registro de `consulta` (prontuário) a partir de um `agendamento`, contendo campos para Anamnese, Exame Físico, Diagnóstico e Conduta. |
| **RF004** | **Prontuário** | O sistema deve permitir o upload e a vinculação de arquivos (`anexo`) como laudos, imagens e vídeos ao registro de `consulta`. |
| **RF005** | **Clínico** | O sistema deve permitir o registro de `peso_historico` e ECC, e exibir um gráfico de evolução de peso para o `animal`. |
| **RF006** | **Clínico** | O sistema deve permitir o registro de `vacina_aplicada`, incluindo lote, fabricante e a data do próximo reforço. |
| **RF007** | **Prescrição** | O sistema deve permitir a emissão de `prescricao` (receituário) com itens (`prescricao_item`) e a impressão/exportação em PDF. |
| **RF008** | **Estoque** | O sistema deve permitir o cadastro de `produto` (medicamentos, materiais) e o registro de `estoque_movimento` (entrada/saída/ajuste). |
| **RF009** | **Financeiro** | O sistema deve permitir a emissão de `fatura` e o registro de `pagamento` com diferentes formas de pagamento. |
| **RF010** | **Comunicação** | O sistema deve enviar lembretes automáticos de vacina e retorno via WhatsApp, com base nas regras configuradas por clínica. |
| **RF011** | **Comunicação** | O sistema deve permitir o envio de mensagens em massa (`comunicacao_massa`) para os tutores que aceitaram a comunicação informativa. |
| **RF012** | **Auditoria** | O sistema deve manter um registro de auditoria (`consulta_historico`) para todas as alterações feitas no prontuário. |

---

## 4. Requisitos Não Funcionais (RNF)

Os requisitos não funcionais descrevem critérios de qualidade e restrições técnicas do sistema.

| ID | Categoria | Requisito Não Funcional |
| :--- | :--- | :--- |
| **RNF001** | **Performance** | O tempo de resposta para a abertura da agenda e do prontuário não deve exceder 2 segundos. |
| **RNF002** | **Disponibilidade** | O sistema deve ter uma disponibilidade de 99.9% (24/7), hospedado em ambiente de nuvem (AWS/GCP/Azure). |
| **RNF003** | **Segurança** | O sistema deve implementar controle de acesso baseado em papéis (RBAC) e criptografia de dados sensíveis (LGPD). |
| **RNF004** | **Tecnologia** | O Backend deve ser desenvolvido em **Java/Spring Boot** e o banco de dados deve ser **PostgreSQL**. |
| **RNF005** | **Usabilidade** | A interface do usuário deve ser intuitiva, responsiva e otimizada para uso em tablets (para o veterinário em campo). |
| **RNF006** | **Padronização** | O código e o banco de dados devem seguir o padrão de nomenclatura `snake_case` e incluir campos de auditoria de data (`data_add`, `data_alt`). |
| **RNF007** | **Design** | O design da interface deve utilizar a paleta de cores definida: Azul Principal (`#2A80FF`), Verde Água (`#17C3B2`), etc. |

---

## 5. Modelagem de Dados (Diagrama ER)

A modelagem de dados a seguir representa o Diagrama de Entidade-Relacionamento (DER) do MVP, utilizando a notação Mermaid.

**Nota:** O diagrama representa as 22 tabelas do modelo final, com a implementação de Multitenancy e as novas funcionalidades de Comunicação e Auditoria.

```mermaid
erDiagram
    clinica ||--o{ usuario : "pertence"
    clinica ||--o{ tutor : "pertence"
    clinica ||--o{ animal : "pertence"
    clinica ||--o{ agendamento : "pertence"
    clinica ||--o{ consulta : "pertence"
    clinica ||--o{ produto : "pertence"
    clinica ||--o{ fatura : "pertence"
    clinica ||--o{ configuracao : "pertence"
    clinica ||--o{ comunicacao_template : "pertence"
    clinica ||--o{ comunicacao_massa : "pertence"
    clinica ||--o{ comunicacao_historico : "pertence"
    clinica ||--o{ auditoria_log : "pertence"

    tutor ||--o{ animal : "tutor_principal"
    tutor }|--|{ tutor_animal : "vincula"
    animal }|--|{ tutor_animal : "vincula"

    animal ||--o{ agendamento : "agendado_para"
    animal ||--o{ peso_historico : "tem_historico"
    animal ||--o{ vacina_aplicada : "recebeu"

    agendamento ||--o{ consulta : "gera"
    usuario ||--o{ agendamento : "veterinario_responsavel"
    usuario ||--o{ consulta : "veterinario_atendimento"
    usuario ||--o{ peso_historico : "registrou"
    usuario ||--o{ vacina_aplicada : "aplicou"
    usuario ||--o{ prescricao : "prescreveu"
    usuario ||--o{ pagamento : "registrou"
    usuario ||--o{ comunicacao_massa : "criou"
    usuario ||--o{ auditoria_log : "realizou_acao"
    usuario ||--o{ consulta_historico : "realizou_alteracao"

    consulta ||--o{ anexo : "tem"
    consulta ||--o{ prescricao : "gera"
    consulta ||--o{ consulta_historico : "historico_de"

    prescricao ||--o{ prescricao_item : "contem"
    produto ||--o{ prescricao_item : "prescrito"
    produto ||--o{ estoque_movimento : "movimentado"

    fatura ||--o{ pagamento : "recebe"
    tutor ||--o{ fatura : "responsavel"

    tutor ||--o{ comunicacao_historico : "recebeu_mensagem"

    %% Definição das Entidades (Apenas as chaves primárias e de relacionamento)

    clinica {
        int id PK
        varchar tipo_tenant
        varchar documento_fiscal
    }

    usuario {
        int id PK
        int clinica_id FK
        varchar perfil
    }

    tutor {
        int id PK
        int clinica_id FK
        varchar cpf
    }

    animal {
        int id PK
        int clinica_id FK
        int tutor_principal_id FK
    }

    tutor_animal {
        int tutor_id PK, FK
        int animal_id PK, FK
    }

    agendamento {
        int id PK
        int clinica_id FK
        int animal_id FK
        int veterinario_id FK
    }

    consulta {
        int id PK
        int clinica_id FK
        int agendamento_id FK
        int veterinario_id FK
    }

    consulta_historico {
        int id PK
        int clinica_id FK
        int consulta_id FK
        int usuario_alteracao_id FK
    }

    prescricao {
        int id PK
        int clinica_id FK
        int consulta_id FK
        int veterinario_id FK
    }

    prescricao_item {
        int id PK
        int prescricao_id FK
        int produto_id FK
    }

    produto {
        int id PK
        int clinica_id FK
    }

    estoque_movimento {
        int id PK
        int clinica_id FK
        int produto_id FK
    }

    fatura {
        int id PK
        int clinica_id FK
        int tutor_id FK
    }

    pagamento {
        int id PK
        int fatura_id FK
    }

    vacina_aplicada {
        int id PK
        int clinica_id FK
        int animal_id FK
        int profissional_id FK
    }

    peso_historico {
        int id PK
        int clinica_id FK
        int animal_id FK
        int usuario_registro_id FK
    }

    anexo {
        int id PK
        int clinica_id FK
        int consulta_id FK
    }

    configuracao {
        int id PK
        int clinica_id FK
    }

    comunicacao_template {
        int id PK
        int clinica_id FK
    }

    comunicacao_massa {
        int id PK
        int clinica_id FK
        int usuario_criacao_id FK
    }

    comunicacao_historico {
        int id PK
        int clinica_id FK
        int tutor_id FK
    }

    auditoria_log {
        int id PK
        int clinica_id FK
        int usuario_id FK
    }
```

---

## 6. Referências

[1]: # "Documento de Requisitos Funcionais e Não Funcionais - PetVital"
[2]: # "Modelagem de Dados PostgreSQL - PetVital (data_model.md)"
[3]: # "Paleta de Cores PetVital"
