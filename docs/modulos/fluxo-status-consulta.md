# Fluxo de Status da Consulta e Trilha Documental

Define o ciclo de vida do status da consulta e a estrutura de trilha documental: histórico de transições + anotações livres.

## 1. Status e transições

Estados em `StatusConsulta`: **AGENDADA**, **CONFIRMADA**, **REALIZADA**, **FALTOU**, **CANCELADA**.

Fluxo:

```
                +-------------+
                |  AGENDADA   |  ← paciente agendou (Fase D)
                +------+------+
                       |
                       | check-in (enfermeiro/admin) [AC-08]
                       v
                +-------------+
                | CONFIRMADA  |
                +------+------+
                       |
                       | atendimento (médico responsável) [AC-02 + AC-09]
                       v
                +-------------+
                |  REALIZADA  |  (final, com observação clínica)
                +-------------+

  Em AGENDADA ou CONFIRMADA também podem ocorrer:
   • FALTOU    (paciente, médico responsável, enfermeiro, admin ou SISTEMA via auto-falta)
   • CANCELADA (paciente até 24h, ou clínica/médico responsável sem trava de 24h)
```

Regras de transição estão consolidadas em `docs/regras/regras-negocio-agendamento.md` (seções 5–7 e 7.1, IDs AG-*, CA-*, AC-*, AF-*).

## 2. Histórico (tb_consulta_historico)

Registro **automático** de cada transição de status. Veja `docs/modulos/historico-consulta.md` para o modelo completo.

Resumo: cada mudança grava `acao`, `tipoResponsavel` (PACIENTE/MEDICO/ENFERMEIRO/ADMIN/SISTEMA), `idResponsavel`, `dataAcao` e observação opcional. Permite reconstrução da linha do tempo da consulta.

## 3. Anotações (tb_consulta_anotacao)

**N anotações por consulta**, adicionadas livremente por médico, enfermeiro ou administrador. Cada anotação tem:

- Título (1–200 chars, obrigatório)
- Descrição (1–2000 chars, obrigatória)
- Responsável (id + tipo)
- Data/hora de criação

Migration: **V31** cria `tb_consulta_anotacao` com FK para `tb_consulta`, CHECK do tipo, índice `(id_consulta, dt_criacao DESC)`.

Entidade JPA: `ConsultaAnotacao` (`hsg-his-domain`) com factory `registrar(consulta, titulo, descricao, idResp, tipoResp)` aplicando todas as validações.

DAO: `ConsultaAnotacaoDAO.salvar` + `listarPorConsulta(id)`.

Service: `ConsultaClinicaServiceFacade.adicionarAnotacao(...)` e `listarAnotacoes(idConsulta)`.

### 3.1 Regras de acesso

| ID | Regra | Onde |
|----|-------|------|
| AN-01 | Tipo do responsável obrigatório (`MEDICO`, `ENFERMEIRO`, `ADMIN`). Paciente ou SISTEMA não anotam. | `ConsultaClinicaServiceImpl.adicionarAnotacao` |
| AN-02 | Consulta `CANCELADA` não aceita novas anotações. | Idem |
| AN-03 | Médico só anota nas próprias consultas (`c.getMedico().getId() == idResponsavel`). | Idem + `validarMedicoSoNasProprias` |
| AN-04 | Enfermeiro e administrador podem anotar em qualquer consulta visível. | Idem |
| AN-05 | Título e descrição obrigatórios; limites 200 / 2000 caracteres validados no domínio. | `ConsultaAnotacao.registrar` |
| AN-06 | Data/hora preenchida automaticamente em `LocalDateTime.now()`. | Idem |

## 4. UI

Botão **Anotações** disponível por consulta nas telas:

- `clinica/recepcao-dia.xhtml` (enfermeiro/admin)
- `clinica/minha-agenda.xhtml` (médico)

O diálogo apresenta três blocos:

1. **Cabeçalho** com dados da consulta (paciente, médico, data, status).
2. **Nova anotação** — form com título e descrição.
3. **Anotações** — lista cronológica reversa, cada cartão com tipo do responsável, data/hora e conteúdo.
4. **Histórico de status** — linha do tempo de transições registradas em `tb_consulta_historico`.

Médicos no diálogo veem todas as anotações registradas por enfermeiros/admins na sua consulta; enfermeiros/admins veem as registradas pelo médico responsável. Toda anotação fica visível para os três perfis.

## 5. Testes

- Domínio: `ConsultaAnotacaoTest` cobre validações de campos obrigatórios, limites de tamanho, factory.
- Serviço: `ConsultaClinicaServiceImplTest` adiciona testes para:
  - Médico anota na própria consulta.
  - Médico não anota em consulta de outro médico.
  - Anotação rejeitada em consulta cancelada.
  - Paciente rejeitado.
  - Enfermeiro autorizado.
  - `listarAnotacoes` delega ao DAO.

## 6. Pontos abertos

- Edição/remoção de anotação não disponível (write-only). Futuramente adicionar `tb_consulta_anotacao_historico` se editar virar requisito.
- Anexos (imagens, PDFs) em anotações vão exigir o módulo de storage (ver discussão sobre MinIO).
- Visualização cronológica unificada (histórico + anotações em ordem única) é uma evolução de UI.
