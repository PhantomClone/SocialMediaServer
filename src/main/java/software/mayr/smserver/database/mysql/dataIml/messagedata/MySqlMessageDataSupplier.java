package software.mayr.smserver.database.mysql.dataIml.messagedata;

import software.mayr.smserver.data.DataAccess;
import software.mayr.smserver.data.messagedata.MessageData;
import software.mayr.smserver.database.DataSupplier;
import software.mayr.smserver.database.mysql.dataIml.MySqlSetUpAbleDataSupplier;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public record MySqlMessageDataSupplier(DataSource dataSource) implements MySqlSetUpAbleDataSupplier, DataSupplier<MessageData> {

    @Override
    public CompletableFuture<Optional<MessageData>> getData(DataAccess<?> dataAccess) {
        return new MySqlMessageData(dataSource()).getDataOutOfGlobalStorage(dataAccess);
    }

    @Override
    public Class<?> getGenericClass() {
        return MessageData.class;
    }

    @Override
    public String createTableSqlString() {
        return new MySqlMessageData(dataSource()).createTableSqlString();
    }
}
