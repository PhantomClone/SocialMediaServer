package software.mayr.smserver.web.httphandler.request;

import org.json.JSONObject;
import software.mayr.smserver.data.userchatdata.Role;
import software.mayr.smserver.data.userchatdata.UserChatData;
import software.mayr.smserver.data.userchatdata.UserChatDataAccess;
import software.mayr.smserver.data.userdata.UserData;
import software.mayr.smserver.web.httphandler.AuthenticatedRequest;
import software.mayr.smserver.web.httphandler.RequestMethod;
import software.mayr.smserver.web.httphandler.Result;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author PhantomClone
 */
public abstract class ChatActionRequest extends AuthenticatedRequest<UserChatData> {

    @Override
    public CompletableFuture<Result> handle(RequestMethod requestMethod, UserChatData data, UserData loggedInUserData) {
        try {
            Action action = (Action) data.getClass().getDeclaredMethod("getAction").invoke(data);
            return switch (action) {
                case CREATE -> {
                    if (data.getUserUuid().equals(loggedInUserData.getUserUuid()))
                        yield createUserChat(data);
                    else
                        yield CompletableFuture.completedFuture(getBadAuthorizedResult());
                }
                case ADDUSER -> {
                    CompletableFuture<Result> resultCompletableFuture = new CompletableFuture<>();
                    getDataRegistry().getDataSupplier(UserChatData.class).getData(UserChatDataAccess.isUserInChat(
                            loggedInUserData.getUserUuid(), data.getChatUuid()
                    )).whenComplete((optionalUserChatData, throwable) -> {
                        optionalUserChatData.ifPresentOrElse(userChatData -> {
                            if (userChatData.getRole() == Role.MEMBER)
                                resultCompletableFuture.complete(getBadAuthorizedResult());
                            else {
                                addUserInChat(data).whenComplete((result, throwable1) -> resultCompletableFuture.complete(result));
                            }
                        }, () -> resultCompletableFuture.complete(getBadAuthorizedResult()));
                    });
                    yield resultCompletableFuture;
                }
                case CHANGEROLE -> {
                    CompletableFuture<Result> resultCompletableFuture = new CompletableFuture<>();
                    getDataRegistry().getDataSupplier(UserChatData.class).getData(UserChatDataAccess.isUserInChat(
                            loggedInUserData.getUserUuid(),
                            data.getChatUuid()
                    )).whenComplete((optionalUserChatData, throwable) -> {
                        optionalUserChatData.ifPresentOrElse(userChatData -> {
                            if (userChatData.getRole() != Role.ADMIN)
                                resultCompletableFuture.complete(getBadAuthorizedResult());
                            else {
                                changeRoleOfUserInChat(data).whenComplete((result, throwable1) -> resultCompletableFuture.complete(result));
                            }
                        }, () -> resultCompletableFuture.complete(getBadAuthorizedResult()));
                    });
                    yield resultCompletableFuture;
                }
                case REMOVEUSER -> {
                    CompletableFuture<Result> resultCompletableFuture = new CompletableFuture<>();
                    getDataRegistry().getDataSupplier(UserChatData.class)
                            .getData(UserChatDataAccess.isUserInChat(data.getUserUuid(), data.getChatUuid()))
                            .whenComplete((optionalUserChatData, throwable) ->
                                optionalUserChatData.ifPresentOrElse(userChatData -> {
                                    if (data.getUserUuid().equals(loggedInUserData.getUserUuid())) {
                                        removeUserOfChat(data).whenComplete((result, throwable1) ->
                                            resultCompletableFuture.complete(result)
                                        );
                                    } else {
                                        getDataRegistry().getDataSupplier(UserChatData.class)
                                                .getData(UserChatDataAccess
                                                        .isUserInChat(
                                                                loggedInUserData.getUserUuid(), data.getChatUuid()
                                                        )
                                                ).whenComplete((optionalUserChatData1, throwable1) ->
                                                    optionalUserChatData1.ifPresentOrElse(foundUserChatData -> {
                                                        if (foundUserChatData.getRole() == Role.MEMBER
                                                                || (foundUserChatData.getRole() == Role.MODERATOR
                                                                && userChatData.getRole() == Role.ADMIN)) {
                                                            resultCompletableFuture.complete(getBadAuthorizedResult());
                                                        } else {
                                                            removeUserOfChat(data).whenComplete((result, throwable2) ->
                                                                    resultCompletableFuture.complete(result)
                                                            );
                                                        }
                                                    }, () -> resultCompletableFuture.complete(notInChat("You")))
                                                );
                                    }
                                }, () -> resultCompletableFuture.complete(notInChat("User")))
                            );
                    yield resultCompletableFuture;
                }
            };
        } catch (Exception ignored) {
            return CompletableFuture.completedFuture(new Result("Internal Server Error", 500));
        }
    }

    private Result notInChat(String who) {
        return new Result(String.format("%s not in chat", who), 403);
    }

    private CompletableFuture<Result> createUserChat(UserChatData userChatData) {
        return getDataRegistry().getDataSupplier(UserChatData.class).getData(UserChatDataAccess.create(
                userChatData.getUserUuid(), userChatData.getChatUuid(), userChatData.getRole(), userChatData.getTimestamp()
                )).thenApply(optionalUserChatData -> optionalUserChatData.map(chatData -> new Result(new JSONObject().put("chatUuid", chatData.getChatUuid()).toString(), 201)).orElseGet(this::internalError));
    }

    private CompletableFuture<Result> addUserInChat(UserChatData userChatData) {
        return getDataRegistry().getDataSupplier(UserChatData.class).getData(UserChatDataAccess.create(
                userChatData.getUserUuid(), userChatData.getChatUuid(), userChatData.getRole(), userChatData.getTimestamp()
        )).thenApply(optionalUserChatData -> optionalUserChatData.map(chatData -> new Result("Added User in Chat", 201)).orElseGet(this::internalError));
    }

    private CompletableFuture<Result> removeUserOfChat(UserChatData userChatData) {
        return getDataRegistry().getDataSupplier(UserChatData.class).getData(UserChatDataAccess.removeUserOutOfChat(
                userChatData.getUserUuid(), userChatData.getChatUuid()
        )).thenApply(optionalUserChatData -> optionalUserChatData.isPresent() ?
                new Result("Removed user", 200) : internalError());
    }

    private CompletableFuture<Result> changeRoleOfUserInChat(UserChatData userChatData) {
        return getDataRegistry().getDataSupplier(UserChatData.class).getData(UserChatDataAccess.setRoleInChat(
                userChatData.getUserUuid(), userChatData.getChatUuid(), userChatData.getRole()
        )).thenApply(optionalUserChatData -> optionalUserChatData.map(foundUserChatData -> new Result("Updated user Role", 200)).orElseGet(this::internalError));
    }

    @Override
    public String getSubUrl() {
        return "/chataction";
    }

    @Override
    public List<RequestMethod> getRequestMethods() {
        return List.of(RequestMethod.POST);
    }

    @Override
    public boolean checkFormat(JSONObject jsonObject, RequestMethod requestMethod) {
        if (!jsonObject.has("action") && !jsonObject.has("userUuid"))
            return false;
        Action action = Action.getValueOf(jsonObject.getString("action"));
        if (action == null)
            return false;
        return switch (action) {
            case CREATE -> true;
            case ADDUSER, REMOVEUSER -> jsonObject.has("chatUuid");
            case CHANGEROLE -> jsonObject.has("chatUuid") && jsonObject.has("role");
        };
    }

    @SuppressWarnings({"all"})
    @Override
    public UserChatData parseJsonObject(JSONObject jsonObject, RequestMethod requestMethod) {
        Action action = Action.getValueOf(jsonObject.getString("action"));
        assert action != null;
        return switch (action) {
            case CREATE -> new UserChatData(UUID.fromString(jsonObject.getString("userUuid")),
                    UUID.randomUUID(), Role.ADMIN, new Timestamp(System.currentTimeMillis())) {
                private Action getAction() {
                    return action;
                }};
            case ADDUSER -> new UserChatData(UUID.fromString(jsonObject.getString("userUuid")),
                    UUID.fromString(jsonObject.getString("chatUuid")), Role.MEMBER,
                    new Timestamp(System.currentTimeMillis())) {
                private Action getAction() {
                    return action;
                }
            };
            case CHANGEROLE -> new UserChatData(UUID.fromString(jsonObject.getString("userUuid")),
                    UUID.fromString(jsonObject.getString("chatUuid")),
                    Role.getValueOf(jsonObject.getString("role")), new Timestamp(System.currentTimeMillis())) {
                private Action getAction() {
                    return action;
                }
            };
            case REMOVEUSER -> new UserChatData(UUID.fromString(jsonObject.getString("userUuid")),
                    UUID.fromString(jsonObject.getString("chatUuid")),
                    Role.MEMBER, new Timestamp(System.currentTimeMillis())
            ) {
                private Action getAction() {
                    return action;
                }
            };
        };
    }

    private enum Action {
        CREATE, ADDUSER, REMOVEUSER, CHANGEROLE;

        public static Action getValueOf(String stringAction) {
            stringAction = stringAction.toUpperCase();
            for (Action action : values()) {
                if (action.name().equals(stringAction))
                    return action;
            }
            return null;
        }
    }
}
