package software.mayr.smserver.data.messagedata;

import org.json.JSONObject;
import software.mayr.smserver.data.Data;

import java.util.UUID;

public abstract class MessageData implements Data {

    private UUID messageUuid;
    private UUID senderUuid;
    private UUID chatUuid;
    private Long timeStamp;
    private ContentType contentType;
    private byte[] content;

    public MessageData() {}

    public MessageData(UUID messageUuid, UUID senderUuid, UUID chatUuid, Long timeStamp, ContentType contentType, byte[] content) {
        this.messageUuid = messageUuid;
        this.senderUuid = senderUuid;
        this.chatUuid = chatUuid;
        this.timeStamp = timeStamp;
        this.contentType = contentType;
        this.content = content;
    }

    @Override
    public String toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("messageUuid", this.messageUuid.toString());
        jsonObject.put("senderUuid", this.senderUuid.toString());
        jsonObject.put("chatUuid", this.chatUuid.toString());
        jsonObject.put("timeStamp", this.timeStamp);
        jsonObject.put("contentType", this.contentType.toString());
        jsonObject.put("content", new String(content));
        return jsonObject.toString();
    }

    public UUID getMessageUuid() {
        return messageUuid;
    }

    public void setMessageUuid(UUID messageUuid) {
        this.messageUuid = messageUuid;
    }

    public UUID getSenderUuid() {
        return senderUuid;
    }

    public void setSenderUuid(UUID senderUuid) {
        this.senderUuid = senderUuid;
    }

    public UUID getChatUuid() {
        return chatUuid;
    }

    public void setChatUuid(UUID chatUuid) {
        this.chatUuid = chatUuid;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
