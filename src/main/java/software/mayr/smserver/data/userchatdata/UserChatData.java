package software.mayr.smserver.data.userchatdata;

import org.json.JSONObject;
import software.mayr.smserver.data.Data;

import java.sql.Timestamp;
import java.util.UUID;

public abstract class UserChatData implements Data {

    private UUID userUuid;
    private UUID chatUuid;
    private Role role;
    private Timestamp timestamp;

    public UserChatData() {}

    public UserChatData(UUID userUuid, UUID chatUuid, Role role, Timestamp timestamp) {
        this.userUuid = userUuid;
        this.chatUuid = chatUuid;
        this.role = role;
        this.timestamp = timestamp;
    }

    @Override
    public String toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userUuid", this.userUuid.toString());
        jsonObject.put("chatUuid", this.chatUuid.toString());
        jsonObject.put("role", this.role.toString());
        jsonObject.put("timeStamp", this.timestamp);
        return jsonObject.toString();
    }

    public UUID getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(UUID userUuid) {
        this.userUuid = userUuid;
    }

    public UUID getChatUuid() {
        return chatUuid;
    }

    public void setChatUuid(UUID chatUuid) {
        this.chatUuid = chatUuid;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
