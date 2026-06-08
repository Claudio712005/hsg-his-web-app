package br.com.hsg.service.impl.clinica;

import br.com.hsg.domain.entity.Consulta;
import br.com.hsg.domain.entity.Medico;
import br.com.hsg.domain.entity.Paciente;
import br.com.hsg.domain.entity.Receita;
import br.com.hsg.domain.entity.ReceitaItem;
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
import java.time.format.DateTimeFormatter;

public final class ReceitaPdfBuilder {

    private static final String LOGO_RESOURCE = "branding/hsg-logo-completa.png";
    private static final String NOME_CLINICA  = "Hospital São Gabriel";
    private static final String ENDERECO      = "Av. Saúde, 1000 — São Paulo/SP — CEP 00000-000";
    private static final String CONTATO       = "(11) 0000-0000 | contato@hsg.com.br";

    private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private static final Color AZUL_HSG  = new Color(0x1F, 0x3A, 0x68);
    private static final Color CINZA     = new Color(0x55, 0x55, 0x55);

    private ReceitaPdfBuilder() {}

    public static byte[] build(Receita receita) {
        if (receita == null) {
            throw new IllegalArgumentException("Receita é obrigatória.");
        }
        Consulta consulta = receita.getConsulta();
        Medico medico     = receita.getMedico();
        Paciente paciente = consulta == null ? null : consulta.getPaciente();
        if (consulta == null || medico == null || paciente == null) {
            throw new IllegalStateException("Receita sem dados completos para PDF.");
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 50, 50, 50, 50);
        try {
            PdfWriter.getInstance(doc, out);
            doc.open();

            adicionarCabecalho(doc);
            adicionarTituloReceita(doc, receita);
            adicionarBlocoPaciente(doc, paciente);
            adicionarBlocoMedico(doc, medico);
            adicionarMedicamentos(doc, receita);
            adicionarAssinatura(doc, medico);
            adicionarRodape(doc);

            doc.close();
        } catch (DocumentException de) {
            throw new IllegalStateException("Falha ao gerar PDF de receita.", de);
        }
        return out.toByteArray();
    }

    private static void adicionarCabecalho(Document doc) throws DocumentException {
        PdfPTable header = new PdfPTable(new float[]{1, 4});
        header.setWidthPercentage(100);
        header.setSpacingAfter(8);

        try (InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(LOGO_RESOURCE)) {
            if (is != null) {
                byte[] logoBytes = readAll(is);
                Image logo = Image.getInstance(logoBytes);
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
        info.addElement(paragrafo(NOME_CLINICA, 14, Font.BOLD, AZUL_HSG, Element.ALIGN_LEFT));
        info.addElement(paragrafo(ENDERECO, 9, Font.NORMAL, CINZA, Element.ALIGN_LEFT));
        info.addElement(paragrafo(CONTATO, 9, Font.NORMAL, CINZA, Element.ALIGN_LEFT));
        header.addCell(info);

        doc.add(header);

        Paragraph linha = new Paragraph(" ");
        linha.setSpacingAfter(4);
        doc.add(linha);
    }

    private static void adicionarTituloReceita(Document doc, Receita r) throws DocumentException {
        Paragraph titulo = new Paragraph("RECEITUÁRIO MÉDICO",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLACK));
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(2);
        doc.add(titulo);

        String emissao = "Emitido em " + r.getDataEmissao().format(FMT_DATA)
                + " às " + r.getDataEmissao().format(FMT_HORA)
                + "  |  Receita nº " + r.getId();
        Paragraph sub = new Paragraph(emissao,
                FontFactory.getFont(FontFactory.HELVETICA, 9, CINZA));
        sub.setAlignment(Element.ALIGN_CENTER);
        sub.setSpacingAfter(12);
        doc.add(sub);
    }

    private static void adicionarBlocoPaciente(Document doc, Paciente p) throws DocumentException {
        Paragraph cab = new Paragraph("PACIENTE",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, AZUL_HSG));
        cab.setSpacingAfter(2);
        doc.add(cab);

        String nome = (p.getNomeCompleto() == null ? "—" : p.getNomeCompleto());
        Paragraph dados = new Paragraph(nome,
                FontFactory.getFont(FontFactory.HELVETICA, 11, Color.BLACK));
        dados.setSpacingAfter(10);
        doc.add(dados);
    }

    private static void adicionarBlocoMedico(Document doc, Medico m) throws DocumentException {
        Paragraph cab = new Paragraph("MÉDICO RESPONSÁVEL",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, AZUL_HSG));
        cab.setSpacingAfter(2);
        doc.add(cab);

        StringBuilder sb = new StringBuilder();
        sb.append("Dr(a). ").append(m.getNomeCompleto() == null ? "—" : m.getNomeCompleto());
        if (m.getCrm() != null) {
            sb.append("  |  ").append(m.getCrm().getFormatado());
        }
        Paragraph dados = new Paragraph(sb.toString(),
                FontFactory.getFont(FontFactory.HELVETICA, 11, Color.BLACK));
        dados.setSpacingAfter(14);
        doc.add(dados);
    }

    private static void adicionarMedicamentos(Document doc, Receita r) throws DocumentException {
        Paragraph titulo = new Paragraph("PRESCRIÇÃO",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, AZUL_HSG));
        titulo.setSpacingAfter(6);
        doc.add(titulo);

        int n = 1;
        for (ReceitaItem item : r.getItens()) {
            Paragraph linha = new Paragraph();
            linha.setSpacingAfter(2);
            linha.add(new com.lowagie.text.Chunk(n + ". " + item.getMedicamento(),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.BLACK)));
            doc.add(linha);

            Paragraph pos = new Paragraph("Posologia: " + item.getPosologia(),
                    FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK));
            pos.setIndentationLeft(14);
            pos.setSpacingAfter(2);
            doc.add(pos);

            if (item.getObservacao() != null && !item.getObservacao().isEmpty()) {
                Paragraph obs = new Paragraph("Observação: " + item.getObservacao(),
                        FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, CINZA));
                obs.setIndentationLeft(14);
                obs.setSpacingAfter(2);
                doc.add(obs);
            }
            if (item.getCid10() != null && !item.getCid10().isEmpty()) {
                Paragraph cid = new Paragraph("CID-10: " + item.getCid10(),
                        FontFactory.getFont(FontFactory.HELVETICA, 9, CINZA));
                cid.setIndentationLeft(14);
                cid.setSpacingAfter(6);
                doc.add(cid);
            } else {
                Paragraph esp = new Paragraph(" ");
                esp.setSpacingAfter(4);
                doc.add(esp);
            }
            n++;
        }
    }

    private static void adicionarAssinatura(Document doc, Medico m) throws DocumentException {
        Paragraph esp = new Paragraph(" ");
        esp.setSpacingBefore(22);
        doc.add(esp);

        PdfPTable assin = new PdfPTable(1);
        assin.setWidthPercentage(60);
        assin.setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell linha = new PdfPCell(new Phrase(" "));
        linha.setBorder(Rectangle.TOP);
        linha.setBorderColor(Color.BLACK);
        linha.setPaddingTop(2);
        linha.setHorizontalAlignment(Element.ALIGN_CENTER);
        assin.addCell(linha);

        String texto = "Dr(a). " + (m.getNomeCompleto() == null ? "—" : m.getNomeCompleto());
        if (m.getCrm() != null) {
            texto += "  |  " + m.getCrm().getFormatado();
        }
        PdfPCell nome = new PdfPCell(new Phrase(texto,
                FontFactory.getFont(FontFactory.HELVETICA, 9, CINZA)));
        nome.setBorder(Rectangle.NO_BORDER);
        nome.setHorizontalAlignment(Element.ALIGN_CENTER);
        assin.addCell(nome);

        doc.add(assin);
    }

    private static void adicionarRodape(Document doc) throws DocumentException {
        Paragraph rodape = new Paragraph(
                "Este documento é válido apenas com a assinatura do médico responsável. "
                        + "Emitido eletronicamente pelo Hospital São Gabriel — HSG HIS.",
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, CINZA));
        rodape.setAlignment(Element.ALIGN_CENTER);
        rodape.setSpacingBefore(40);
        doc.add(rodape);
    }

    private static Paragraph paragrafo(String texto, int tamanho, int style, Color cor, int align) {
        Paragraph p = new Paragraph(texto, FontFactory.getFont(FontFactory.HELVETICA, tamanho, style, cor));
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
