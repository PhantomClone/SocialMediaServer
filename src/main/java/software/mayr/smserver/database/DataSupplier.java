package software.mayr.smserver.database;

import software.mayr.smserver.data.DataAccess;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface DataSupplier<T> {

    CompletableFuture<Optional<T>> getData(DataAccess<?> dataAccess);

    Class<?> getGenericClass();

}
