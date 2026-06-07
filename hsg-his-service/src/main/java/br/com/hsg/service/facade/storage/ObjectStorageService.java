package br.com.hsg.service.facade.storage;

import java.io.InputStream;
import java.net.URL;
import java.time.Duration;

public interface ObjectStorageService {

    StoragePutResult put(String pathLogico, InputStream stream, long size, String contentType);

    byte[] get(String pathLogico);

    URL presignedGet(String pathLogico, Duration ttl);

    void delete(String pathLogico);

    boolean exists(String pathLogico);
}
