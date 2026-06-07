package br.com.hsg.service.facade.storage;

public class StoragePutResult {

    private final String pathLogico;
    private final long sizeBytes;
    private final String contentType;
    private final String etag;

    public StoragePutResult(String pathLogico, long sizeBytes, String contentType, String etag) {
        this.pathLogico  = pathLogico;
        this.sizeBytes   = sizeBytes;
        this.contentType = contentType;
        this.etag        = etag;
    }

    public String getPathLogico()  { return pathLogico; }
    public long   getSizeBytes()   { return sizeBytes; }
    public String getContentType() { return contentType; }
    public String getEtag()        { return etag; }
}
