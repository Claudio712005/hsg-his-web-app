package br.com.hsg.service.facade.storage;

import br.com.hsg.domain.enums.StorageDomain;

public interface StoragePathResolver {

    String buildLogicalPath(StorageDomain dominio, long ownerId, String filename);

    BucketBinding resolve(String pathLogico);
}
