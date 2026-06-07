package br.com.hsg.service.facade.storage;

public class BucketBinding {

    private final String bucket;
    private final String objectKey;

    public BucketBinding(String bucket, String objectKey) {
        if (bucket == null || bucket.trim().isEmpty()) {
            throw new IllegalArgumentException("Bucket é obrigatório.");
        }
        if (objectKey == null || objectKey.trim().isEmpty()) {
            throw new IllegalArgumentException("ObjectKey é obrigatório.");
        }
        this.bucket    = bucket;
        this.objectKey = objectKey;
    }

    public String getBucket()    { return bucket; }
    public String getObjectKey() { return objectKey; }
}
