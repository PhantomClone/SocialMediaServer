package software.mayr.smserver.storage;

import software.mayr.smserver.data.DataAccess;

import java.util.concurrent.CompletableFuture;

public interface GlobalStorable<T> {

    CompletableFuture<T> getDataOutOfGlobalStorage(DataAccess<?> dataAccess);

}
