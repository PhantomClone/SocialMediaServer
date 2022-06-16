package software.mayr.smserver.database.mysql.dataIml.userchatdata;

import software.mayr.smserver.data.DataAccess;
import software.mayr.smserver.data.userchatdata.UserChatData;
import software.mayr.smserver.database.DataSupplier;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public record MySqlListUserChatDataSupplier(DataSource dataSource) implements DataSupplier<List<UserChatData>> {

    @Override
    public CompletableFuture<Optional<List<UserChatData>>> getData(DataAccess<?> dataAccess) {
        return new MySqlListUserChatData(dataSource()).getDataOutOfGlobalStorage(dataAccess).thenApply(Optional::of);
    }

    @Override
    public Class<?> getGenericClass() {
        return UserChatData.class;
    }
}
