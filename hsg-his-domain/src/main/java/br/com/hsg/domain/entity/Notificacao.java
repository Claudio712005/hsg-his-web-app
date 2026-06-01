package br.com.hsg.domain.entity;

import br.com.hsg.domain.enums.CategoriaNotificacao;
import br.com.hsg.domain.enums.TipoDestinatarioNotificacao;
import br.com.hsg.domain.enums.TipoNotificacao;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_NOTIFICACAO", schema = "hsg")
public class Notificacao {

    public static final int DIAS_RETENCAO = 40;

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_NOTIFICACAO")
    @SequenceGenerator(name = "SEQ_NOTIFICACAO", sequenceName = "SEQ_NOTIFICACAO", allocationSize = 1)
    @Column(name = "ID_NOTIFICACAO")
    private Long id;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "TP_DESTINATARIO", length = 15, nullable = false)
    private TipoDestinatarioNotificacao tipoDestinatario;

    @Getter
    @Column(name = "ID_DESTINATARIO", nullable = false)
    private Long idDestinatario;

    @Getter
    @Column(name = "DS_TITULO", length = 200, nullable = false)
    private String titulo;

    @Getter
    @Column(name = "DS_MENSAGEM", length = 1000, nullable = false)
    private String mensagem;

    @Getter
    @Column(name = "DS_LINK", length = 500)
    private String link;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "TP_NOTIFICACAO", length = 12, nullable = false)
    private TipoNotificacao tipo;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "TP_CATEGORIA", length = 12, nullable = false)
    private CategoriaNotificacao categoria;

    @Column(name = "FL_LIDA", length = 1, nullable = false)
    private String lida;

    @Getter
    @Column(name = "DT_LEITURA")
    private LocalDateTime dataLeitura;

    @Getter
    @Column(name = "DT_CRIACAO", nullable = false)
    private LocalDateTime dataCriacao;

    @Getter
    @Column(name = "DT_EXPIRACAO", nullable = false)
    private LocalDateTime dataExpiracao;

    protected Notificacao() {}

    public static Notificacao criar(TipoDestinatarioNotificacao tipoDestinatario,
                                     Long idDestinatario,
                                     String titulo, String mensagem,
                                     TipoNotificacao tipo, CategoriaNotificacao categoria,
                                     String link) {
        validar(tipoDestinatario, idDestinatario, titulo, mensagem, tipo, categoria);
        Notificacao n = new Notificacao();
        n.tipoDestinatario = tipoDestinatario;
        n.idDestinatario   = idDestinatario;
        n.titulo           = titulo.trim();
        n.mensagem         = mensagem.trim();
        n.tipo             = tipo;
        n.categoria        = categoria;
        n.link             = (link != null && !link.trim().isEmpty()) ? link.trim() : null;
        n.lida             = "N";
        n.dataCriacao      = LocalDateTime.now();
        n.dataExpiracao    = n.dataCriacao.plusDays(DIAS_RETENCAO);
        return n;
    }

    public static void validar(TipoDestinatarioNotificacao tipoDestinatario, Long idDestinatario,
                                String titulo, String mensagem,
                                TipoNotificacao tipo, CategoriaNotificacao categoria) {
        if (tipoDestinatario == null) {
            throw new IllegalArgumentException("O tipo de destinatário é obrigatório.");
        }
        if (idDestinatario == null) {
            throw new IllegalArgumentException("O destinatário é obrigatório.");
        }
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("O título é obrigatório.");
        }
        if (mensagem == null || mensagem.trim().isEmpty()) {
            throw new IllegalArgumentException("A mensagem é obrigatória.");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("O tipo da notificação é obrigatório.");
        }
        if (categoria == null) {
            throw new IllegalArgumentException("A categoria é obrigatória.");
        }
    }

    public void marcarComoLida() {
        if (isLida()) return;
        this.lida = "S";
        this.dataLeitura = LocalDateTime.now();
    }

    public boolean isLida() {
        return "S".equalsIgnoreCase(lida);
    }
}
