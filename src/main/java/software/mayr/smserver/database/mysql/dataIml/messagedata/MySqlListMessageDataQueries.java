package software.mayr.smserver.database.mysql.dataIml.messagedata;

import de.chojo.sqlutil.conversion.UUIDConverter;
import de.chojo.sqlutil.wrapper.QueryBuilder;
import de.chojo.sqlutil.wrapper.stage.StatementStage;
import software.mayr.smserver.data.messagedata.ContentType;
import software.mayr.smserver.data.messagedata.MessageData;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class MySqlListMessageDataQueries {

    private final StatementStage<MessageData> messageQueryByChat, messageQueryByUserChat;

    public MySqlListMessageDataQueries(DataSource dataSource) {
        this.messageQueryByChat = QueryBuilder.builder(dataSource, MessageData.class).defaultConfig()
                .query("SELECT * FROM Message WHERE chatUuid=? AND timeStamp>?");
        this.messageQueryByUserChat = QueryBuilder.builder(dataSource, MessageData.class).defaultConfig()
                .query("SELECT * FROM Message WHERE userUuid=? AND chatUuid=? AND timeStamp>?");
    }

    public CompletableFuture<List<MessageData>> getMessagesDataByChat(UUID chatUuid, Timestamp timestamp) {
        return this.messageQueryByChat.params(preparedStatement -> {
            preparedStatement.setBytes(1, UUIDConverter.convert(chatUuid));
            preparedStatement.setString(2, timestamp.toString());
        }).readRow(resultSet -> new MessageData(
                UUIDConverter.convert(resultSet.getBytes("messageUuid")),
                UUIDConverter.convert(resultSet.getBytes("senderUuid")),
                UUIDConverter.convert(resultSet.getBytes("chatUuid")),
                resultSet.getTimestamp("timeStamp").getTime(),
                ContentType.valueOf(resultSet.getString("contentType").toUpperCase()),
                resultSet.getBytes("content")
        ) {}).all();
    }

    public CompletableFuture<List<MessageData>> getMessagesDataByUserAndChat(UUID userUuid, UUID chatUuid,
                                                                             Timestamp timestamp) {
        return this.messageQueryByUserChat.params(preparedStatement -> {
            preparedStatement.setBytes(1, UUIDConverter.convert(userUuid));
            preparedStatement.setBytes(2, UUIDConverter.convert(chatUuid));
            preparedStatement.setString(3, timestamp.toString());
        }).readRow(resultSet -> new MessageData(
                UUIDConverter.convert(resultSet.getBytes("messageUuid")),
                userUuid, chatUuid,
                resultSet.getTimestamp("timeStamp").getTime(),
                ContentType.valueOf(resultSet.getString("contentType").toUpperCase()),
                resultSet.getBytes("content")
        ) {}).all();
    }
}
