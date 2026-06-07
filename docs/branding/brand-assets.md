# Brand Assets — HSG (Hospital São Gabriel)

Catálogo dos arquivos de marca disponíveis no projeto, com o uso recomendado de cada versão.

---

## 1. Inventário

| Arquivo | Tamanho | Composição | Uso recomendado |
|---------|---------|-----------|-----------------|
| [`hsg-logo-completa.png`](../../hsg-his-web/src/main/webapp/resources/images/hsg-logo-completa.png) | ~67 KB | Símbolo (escudo+cruz+mão) **+** texto "Hospital São Gabriel" — lockup horizontal | Cabeçalho do receituário PDF, página de login, header principal de telas com bastante espaço, e-mails transacionais, README do repositório |
| [`hsg-logo.png`](../../hsg-his-web/src/main/webapp/resources/images/hsg-logo.png) | ~356 KB | Apenas símbolo (escudo+cruz+mão) | Favicon (com resize), badges compactos, ícone de sidebar/topbar com pouco espaço horizontal, splash, social cards (avatar) |
| [`hsg-logo-nome.png`](../../hsg-his-web/src/main/webapp/resources/images/hsg-logo-nome.png) | ~342 KB | Apenas texto "Hospital São Gabriel" | Quando o símbolo já está visível na mesma viewport (ex.: footer abaixo de um header que já tem a logo completa) ou cabeçalho secundário de PDF |

Cores principais derivadas do logo:
- Azul HSG: `#1F3A68` — usado em textos de cabeçalho e títulos de seção
- Verde acento: `#2DA64C` (aprox.) — usado em destaques positivos pontuais
- Cinza neutro: `#555555` — texto secundário em PDFs

---

## 2. Convenções de uso

1. **Sempre dar respiro** — mínimo de 10px de padding em volta da logo
2. **Não distorcer** — manter aspect ratio; usar `object-fit: contain` em containers HTML
3. **Fundo claro preferencial** — versões em PNG têm fundo branco; para fundos escuros, criar versão invertida quando necessário (TODO futuro)
4. **Tamanhos mínimos**:
   - `hsg-logo-completa.png`: ≥ 200px de largura
   - `hsg-logo.png`: ≥ 64px (favicon ≥ 16px usando ícone redimensionado por separado)
5. **Resolução** — todos arquivos atuais são raster PNG. Versão SVG vetorial é uma evolução futura desejável.

---

## 3. Locais onde a logo é consumida no código

| Local | Versão usada | Como é carregada |
|-------|--------------|------------------|
| Receituário PDF | `hsg-logo-completa.png` | Cópia em `hsg-his-service/src/main/resources/branding/hsg-logo-completa.png`, carregada via `Thread.currentThread().getContextClassLoader().getResourceAsStream("branding/hsg-logo-completa.png")` em [`ReceitaPdfBuilder`](../../hsg-his-service/src/main/java/br/com/hsg/service/impl/clinica/ReceitaPdfBuilder.java) |
| Header do template web | (a definir) | Pretendido em `hsg-his-web/src/main/webapp/resources/images/` |
| Página de login | (a definir) | Idem |

**Cópia duplicada no service module**: a logo está duplicada em `hsg-his-service/src/main/resources/branding/` para que o módulo de serviço (não web) consiga embarcá-la no PDF sem dependência cross-module. Manter as duas cópias sincronizadas ao atualizar a marca.

---

## 4. Política de atualização

Ao atualizar a logo:

1. Substituir os arquivos em `hsg-his-web/src/main/webapp/resources/images/`
2. Atualizar a cópia em `hsg-his-service/src/main/resources/branding/hsg-logo-completa.png`
3. Limpar cache do browser dos desenvolvedores (versionar nome quando mudar significativo: `hsg-logo-completa-v2.png` e atualizar referências)
4. Rebuild + redeploy
5. Smoke test do PDF do receituário pra confirmar que a logo apareceu

---

## 5. Roadmap de evolução

| Item | Prioridade |
|------|-----------|
| Versão SVG vetorial (escalável sem perda) | Alta |
| Versão invertida (fundo escuro) | Média |
| Favicon próprio (16x16, 32x32, 48x48, .ico) | Média |
| Open Graph image (1200x630) para link previews | Baixa |
| Manual de identidade visual (PDF/Figma) | Baixa |
