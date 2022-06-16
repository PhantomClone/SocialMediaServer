package software.mayr.smserver.data.userdata;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import software.mayr.smserver.data.Data;

import static java.util.Objects.requireNonNull;

import java.util.UUID;

public abstract class UserData implements Data {

    private UUID userUuid;
    private String userName;
    private String password;
    private String email;

    public UserData() {}

    public UserData(UUID userUuid, String userName, String password, String email) {
        this.userUuid = userUuid;
        this.userName = userName;
        this.password = password;
        this.email = email;
    }

    @Override
    public String toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userUuid", this.userUuid.toString());
        jsonObject.put("userName", this.userName);
        jsonObject.put("password", this.password);
        jsonObject.put("email", this.email);
        return jsonObject.toString();
    }

    public void setUserUuid(UUID userUuid) {
        this.userUuid = userUuid;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UUID getUserUuid() {
        return userUuid;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}
