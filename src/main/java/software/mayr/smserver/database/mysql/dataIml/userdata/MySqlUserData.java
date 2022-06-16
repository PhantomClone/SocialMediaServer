package software.mayr.smserver.database.mysql.dataIml.userdata;

import software.mayr.smserver.data.DataAccess;
import software.mayr.smserver.data.userdata.UserData;
import software.mayr.smserver.data.userdata.UserDataAccess;
import software.mayr.smserver.storage.GlobalStorable;
import software.mayr.smserver.storage.StoreResult;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class MySqlUserData extends MySqlUserDataQueries implements GlobalStorable<Optional<UserData>> {

    public MySqlUserData(DataSource mySqlUserDataQueries) {
        super(mySqlUserDataQueries);
    }

    @Override
    public CompletableFuture<Optional<UserData>> getDataOutOfGlobalStorage(DataAccess<?> dataAccess) {
        if (dataAccess.getDataClass() != UserData.class)
            throw new IllegalArgumentException("Wrong DataAccess");
        if (dataAccess instanceof UserDataAccess.UserUuidDataAccess userUuidDataAccess) {
            return getUser(userUuidDataAccess.userUuid(), userUuidDataAccess.password());
        } else if (dataAccess instanceof UserDataAccess.UserNameDataAccess userNameDataAccess) {
            return getUser(userNameDataAccess.username(), userNameDataAccess.password());
        } else if (dataAccess instanceof UserDataAccess.CreateUserDataAccess createUserDataAccess) {
            setUserUuid(createUserDataAccess.userUuid());
            setUserName(createUserDataAccess.username());
            setPassword(createUserDataAccess.password());
            setEmail(createUserDataAccess.email());
            return insertUser(this).thenApply(integer -> Optional.ofNullable(integer == 1 ? this : null));
        } else if (dataAccess instanceof UserDataAccess.DoesUsernameExistAccess doesUsernameExistAccess) {
            return doesUserExist(doesUsernameExistAccess.username());
        }
        throw new UnsupportedOperationException(String
                .format("%s is not suuported!", dataAccess.getClass().getSimpleName()));
    }

}
