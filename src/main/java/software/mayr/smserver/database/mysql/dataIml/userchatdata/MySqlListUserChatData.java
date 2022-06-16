package software.mayr.smserver.database.mysql.dataIml.userchatdata;

import software.mayr.smserver.data.DataAccess;
import software.mayr.smserver.data.userchatdata.UserChatData;
import software.mayr.smserver.data.userchatdata.UserChatDataAccess;
import software.mayr.smserver.data.userdata.UserData;
import software.mayr.smserver.storage.ListGlobalStorable;

import javax.sql.DataSource;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MySqlListUserChatData extends MySqlListUserChatDataQueries implements ListGlobalStorable<UserChatData> {

    public MySqlListUserChatData(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public CompletableFuture<List<UserChatData>> getDataOutOfGlobalStorage(DataAccess<?> dataAccess) {
        if (dataAccess.getDataClass() != UserData.class)
            throw new IllegalArgumentException("Wrong DataAccess");
        if (dataAccess instanceof UserChatDataAccess.UserUuidDataAccess userUuidDataAccess) {
            return getUserChats(userUuidDataAccess.userUuid());
        } else if (dataAccess instanceof UserChatDataAccess.ChatUuidDataAccess chatUuidDataAccess) {
            return getChatMemebers(chatUuidDataAccess.chatUuid());
        }
        throw new UnsupportedOperationException(String
                .format("%s is not suuported!", dataAccess.getClass().getSimpleName()));
    }
}
