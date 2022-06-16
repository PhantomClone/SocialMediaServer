package software.mayr.smserver.database.mysql.dataIml.messagedata;

import software.mayr.smserver.data.DataAccess;
import software.mayr.smserver.data.messagedata.MessageData;
import software.mayr.smserver.database.DataSupplier;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public record MySqlListMessageDataSupplier(DataSource dataSource) implements DataSupplier<List<MessageData>> {

    @Override
    public CompletableFuture<Optional<List<MessageData>>> getData(DataAccess<?> dataAccess) {
        return new MySqlListMessageData(dataSource()).getDataOutOfGlobalStorage(dataAccess).thenApply(Optional::of);
    }

    @Override
    public Class<?> getGenericClass() {
        return MessageData.class;
    }
}
