package software.mayr.smserver.database;

import software.mayr.smserver.data.Data;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface DataRegistry {

    CompletableFuture<Integer> setUpDataRegistry();
    <T extends Data> DataSupplier<T> getDataSupplier(Class<T> dataClazz);
    <T extends Data> DataSupplier<List<T>> getListDataSupplier(Class<T> dataClazz);
    void close();

}
