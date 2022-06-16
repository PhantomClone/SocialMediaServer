package software.mayr.smserver.data.messagedata;

import software.mayr.smserver.data.DataAccess;

import java.sql.Timestamp;
import java.util.UUID;

public abstract class MessageDataAccess {

    public static CreateMessageDataAccess getCreateMessageDataAccess(UUID messageUuid, UUID senderUuid, UUID chatUuid,
                                                                     Long timeStamp,
                                          ContentType contentType, byte[] content) {
        return new CreateMessageDataAccess(messageUuid, senderUuid, chatUuid, timeStamp, contentType, content);
    }

    public static MessagesDataByChatUuid getMessagesDataByChatUuid(UUID chatUuid, Timestamp timestamp) {
        return new MessagesDataByChatUuid(chatUuid, timestamp);
    }

    public static MessagesDataByUserUuidChatUuid getMessagesDataByUserUuidChatUuid(UUID userUuid, UUID chatUuid,
                                                                                   Timestamp timestamp) {
        return new MessagesDataByUserUuidChatUuid(userUuid, chatUuid, timestamp);
    }

    public static record CreateMessageDataAccess(UUID messageUuid, UUID senderUuid, UUID chatUuid, Long timeStamp,
                                                 ContentType contentType, byte[] content)
            implements DataAccess<MessageData> {
        @Override
        public Class<?> getDataClass() {
            return MessageData.class;
        }
    }

    public static record MessagesDataByChatUuid(UUID chatUuid, Timestamp timestamp) implements DataAccess<MessageData> {

        @Override
        public Class<?> getDataClass() {
            return MessageData.class;
        }
    }


    public static record MessagesDataByUserUuidChatUuid(UUID userUuid, UUID chatUuid, Timestamp timestamp)
            implements DataAccess<MessageData> {

        @Override
        public Class<?> getDataClass() {
            return MessageData.class;
        }
    }

}
