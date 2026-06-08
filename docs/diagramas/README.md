# Diagramas

Diagramas em [Mermaid](https://mermaid.js.org/) (`.mmd`). Versionados como código, renderizados sob demanda.

## Arquivos

| Arquivo | O que mostra |
|---------|--------------|
| [`arquitetura.mmd`](arquitetura.mmd) | Visão geral de containers/componentes: usuários → browser → Keycloak → EAR (web/service/domain) → Postgres + MinIO/S3 + SMTP |
| [`fluxo-upload-anexo.mmd`](fluxo-upload-anexo.mmd) | Sequência completa de uma operação (upload de anexo clínico), incluindo validações AX-*, resolução de bucket e persistência |

## Como visualizar

### Online (rápido)

1. Abre https://mermaid.live
2. Cola o conteúdo do `.mmd`
3. Exporta SVG ou PNG

### CLI (mermaid-cli)

```bash
npm install -g @mermaid-js/mermaid-cli
mmdc -i docs/diagramas/arquitetura.mmd -o docs/diagramas/arquitetura.svg
mmdc -i docs/diagramas/arquitetura.mmd -o docs/diagramas/arquitetura.png -w 1600
```

### IDE / GitHub

- VS Code: extensão **Markdown Preview Mermaid Support**
- IntelliJ: plugin **Mermaid**
- GitHub renderiza `.mmd` em fences ```mermaid``` automaticamente no Markdown

## Convenções

- **Cores** (no `arquitetura.mmd`):
  - Azul claro (`#e7eef9`) — usuários
  - Amarelo claro (`#fff8e1`) — edge / autenticação
  - Azul forte (`#f6f9fd` borda `#1f3a68`) — EAR / componentes próprios
  - Verde claro (`#e3f4e3`) — infraestrutura externa
- **Nomes** usam termos do código (classes, módulos Maven, nomes de schemas)
- **Setas** representam direção de invocação ou fluxo de dados
- **Tracejado** indica dependência fraca (uso de tipo / leitura)
