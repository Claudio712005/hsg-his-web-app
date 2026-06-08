package br.com.hsg.service.impl.clinica;

import br.com.hsg.service.dto.prontuario.AlergiaResumoDTO;
import br.com.hsg.service.dto.prontuario.AnexoResumoDTO;
import br.com.hsg.service.dto.prontuario.AnotacaoResumoDTO;
import br.com.hsg.service.dto.prontuario.ConsultaResumoDTO;
import br.com.hsg.service.dto.prontuario.ProntuarioDTO;
import br.com.hsg.service.dto.prontuario.ReceitaResumoDTO;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ProntuarioPdfBuilder {

    private static final String LOGO_RESOURCE = "branding/hsg-logo-completa.png";
    private static final String NOME_CLINICA  = "Hospital São Gabriel";
    private static final DateTimeFormatter FMT_DT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FMT_D  = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Color AZUL_HSG = new Color(0x1F, 0x3A, 0x68);
    private static final Color CINZA    = new Color(0x55, 0x55, 0x55);

    private ProntuarioPdfBuilder() {}

    public static byte[] build(ProntuarioDTO dto) {
        if (dto == null || dto.getPaciente() == null) {
            throw new IllegalArgumentException("Prontuário inválido.");
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 50, 50, 50, 50);
        try {
            PdfWriter.getInstance(doc, out);
            doc.open();
            cabecalho(doc);
            tituloPaciente(doc, dto);
            alergias(doc, dto);
            consultas(doc, dto);
            rodape(doc);
            doc.close();
        } catch (DocumentException de) {
            throw new IllegalStateException("Falha ao gerar PDF do prontuário.", de);
        }
        return out.toByteArray();
    }

    private static void cabecalho(Document doc) throws DocumentException {
        PdfPTable header = new PdfPTable(new float[]{1, 4});
        header.setWidthPercentage(100);
        header.setSpacingAfter(8);

        try (InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(LOGO_RESOURCE)) {
            if (is != null) {
                Image logo = Image.getInstance(readAll(is));
                logo.scaleToFit(110, 90);
                PdfPCell cellLogo = new PdfPCell(logo, false);
                cellLogo.setBorder(Rectangle.NO_BORDER);
                cellLogo.setHorizontalAlignment(Element.ALIGN_LEFT);
                cellLogo.setVerticalAlignment(Element.ALIGN_MIDDLE);
                header.addCell(cellLogo);
            } else {
                PdfPCell vazio = new PdfPCell(new Phrase(""));
                vazio.setBorder(Rectangle.NO_BORDER);
                header.addCell(vazio);
            }
        } catch (IOException ioe) {
            PdfPCell vazio = new PdfPCell(new Phrase(""));
            vazio.setBorder(Rectangle.NO_BORDER);
            header.addCell(vazio);
        }

        PdfPCell info = new PdfPCell();
        info.setBorder(Rectangle.NO_BORDER);
        info.setVerticalAlignment(Element.ALIGN_MIDDLE);
        info.addElement(par(NOME_CLINICA, 14, Font.BOLD, AZUL_HSG, Element.ALIGN_LEFT));
        info.addElement(par("Prontuário do paciente — emissão eletrônica",
                10, Font.NORMAL, CINZA, Element.ALIGN_LEFT));
        info.addElement(par("Gerado em " + LocalDateTime.now().format(FMT_DT),
                9, Font.NORMAL, CINZA, Element.ALIGN_LEFT));
        header.addCell(info);
        doc.add(header);
    }

    private static void tituloPaciente(Document doc, ProntuarioDTO dto) throws DocumentException {
        Paragraph t = new Paragraph("PRONTUÁRIO MÉDICO",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLACK));
        t.setAlignment(Element.ALIGN_CENTER);
        t.setSpacingAfter(8);
        doc.add(t);

        Paragraph cab = new Paragraph("PACIENTE",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, AZUL_HSG));
        cab.setSpacingAfter(2);
        doc.add(cab);

        StringBuilder sb = new StringBuilder();
        sb.append(dto.getPaciente().getNomeCompleto() == null ? "—" : dto.getPaciente().getNomeCompleto());
        if (dto.getPaciente().getDataNascimento() != null) {
            sb.append("  |  Nasc. ").append(dto.getPaciente().getDataNascimento().format(FMT_D));
        }
        if (dto.getPaciente().getIdade() != null) {
            sb.append("  |  ").append(dto.getPaciente().getIdade()).append(" anos");
        }
        Paragraph p = new Paragraph(sb.toString(),
                FontFactory.getFont(FontFactory.HELVETICA, 11, Color.BLACK));
        p.setSpacingAfter(10);
        doc.add(p);
    }

    private static void alergias(Document doc, ProntuarioDTO dto) throws DocumentException {
        Paragraph cab = new Paragraph("ALERGIAS",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, AZUL_HSG));
        cab.setSpacingAfter(4);
        doc.add(cab);

        if (dto.getAlergias() == null || dto.getAlergias().isEmpty()) {
            doc.add(par("Sem alergias registradas.", 10, Font.ITALIC, CINZA, Element.ALIGN_LEFT));
        } else {
            for (AlergiaResumoDTO a : dto.getAlergias()) {
                StringBuilder s = new StringBuilder("• ").append(a.getNome());
                if (a.getTipo() != null)      s.append("  (").append(a.getTipo()).append(")");
                if (a.getGravidade() != null) s.append("  — gravidade ").append(a.getGravidade());
                if (a.getStatus() != null)    s.append("  [").append(a.getStatus()).append("]");
                doc.add(par(s.toString(), 10, Font.NORMAL, Color.BLACK, Element.ALIGN_LEFT));
                if (a.getReacao() != null && !a.getReacao().isEmpty()) {
                    Paragraph r = par("    Reação: " + a.getReacao(),
                            9, Font.ITALIC, CINZA, Element.ALIGN_LEFT);
                    doc.add(r);
                }
            }
        }
        Paragraph esp = new Paragraph(" ");
        esp.setSpacingAfter(8);
        doc.add(esp);
    }

    private static void consultas(Document doc, ProntuarioDTO dto) throws DocumentException {
        Paragraph cab = new Paragraph("HISTÓRICO DE CONSULTAS",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, AZUL_HSG));
        cab.setSpacingAfter(6);
        doc.add(cab);

        if (dto.getConsultas() == null || dto.getConsultas().isEmpty()) {
            doc.add(par("Sem consultas registradas.", 10, Font.ITALIC, CINZA, Element.ALIGN_LEFT));
            return;
        }
        for (ConsultaResumoDTO c : dto.getConsultas()) {
            Paragraph titulo = new Paragraph();
            titulo.add(new Chunk(c.getDataConsulta() != null ? c.getDataConsulta().format(FMT_DT) : "—",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, AZUL_HSG)));
            titulo.add(new Chunk("  |  " + (c.getStatus() == null ? "—" : c.getStatus()),
                    FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK)));
            if (c.getMedicoNome() != null) {
                titulo.add(new Chunk("  |  Dr(a). " + c.getMedicoNome(),
                        FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK)));
            }
            if (c.getEspecialidade() != null) {
                titulo.add(new Chunk("  |  " + c.getEspecialidade(),
                        FontFactory.getFont(FontFactory.HELVETICA, 10, CINZA)));
            }
            titulo.setSpacingAfter(2);
            doc.add(titulo);

            if (c.getObservacaoClinica() != null && !c.getObservacaoClinica().isEmpty()) {
                doc.add(par("    Obs. clínica: " + c.getObservacaoClinica(),
                        9, Font.NORMAL, Color.BLACK, Element.ALIGN_LEFT));
            }
            if (c.getMotivoCancelamento() != null && !c.getMotivoCancelamento().isEmpty()) {
                doc.add(par("    Motivo cancelamento: " + c.getMotivoCancelamento(),
                        9, Font.NORMAL, Color.BLACK, Element.ALIGN_LEFT));
            }

            if (c.getAnotacoes() != null && !c.getAnotacoes().isEmpty()) {
                doc.add(par("    Anotações:", 9, Font.BOLD, CINZA, Element.ALIGN_LEFT));
                for (AnotacaoResumoDTO an : c.getAnotacoes()) {
                    doc.add(par("      • " + an.getTitulo() + ": " + an.getDescricao(),
                            9, Font.NORMAL, Color.BLACK, Element.ALIGN_LEFT));
                }
            }
            if (c.getAnexos() != null && !c.getAnexos().isEmpty()) {
                doc.add(par("    Anexos:", 9, Font.BOLD, CINZA, Element.ALIGN_LEFT));
                for (AnexoResumoDTO ax : c.getAnexos()) {
                    doc.add(par("      • " + ax.getNomeOriginal()
                                    + " (" + (ax.getDominio() == null ? "" : ax.getDominio()) + ")",
                            9, Font.NORMAL, Color.BLACK, Element.ALIGN_LEFT));
                }
            }
            if (c.getReceitaAtiva() != null) {
                doc.add(par("    Receituário ativo (Receita nº " + c.getReceitaAtiva().getId() + "):",
                        9, Font.BOLD, CINZA, Element.ALIGN_LEFT));
                for (ReceitaResumoDTO.ItemDTO i : c.getReceitaAtiva().getItens()) {
                    doc.add(par("      • " + i.getMedicamento() + " — " + i.getPosologia(),
                            9, Font.NORMAL, Color.BLACK, Element.ALIGN_LEFT));
                }
            }
            if (c.getReceitasInativas() != null && !c.getReceitasInativas().isEmpty()) {
                doc.add(par("    Receitas anteriores: " + c.getReceitasInativas().size(),
                        9, Font.ITALIC, CINZA, Element.ALIGN_LEFT));
            }
            Paragraph esp = new Paragraph(" ");
            esp.setSpacingAfter(6);
            doc.add(esp);
        }
    }

    private static void rodape(Document doc) throws DocumentException {
        Paragraph rod = new Paragraph(
                "Documento gerado eletronicamente pelo Hospital São Gabriel — HSG HIS. "
                        + "Uso clínico restrito.",
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, CINZA));
        rod.setAlignment(Element.ALIGN_CENTER);
        rod.setSpacingBefore(20);
        doc.add(rod);
    }

    private static Paragraph par(String texto, int tamanho, int style, Color cor, int align) {
        Paragraph p = new Paragraph(texto,
                FontFactory.getFont(FontFactory.HELVETICA, tamanho, style, cor));
        p.setAlignment(align);
        return p;
    }

    private static byte[] readAll(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int n;
        while ((n = is.read(buf)) > 0) baos.write(buf, 0, n);
        return baos.toByteArray();
    }
}
