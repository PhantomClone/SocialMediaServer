package software.mayr.smserver.database.mysql.dataIml.messagedata;

import software.mayr.smserver.data.DataAccess;
import software.mayr.smserver.data.messagedata.MessageData;
import software.mayr.smserver.data.messagedata.MessageDataAccess;
import software.mayr.smserver.data.userdata.UserData;
import software.mayr.smserver.data.userdata.UserDataAccess;
import software.mayr.smserver.storage.GlobalStorable;
import software.mayr.smserver.storage.StoreResult;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class MySqlMessageData extends MySqlMessageDataQueries implements GlobalStorable<Optional<MessageData>> {

    public MySqlMessageData(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public CompletableFuture<Optional<MessageData>> getDataOutOfGlobalStorage(DataAccess<?> dataAccess) {
        if (dataAccess.getDataClass() != MessageData.class)
            throw new IllegalArgumentException("Wrong DataAccess");
        if (dataAccess instanceof MessageDataAccess.CreateMessageDataAccess createMessageDataAccess) {
            return insertMessage(createMessageDataAccess.messageUuid(),
                    createMessageDataAccess.senderUuid(),
                    createMessageDataAccess.chatUuid(),
                    new Timestamp(createMessageDataAccess.timeStamp()),
                    createMessageDataAccess.contentType(),
                    createMessageDataAccess.content()).thenApply(integer -> {
                        if (integer != 1)
                            return Optional.empty();
                        setMessageUuid(createMessageDataAccess.messageUuid());
                        setChatUuid(createMessageDataAccess.chatUuid());
                        setTimeStamp(createMessageDataAccess.timeStamp());
                        setContentType(createMessageDataAccess.contentType());
                        setContent(createMessageDataAccess.content());
                return Optional.of(this);
            });
        }
        throw new UnsupportedOperationException(String
                .format("%s is not suuported!", dataAccess.getClass().getSimpleName()));
    }

}
