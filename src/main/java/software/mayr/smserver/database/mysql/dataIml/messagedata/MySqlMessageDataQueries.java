package software.mayr.smserver.database.mysql.dataIml.messagedata;

import de.chojo.sqlutil.conversion.UUIDConverter;
import de.chojo.sqlutil.wrapper.QueryBuilder;
import de.chojo.sqlutil.wrapper.stage.StatementStage;
import software.mayr.smserver.data.messagedata.ContentType;
import software.mayr.smserver.data.messagedata.MessageData;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class MySqlMessageDataQueries extends MessageData {

    private final StatementStage<?> insertMessageQuery;

    public MySqlMessageDataQueries(DataSource dataSource) {
        this.insertMessageQuery = QueryBuilder.builder(dataSource).defaultConfig()
                .query("INSERT INTO Message(messageUuid, senderUuid, chatUuid, timeStamp, contentType, content) VALUES(?, ?, ?, ?, ?, ?)");
    }

    public String createTableSqlString() {
        return "CREATE TABLE IF NOT EXISTS Message(messageUuid BINARY(16) PRIMARY KEY NOT NULL, senderUuid BINARY(16) NOT NULL, chatUuid BINARY(16) NOT NULL, timeStamp DATETIME(3) NOT NULL, contentType ENUM('message', 'picture', 'audio', 'video') NOT NULL, content TEXT NOT NULL)";
    }

    public CompletableFuture<Integer> insertMessage(UUID messageUuid, UUID senderUuid, UUID chatUuid,
                                                    Timestamp timestamp, ContentType contentType, byte[] content) {
        return insertMessageQuery.params(preparedStatement -> {
            preparedStatement.setBytes(1, UUIDConverter.convert(messageUuid));
            preparedStatement.setBytes(2, UUIDConverter.convert(senderUuid));
            preparedStatement.setBytes(3, UUIDConverter.convert(chatUuid));

            preparedStatement.setTimestamp(4, timestamp);
            preparedStatement.setString(5, contentType.name());
            preparedStatement.setBlob(6, new ByteArrayInputStream(content));
        }).insert().execute();
    }

}
