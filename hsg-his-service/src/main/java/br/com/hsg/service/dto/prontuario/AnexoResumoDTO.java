package br.com.hsg.service.dto.prontuario;

import java.io.Serializable;
import java.time.LocalDateTime;

public class AnexoResumoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long          id;
    private String        nomeOriginal;
    private String        contentType;
    private long          tamanhoBytes;
    private String        dominio;
    private LocalDateTime dataUpload;
    private String        responsavelTipo;

    public Long getId()                         { return id; }
    public void setId(Long v)                   { this.id = v; }
    public String getNomeOriginal()             { return nomeOriginal; }
    public void setNomeOriginal(String v)       { this.nomeOriginal = v; }
    public String getContentType()              { return contentType; }
    public void setContentType(String v)        { this.contentType = v; }
    public long getTamanhoBytes()               { return tamanhoBytes; }
    public void setTamanhoBytes(long v)         { this.tamanhoBytes = v; }
    public String getDominio()                  { return dominio; }
    public void setDominio(String v)            { this.dominio = v; }
    public LocalDateTime getDataUpload()        { return dataUpload; }
    public void setDataUpload(LocalDateTime v)  { this.dataUpload = v; }
    public String getResponsavelTipo()          { return responsavelTipo; }
    public void setResponsavelTipo(String v)    { this.responsavelTipo = v; }
}
