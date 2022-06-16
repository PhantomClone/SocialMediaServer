package software.mayr.smserver.data.userdata;

import software.mayr.smserver.data.DataAccess;

import java.util.UUID;

public abstract class UserDataAccess {

    public static DataAccess<?> getDoesUsernameExistAccess(String username) {
        return new DoesUsernameExistAccess(username);
    }

    public static DataAccess<?> getCreateUserDataAccess(UUID userUuid, String username, String password, String email) {
        return new CreateUserDataAccess(userUuid, username, password, email);
    }

    public static DataAccess<?> getUserNameDataAccess(String username, String password) {
        return new UserNameDataAccess(username, password);
    }

    public static DataAccess<UserData> getUserUuidDataAccess(UUID userUuid, String password) {
        return new UserUuidDataAccess(userUuid, password);
    }

    public record DoesUsernameExistAccess(String username) implements DataAccess<UserData> {

        @Override
        public Class<?> getDataClass() {
            return UserData.class;
        }
    }

    public static record CreateUserDataAccess(UUID userUuid, String username, String password, String email)
            implements DataAccess<UserData> {

        @Override
        public Class<?> getDataClass() {
            return UserData.class;
        }
    }

    public record UserNameDataAccess(String username, String password) implements DataAccess<UserData> {
        @Override
        public Class<?> getDataClass() {
            return UserData.class;
        }
    }

    public record UserUuidDataAccess(UUID userUuid, String password) implements DataAccess<UserData> {
        @Override
        public Class<?> getDataClass() {
            return UserData.class;
        }
    }
}
