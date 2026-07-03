# PetVital API

**PetVital** é um sistema de gestão veterinária moderno, baseado em arquitetura **Multi-Inquilino (SaaS)**, focado em clínicas e veterinários autônomos. O objetivo é centralizar o gerenciamento de pacientes, prontuários, agendamentos, estoque, financeiro e comunicação com tutores, garantindo rastreabilidade e conformidade com boas práticas clínicas e de segurança de dados (LGPD).

> Toda a informação detalhada de negócio, requisitos e modelagem que estava anteriormente neste README foi preservada integralmente em [`docs/Documento de Requisitos do Sistema PetVital (MVP).md`](docs/Documento%20de%20Requisitos%20do%20Sistema%20PetVital%20%28MVP%29.md). Nada foi removido — apenas reorganizado para que este README funcione como porta de entrada técnica do repositório.

## Status do projeto

Este repositório está atualmente na **fase de documentação**: regras de negócio, requisitos e modelagem de dados já foram definidos, mas ainda não há código-fonte, scaffold de projeto ou infraestrutura implementados. O próximo passo é iniciar o desenvolvimento do backend a partir dos requisitos documentados.

## Documentação

| Documento | Conteúdo |
| :--- | :--- |
| [Documento de Requisitos do Sistema PetVital (MVP)](docs/Documento%20de%20Requisitos%20do%20Sistema%20PetVital%20%28MVP%29.md) | Regras de Negócio (RN001–RN070), Requisitos Funcionais (RF001–RF022), Requisitos Não Funcionais (RNF001–RNF013) e diagrama ER (Mermaid). |
| [Modelagem de Dados (data_model.md)](docs/data_model.md) | Especificação detalhada das 30 tabelas PostgreSQL do modelo (colunas, tipos, chaves e descrições). |
| [Diagrama ER (PNG)](docs/diagrama_er_petvital.png) | Diagrama de Entidade-Relacionamento em imagem. |

## Visão geral dos módulos

O escopo documentado cobre dois grandes blocos (ver detalhes completos no documento de requisitos):

- **Núcleo clínico:** cadastro de tutores e animais, agenda, prontuário (consulta) com histórico imutável, peso/ECC, vacinas, prescrição, estoque básico e financeiro básico (fatura/pagamento).
- **Camada SaaS comercial:** planos e assinaturas com trial e bloqueio por inadimplência, administração global (Super Admin), recuperação de senha, monitoramento/logs de erro, backup e recuperação, dashboard gerencial, controle de caixa, cadastro de serviços, emissão de documentos em PDF e gestão de mídias de pacientes.

## Arquitetura e stack (conforme requisitos documentados)

- **Backend:** Java / Spring Boot (RNF004).
- **Banco de dados:** PostgreSQL, com Multitenancy implementado em dupla camada — filtro lógico via Hibernate/Spring Data (`clinica_id`) e Row Level Security nativo do banco (RN001, RN013).
- **Segurança:** RBAC via Spring Security, senhas com BCrypt, TLS 1.2+, criptografia AES-256-GCM para segredos de terceiros (ex: token do WhatsApp), rate limiting (RNF003).
- **Hospedagem:** ambiente de nuvem (AWS/GCP/Azure) com meta de disponibilidade de 99.9% (RNF002).

## Estrutura do repositório

```
.
├── README.md
└── docs/
    ├── Documento de Requisitos do Sistema PetVital (MVP).md
    ├── data_model.md
    └── diagrama_er_petvital.png
```

## Próximos passos

- Definir o corte de escopo real do MVP (núcleo clínico) versus a camada SaaS comercial, que pode ser endereçada em fases posteriores.
- Criar o scaffold do projeto Spring Boot (estrutura de pacotes, gerenciador de dependências, Flyway/Liquibase para migrations, Docker Compose com PostgreSQL).
- Definir o contrato de API (OpenAPI) e o fluxo de autenticação antes de iniciar a implementação dos módulos.
