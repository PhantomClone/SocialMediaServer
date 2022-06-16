package software.mayr.smserver.web.httphandler.request;

import org.json.JSONObject;
import software.mayr.smserver.data.userdata.UserData;
import software.mayr.smserver.data.userdata.UserDataAccess;
import software.mayr.smserver.web.httphandler.Request;
import software.mayr.smserver.web.httphandler.RequestMethod;
import software.mayr.smserver.web.httphandler.Result;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class CreateUserRequest implements Request<UserData> {

    @Override
    public String getSubUrl() {
        return "/createUser";
    }

    @Override
    public List<RequestMethod> getRequestMethods() {
        return List.of(RequestMethod.POST);
    }

    @Override
    public CompletableFuture<Result> handle(RequestMethod requestMethod, UserData data) {
        CompletableFuture<Result> completableFuture = new CompletableFuture<>();
        CompletableFuture<Optional<UserData>> optionalUserDataCompletableFuture = getDataRegistry()
                .getDataSupplier(UserData.class)
                .getData(UserDataAccess.getDoesUsernameExistAccess(data.getUserName()));
        optionalUserDataCompletableFuture.thenApply(optionalUserDataExist -> optionalUserDataExist.isEmpty() ?
                getDataRegistry().getDataSupplier(UserData.class).getData(UserDataAccess.getCreateUserDataAccess(
                        data.getUserUuid(), data.getUserName(), data.getPassword(), data.getEmail()
                )).whenComplete((optionalUserData, throwable) ->
                        optionalUserData.ifPresentOrElse(userData ->
                                        completableFuture.complete(canCreateUser(userData.getUserUuid())),
                                () -> completableFuture.complete(canNotCreateUser())))
                : completableFuture.complete(userNameIsTaken()));
        return completableFuture;
    }

    private Result userNameIsTaken() {
        return new Result("Username is taken", 400);
    }

    private Result canCreateUser(UUID userUuid) {
        return new Result(new JSONObject().put("userUuid", userUuid.toString()).toString(), 201);
    }
    private Result canNotCreateUser() {
        return new Result("Can not create user", 502);
    }

    @Override
    public boolean checkFormat(JSONObject jsonObject, RequestMethod requestMethod) {
        return jsonObject.has("userName")
                && jsonObject.has("password") && jsonObject.has("email");
    }

    @Override
    public UserData parseJsonObject(JSONObject jsonObject, RequestMethod requestMethod) {
        return new UserData(
                UUID.randomUUID(),
                jsonObject.getString("userName"),
                jsonObject.getString("password"),
                jsonObject.getString("email")
        ) {};
    }
}
