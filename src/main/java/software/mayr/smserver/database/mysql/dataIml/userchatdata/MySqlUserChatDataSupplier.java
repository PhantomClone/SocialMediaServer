package software.mayr.smserver.database.mysql.dataIml.userchatdata;

import software.mayr.smserver.data.DataAccess;
import software.mayr.smserver.data.userchatdata.UserChatData;
import software.mayr.smserver.database.DataSupplier;
import software.mayr.smserver.database.mysql.dataIml.MySqlSetUpAbleDataSupplier;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public record MySqlUserChatDataSupplier(DataSource dataSource) implements MySqlSetUpAbleDataSupplier, DataSupplier<UserChatData> {

    @Override
    public CompletableFuture<Optional<UserChatData>> getData(DataAccess<?> dataAccess) {
        return new MySqlUserChatData(dataSource()).getDataOutOfGlobalStorage(dataAccess);
    }

    @Override
    public Class<?> getGenericClass() {
        return UserChatData.class;
    }

    @Override
    public String createTableSqlString() {
        return new MySqlUserChatData(dataSource()).createTableSqlString();
    }
}
