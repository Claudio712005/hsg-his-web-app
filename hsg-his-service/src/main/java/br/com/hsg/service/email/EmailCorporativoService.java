package br.com.hsg.service.email;

import br.com.hsg.dao.PreCadastroProfissionalDAO;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.text.Normalizer;

@Stateless
public class EmailCorporativoService {

    private static final String DOMINIO =
            System.getenv().getOrDefault("HSG_EMAIL_DOMINIO", "hsg.com.br");

    @EJB
    private PreCadastroProfissionalDAO preCadastroDAO;

    public String gerar(String nomeCompleto) {
        String base = extrairBase(nomeCompleto);

        String candidato = base + "@" + DOMINIO;
        if (!preCadastroDAO.existeEmailCorporativo(candidato)) {
            return candidato;
        }

        int sufixo = 2;
        while (preCadastroDAO.existeEmailCorporativo(base + sufixo + "@" + DOMINIO)) {
            sufixo++;
        }
        return base + sufixo + "@" + DOMINIO;
    }

    private String extrairBase(String nomeCompleto) {
        if (nomeCompleto == null || nomeCompleto.trim().isEmpty()) {
            return "profissional";
        }

        String normalizado = Normalizer
                .normalize(nomeCompleto.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase()
                .replaceAll("[^a-z\\s]", "")
                .trim()
                .replaceAll("\\s+", " ");

        String[] partes = normalizado.split(" ");
        if (partes.length == 1) {
            return partes[0];
        }
        return partes[0] + "." + partes[partes.length - 1];
    }
}
