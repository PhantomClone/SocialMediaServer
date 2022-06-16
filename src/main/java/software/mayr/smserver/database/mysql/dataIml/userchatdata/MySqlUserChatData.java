package software.mayr.smserver.database.mysql.dataIml.userchatdata;

import software.mayr.smserver.data.DataAccess;
import software.mayr.smserver.data.userchatdata.UserChatData;
import software.mayr.smserver.data.userchatdata.UserChatDataAccess;
import software.mayr.smserver.storage.GlobalStorable;
import software.mayr.smserver.storage.StoreResult;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class MySqlUserChatData extends MySqlUserChatDataQueries implements GlobalStorable<Optional<UserChatData>> {

    public MySqlUserChatData(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public CompletableFuture<Optional<UserChatData>> getDataOutOfGlobalStorage(DataAccess<?> dataAccess) {
        if (dataAccess.getDataClass() != UserChatData.class)
            throw new IllegalArgumentException("Wrong DataAccess");
        if (dataAccess instanceof UserChatDataAccess.CreateUserChatDataAccess createUserChatDataAccess) {
            return insertUserChat(createUserChatDataAccess.userUuid(), createUserChatDataAccess.chatUuid(),
                    createUserChatDataAccess.role(), createUserChatDataAccess.timestamp()).thenApply(integer -> {
                        if (integer != 1)
                            return Optional.empty();
                        setUserUuid(createUserChatDataAccess.userUuid());
                        setChatUuid(createUserChatDataAccess.chatUuid());
                        setRole(createUserChatDataAccess.role());
                        setTimestamp(createUserChatDataAccess.timestamp());
                        return Optional.of(this);
                    }
            );
        } else if (dataAccess instanceof UserChatDataAccess.IsUserInChatDataAccess isUserInChatDataAccess) {
            return userChatExist(isUserInChatDataAccess.userUuid(), isUserInChatDataAccess.chatUuid());
        } else if (dataAccess instanceof UserChatDataAccess.SetRoleInChat setRoleInChat) {
            return updateUserChat(setRoleInChat.userUuid(), setRoleInChat.chatUuid(), setRoleInChat.role());
        } else if (dataAccess instanceof UserChatDataAccess.RemoveUser removeUser) {
            return removeUser(removeUser.userUuid(), removeUser.chatUuid());
        }
        throw new UnsupportedOperationException(String
                .format("%s is not supported!", dataAccess.getClass().getSimpleName()));
    }

}
