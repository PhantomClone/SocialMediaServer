package software.mayr.smserver.database.mysql;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.mayr.smserver.data.userchatdata.Role;
import software.mayr.smserver.data.userchatdata.UserChatData;
import software.mayr.smserver.data.userchatdata.UserChatDataAccess;
import software.mayr.smserver.data.userdata.UserData;
import software.mayr.smserver.data.userdata.UserDataAccess;
import software.mayr.smserver.database.DataRegistry;

import java.sql.Timestamp;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class TestMySqlDataRegistry {

    private static final DataRegistry dataRegistry;

    static {
        dataRegistry  = MySqlDataRegistry.create(
                "127.0.0.1",
                "socialmedia",
                3306,
                "root",
                "S0c1alM3dia!"
        );
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(15);
                dataRegistry.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static UUID userUuid = UUID.fromString("791addad-5ff2-49bd-ac04-d94f58ae3e0e"),
            chatUuid = UUID.fromString("2952d6ba-ec3c-437f-b115-9ea1d1e75ee9");

    @Test
    public void test() {
        testSetUpTables();
        checkUserExists();
    }

    @Test
    public void testSetUpTables() {
        long start = System.currentTimeMillis();
        CompletableFuture<Integer> integerCompletableFuture = dataRegistry.setUpDataRegistry();
        integerCompletableFuture.whenComplete((integer, throwable) -> System.out.println("Setup (" + (System.currentTimeMillis() - start) + "ms)"));
    }

    @Test
    public void checkUserExists() {
        long start = System.currentTimeMillis();
        dataRegistry.getDataSupplier(UserData.class).getData(UserDataAccess.getDoesUsernameExistAccess("PhantomClone"))
                .whenComplete((optionalUserData, throwable) -> {
                    System.out.println("CheckUser (" + (System.currentTimeMillis() - start) + "ms)");
                    optionalUserData.ifPresentOrElse(userData -> System.out.println("User PhantomClone already registered"), this::registerUser);
                });
    }

    public void registerUser() {
        long start = System.currentTimeMillis();
        dataRegistry.getDataSupplier(UserData.class)
                .getData(UserDataAccess.getCreateUserDataAccess(
                        userUuid, "PhantomClone", "password", "email"
                )).whenComplete((optionalUserData, throwable) -> {
                    System.out.println("RegisterUser (" + (System.currentTimeMillis() - start) + "ms)");

            optionalUserData.ifPresent(userData -> System.out.printf("Uuid(%s)\nUserName(%s)\nPassword(%s)\nEmail(%s)\n",
                    userData.getUserUuid().toString(), userData.getUserName(), userData.getPassword(), userData.getEmail()));
        });
    }

    @Test
    public void checkIfChatExist() {
        long start = System.currentTimeMillis();
        dataRegistry.getDataSupplier(UserChatData.class).getData(UserChatDataAccess.isUserInChat(userUuid, chatUuid))
                .whenComplete((optionalUserChatData, throwable) -> {
                    System.out.println("CheckUserInChat (" + (System.currentTimeMillis() - start) + "ms)");
                    optionalUserChatData.ifPresentOrElse(userChatData -> System.out.println("User is in Chat"),
                            this::setUserInChat);
                });
    }

    public void setUserInChat() {
        long start = System.currentTimeMillis();
        dataRegistry.getDataSupplier(UserChatData.class).getData(UserChatDataAccess.create(
                userUuid, chatUuid, Role.ADMIN, new Timestamp(System.currentTimeMillis())
        )).whenComplete((optionalUserChatData, throwable) -> {
            System.out.println("setUserInChat (" + (System.currentTimeMillis() - start) + "ms)");
            optionalUserChatData.ifPresentOrElse(userChatData ->
                System.out.printf("User %s is now in chat\n", userChatData.getUserUuid().toString()),
                    () -> System.out.println("User cant be added in chat."));
        });

    }

    @BeforeAll
    static void nextTest() {
        System.out.println("Start next Test");
    }

    @AfterEach
    public void waitNext() {
        System.out.println("Waiting... (3s)");
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
