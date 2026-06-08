package br.com.hsg.service.dto.prontuario;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReceitaResumoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    public static class ItemDTO implements Serializable {
        private static final long serialVersionUID = 1L;
        private String medicamento;
        private String posologia;
        private String observacao;
        private String cid10;
        public String getMedicamento()        { return medicamento; }
        public void setMedicamento(String v)  { this.medicamento = v; }
        public String getPosologia()          { return posologia; }
        public void setPosologia(String v)    { this.posologia = v; }
        public String getObservacao()         { return observacao; }
        public void setObservacao(String v)   { this.observacao = v; }
        public String getCid10()              { return cid10; }
        public void setCid10(String v)        { this.cid10 = v; }
    }

    private Long          id;
    private LocalDateTime dataEmissao;
    private boolean       ativa;
    private List<ItemDTO> itens = new ArrayList<>();

    public Long getId()                         { return id; }
    public void setId(Long v)                   { this.id = v; }
    public LocalDateTime getDataEmissao()       { return dataEmissao; }
    public void setDataEmissao(LocalDateTime v) { this.dataEmissao = v; }
    public boolean isAtiva()                    { return ativa; }
    public void setAtiva(boolean v)             { this.ativa = v; }
    public List<ItemDTO> getItens()             { return itens; }
    public void setItens(List<ItemDTO> v)       { this.itens = v; }
}
