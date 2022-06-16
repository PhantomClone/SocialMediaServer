package software.mayr.smserver.database.mysql.dataIml.userchatdata;

import de.chojo.sqlutil.conversion.UUIDConverter;
import de.chojo.sqlutil.wrapper.QueryBuilder;
import de.chojo.sqlutil.wrapper.stage.StatementStage;
import software.mayr.smserver.data.userchatdata.Role;
import software.mayr.smserver.data.userchatdata.UserChatData;

import javax.sql.DataSource;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class MySqlListUserChatDataQueries {

        private final StatementStage<UserChatData> selectUserQuery, selectChatQuery;
        
        public MySqlListUserChatDataQueries(DataSource dataSource) {
            this.selectUserQuery= QueryBuilder.builder(dataSource, UserChatData.class).defaultConfig()
                    .query("SELECT * FROM UserChat WHERE userUuid=?");
            this.selectChatQuery = QueryBuilder.builder(dataSource, UserChatData.class).defaultConfig()
                    .query("SELECT * FROM UserChat WHERE chatUuid=?");
        }
        
        public CompletableFuture<List<UserChatData>> getUserChats(UUID userUuid) {
            return this.selectUserQuery.params(preparedStatement ->
                            preparedStatement.setBytes(1, UUIDConverter.convert(userUuid)))
                    .readRow(resultSet -> new UserChatData(
                            userUuid,
                            UUIDConverter.convert(resultSet.getBytes("chatUuid")),
                            Role.valueOf(resultSet.getString("role")),
                            resultSet.getTimestamp("timeStamp")
                    ) {}).all();
        }

        public CompletableFuture<List<UserChatData>> getChatMemebers(UUID chatUuid) {
            return this.selectUserQuery.params(preparedStatement ->
                            preparedStatement.setBytes(1, UUIDConverter.convert(chatUuid)))
                    .readRow(resultSet -> new UserChatData(
                            UUIDConverter.convert(resultSet.getBytes("userUuid")),
                            chatUuid,
                            Role.valueOf(resultSet.getString("role")),
                            resultSet.getTimestamp("timeStamp")
                    ) {}).all();
        }
    }