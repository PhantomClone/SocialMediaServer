package software.mayr.smserver.database.mysql.dataIml.userchatdata;

import de.chojo.sqlutil.conversion.UUIDConverter;
import de.chojo.sqlutil.wrapper.QueryBuilder;
import de.chojo.sqlutil.wrapper.QueryBuilderConfig;
import de.chojo.sqlutil.wrapper.stage.StatementStage;
import de.chojo.sqlutil.wrapper.stage.UpdateStage;
import org.jetbrains.annotations.NotNull;
import software.mayr.smserver.data.userchatdata.Role;
import software.mayr.smserver.data.userchatdata.UserChatData;

import javax.sql.DataSource;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public abstract class MySqlUserChatDataQueries extends UserChatData {

    private final StatementStage<?> insertUserChatQuery, updateUserChatQuery, removeUserQuery;
    private final StatementStage<UserChatData> findUser;

    public MySqlUserChatDataQueries(DataSource dataSource) {
        this.insertUserChatQuery = QueryBuilder.builder(dataSource).defaultConfig()
                .query("INSERT INTO UserChat(userUuid, chatUuid, role, timeStamp) VALUES(?, ?, ?, ?)");
        this.updateUserChatQuery = QueryBuilder.builder(dataSource).defaultConfig()
                .query("UPDATE UserChat SET role=? WHERE userUuid=? AND chatUuid=?");
        this.findUser = QueryBuilder.builder(dataSource, UserChatData.class).defaultConfig()
                .query("SELECT role, timeStamp FROM UserChat WHERE userUuid=? AND chatUuid=?");
        this.removeUserQuery = QueryBuilder.builder(dataSource).defaultConfig()
                .query("DELETE FROM UserChat WHERE userUuid=? AND chatUuid=?");
    }

    public String createTableSqlString() {
        return "CREATE TABLE IF NOT EXISTS UserChat(userUuid BINARY(16) PRIMARY KEY NOT NULL, chatUuid BINARY(16) NOT NULL, role ENUM('member', 'moderator', 'admin'), timeStamp DATETIME(3) NOT NULL)";
    }

    public CompletableFuture<Integer> insertUserChat(UUID userUuid, UUID chatUuid, Role role, Timestamp timestamp) {
        return this.insertUserChatQuery.params(preparedStatement -> {
            preparedStatement.setBytes(1, UUIDConverter.convert(userUuid));
            preparedStatement.setBytes(2, UUIDConverter.convert(chatUuid));
            preparedStatement.setString(3, role.toString());
            preparedStatement.setTimestamp(4, timestamp);
        }).insert().execute();
    }

    public CompletableFuture<Optional<UserChatData>> updateUserChat(UUID userUuid, UUID chatUuid, Role role) {
        return this.updateUserChatQuery.params(preparedStatement -> {
            preparedStatement.setString(1, role.toString());
            preparedStatement.setBytes(2, UUIDConverter.convert(userUuid));
            preparedStatement.setBytes(3, UUIDConverter.convert(chatUuid));
        }).update().execute().thenApply(integer -> {
            if (integer != 1) {
                return Optional.empty();
            }
            setUserUuid(userUuid);
            setChatUuid(chatUuid);
            setRole(role);
            return Optional.of(this);
        });
    }

    public CompletableFuture<Optional<UserChatData>> userChatExist(UUID userUuid, UUID chatUuid) {
        return this.findUser.params(preparedStatement -> {
            preparedStatement.setBytes(1, UUIDConverter.convert(userUuid));
            preparedStatement.setBytes(2, UUIDConverter.convert(chatUuid));
        }).readRow(resultSet -> {
            setUserUuid(userUuid);
            setChatUuid(chatUuid);
            setRole(Role.valueOf(resultSet.getString("role").toUpperCase()));
            setTimestamp(resultSet.getTimestamp("timeStamp"));
           return this;
        }).first();
    }

    public CompletableFuture<Optional<UserChatData>> removeUser(UUID userUuid, UUID chatUuid) {
        return this.removeUserQuery.params(preparedStatement -> {
            preparedStatement.setBytes(1, UUIDConverter.convert(userUuid));
            preparedStatement.setBytes(2, UUIDConverter.convert(chatUuid));
        }).update().execute().thenApply(integer -> {
            if (integer != 1) {
                return Optional.empty();
            }
            setUserUuid(userUuid);
            setChatUuid(chatUuid);
            return Optional.of(this);
        });
    }

}
