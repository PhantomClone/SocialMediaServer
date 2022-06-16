package software.mayr.smserver.database.mysql.dataIml.messagedata;

import software.mayr.smserver.data.DataAccess;
import software.mayr.smserver.data.messagedata.MessageData;
import software.mayr.smserver.data.messagedata.MessageDataAccess;
import software.mayr.smserver.storage.ListGlobalStorable;

import javax.sql.DataSource;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MySqlListMessageData extends MySqlListMessageDataQueries implements ListGlobalStorable<MessageData> {

    public MySqlListMessageData(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public CompletableFuture<List<MessageData>> getDataOutOfGlobalStorage(DataAccess<?> dataAccess) {
        if (dataAccess.getDataClass() != MessageData.class)
            throw new IllegalArgumentException("Wrong DataAccess");
        if (dataAccess instanceof MessageDataAccess.MessagesDataByChatUuid messagesDataByChatUuid) {
            return getMessagesDataByChat(messagesDataByChatUuid.chatUuid(), messagesDataByChatUuid.timestamp());
        } else if (dataAccess instanceof MessageDataAccess.MessagesDataByUserUuidChatUuid messagesDataByUserUuidChatUuid) {
            return getMessagesDataByUserAndChat(messagesDataByUserUuidChatUuid.userUuid(),
                    messagesDataByUserUuidChatUuid.chatUuid(), messagesDataByUserUuidChatUuid.timestamp());
        }
        throw new UnsupportedOperationException(String
                .format("%s is not suuported!", dataAccess.getClass().getSimpleName()));
    }
}
