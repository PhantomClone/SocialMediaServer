package software.mayr.smserver.database.mysql.dataIml.userdata;

import software.mayr.smserver.data.DataAccess;
import software.mayr.smserver.data.userdata.UserData;
import software.mayr.smserver.database.DataSupplier;
import software.mayr.smserver.database.mysql.dataIml.MySqlSetUpAbleDataSupplier;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public record MySqlUserDataSupplier(DataSource dataSource) implements MySqlSetUpAbleDataSupplier, DataSupplier<UserData> {

    @Override
    public CompletableFuture<Optional<UserData>> getData(DataAccess<?> dataAccess) {
        return new MySqlUserData(dataSource()).getDataOutOfGlobalStorage(dataAccess);
    }

    @Override
    public Class<UserData> getGenericClass() {
        return UserData.class;
    }

    @Override
    public String createTableSqlString() {
        return new MySqlUserData(dataSource()).createTableSqlString();
    }
}
