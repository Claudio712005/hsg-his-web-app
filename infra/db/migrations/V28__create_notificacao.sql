CREATE SEQUENCE hsg.seq_notificacao
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE hsg.tb_notificacao (
    id_notificacao    BIGINT       PRIMARY KEY DEFAULT nextval('hsg.seq_notificacao'),
    tp_destinatario   VARCHAR(15)  NOT NULL,
    id_destinatario   BIGINT       NOT NULL,
    ds_titulo         VARCHAR(200) NOT NULL,
    ds_mensagem       VARCHAR(1000) NOT NULL,
    ds_link           VARCHAR(500),
    tp_notificacao    VARCHAR(12)  NOT NULL,
    tp_categoria      VARCHAR(12)  NOT NULL,
    fl_lida           CHAR(1)      NOT NULL DEFAULT 'N',
    dt_leitura        TIMESTAMP,
    dt_criacao        TIMESTAMP    NOT NULL DEFAULT NOW(),
    dt_expiracao      TIMESTAMP    NOT NULL,
    CONSTRAINT ck_notif_tp_destinatario CHECK (tp_destinatario IN ('PACIENTE','MEDICO','ENFERMEIRO','ADMIN')),
    CONSTRAINT ck_notif_tipo            CHECK (tp_notificacao IN ('INFO','SUCESSO','ALERTA','ERRO')),
    CONSTRAINT ck_notif_categoria       CHECK (tp_categoria IN ('CONSULTA','CONVENIO','AGENDA','SISTEMA')),
    CONSTRAINT ck_notif_lida            CHECK (fl_lida IN ('S','N'))
);

CREATE INDEX idx_notif_dest_lida  ON hsg.tb_notificacao (tp_destinatario, id_destinatario, fl_lida);
CREATE INDEX idx_notif_expiracao  ON hsg.tb_notificacao (dt_expiracao);
CREATE INDEX idx_notif_criacao    ON hsg.tb_notificacao (dt_criacao DESC);
