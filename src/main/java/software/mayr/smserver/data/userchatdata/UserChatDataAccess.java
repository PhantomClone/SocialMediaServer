package software.mayr.smserver.data.userchatdata;

import software.mayr.smserver.data.DataAccess;

import java.sql.Timestamp;
import java.util.UUID;

public abstract class UserChatDataAccess  {

    public static DataAccess<UserChatData> getChatUuidDataAccess(UUID chatUuid) {
        return new ChatUuidDataAccess(chatUuid);
    }

    public static DataAccess<UserChatData> getUserUuidDataAccess(UUID userUuid) {
        return new UserUuidDataAccess(userUuid);
    }

    public static DataAccess<UserChatData> getRoleOfUserInChat(UUID userUuid, UUID chatUuid) {
        return new RoleOfUserInChat(userUuid, chatUuid);
    }

    public static DataAccess<UserChatData> isUserInChat(UUID userUuid, UUID chatUuid) {
        return new IsUserInChatDataAccess(userUuid, chatUuid);
    }

    public static DataAccess<UserChatData> create(UUID userUuid, UUID chatUuid, Role role, Timestamp timestamp) {
        return new CreateUserChatDataAccess(userUuid, chatUuid, role, timestamp);
    }

    public static DataAccess<UserChatData> setRoleInChat(UUID userUuid, UUID chatUuid, Role role) {
        return new SetRoleInChat(userUuid, chatUuid, role);
    }

    public static DataAccess<UserChatData> removeUserOutOfChat(UUID userUuid, UUID chatUuid) {
        return new RemoveUser(userUuid, chatUuid);
    }

    public record RemoveUser(UUID userUuid, UUID chatUuid) implements DataAccess<UserChatData> {

        @Override
        public Class<?> getDataClass() {
            return UserChatData.class;
        }
    }

    public record SetRoleInChat(UUID userUuid, UUID chatUuid, Role role) implements DataAccess<UserChatData> {

        @Override
        public Class<?> getDataClass() {
            return UserChatData.class;
        }
    }

    public record ChatUuidDataAccess(UUID chatUuid) implements DataAccess<UserChatData> {

        @Override
        public Class<?> getDataClass() {
            return UserChatData.class;
        }
    }

    public record IsUserInChatDataAccess(UUID userUuid, UUID chatUuid) implements DataAccess<UserChatData> {

        @Override
        public Class<?> getDataClass() {
            return UserChatData.class;
        }
    }

    public record UserUuidDataAccess(UUID userUuid) implements DataAccess<UserChatData> {

        @Override
        public Class<?> getDataClass() {
            return UserChatData.class;
        }
    }

    public record CreateUserChatDataAccess(UUID userUuid, UUID chatUuid, Role role, Timestamp timestamp)
            implements DataAccess<UserChatData> {

        @Override
        public Class<?> getDataClass() {
            return UserChatData.class;
        }
    }

    public record RoleOfUserInChat(UUID userUuid, UUID chatUuid) implements DataAccess<UserChatData> {

        @Override
        public Class<?> getDataClass() {
            return UserChatData.class;
        }
    }

}
