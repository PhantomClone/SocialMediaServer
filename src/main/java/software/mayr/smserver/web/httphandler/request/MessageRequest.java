package software.mayr.smserver.web.httphandler.request;

import org.json.JSONArray;
import org.json.JSONObject;
import software.mayr.smserver.data.messagedata.ContentType;
import software.mayr.smserver.data.messagedata.MessageData;
import software.mayr.smserver.data.messagedata.MessageDataAccess;
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
public abstract class MessageRequest extends AuthenticatedRequest<MessageData> {

    @Override
    public CompletableFuture<Result> handle(RequestMethod requestMethod, MessageData data, UserData loggedInUserData) {
        CompletableFuture<Result> resultCompletableFuture = new CompletableFuture<>();
        if (requestMethod == RequestMethod.POST) {
            if (data.getSenderUuid().equals(loggedInUserData.getUserUuid())) {
                getDataRegistry().getDataSupplier(UserChatData.class).getData(UserChatDataAccess.isUserInChat(
                        data.getSenderUuid(), data.getChatUuid()
                )).whenComplete((optionalUserChatData, throwable) ->
                    optionalUserChatData.ifPresentOrElse(userChatData ->
                        getDataRegistry().getDataSupplier(MessageData.class).getData(MessageDataAccess.getCreateMessageDataAccess(
                                data.getMessageUuid(),
                                data.getSenderUuid(),
                                data.getChatUuid(),
                                data.getTimeStamp(),
                                data.getContentType(),
                                data.getContent()
                        )).whenComplete((optionalMessageData, throwable1) ->
                                optionalMessageData.ifPresentOrElse(messageData -> resultCompletableFuture.complete(
                                        canCreateMessage(messageData.getMessageUuid())
                                ), () -> resultCompletableFuture.complete(canNotCreateMessage()))
                        ), () -> resultCompletableFuture.complete(internalError()))
                );
                return resultCompletableFuture;
            } else {
                return CompletableFuture.completedFuture(getBadAuthorizedResult());
            }
        } else if (requestMethod == RequestMethod.GET) {
            getDataRegistry().getDataSupplier(UserChatData.class).getData(UserChatDataAccess.isUserInChat(
                    loggedInUserData.getUserUuid(), data.getChatUuid()
            )).whenComplete((optionalUserChatData, throwable) ->
                optionalUserChatData.ifPresentOrElse(userChatData ->
                    getDataRegistry().getListDataSupplier(MessageData.class).getData(MessageDataAccess.getMessagesDataByChatUuid(
                            data.getChatUuid(), new Timestamp(data.getTimeStamp())
                    )).whenComplete((optionalMessageDataList, throwable1) ->
                        optionalMessageDataList.ifPresentOrElse(messageDataList -> {
                            JSONArray jsonArray = new JSONArray();
                            messageDataList.forEach(messageData -> jsonArray.put(messageData.toJson()));
                            resultCompletableFuture.complete(new Result(jsonArray.toString(), 200));
                                }, () -> resultCompletableFuture.complete(internalError()))),
                        () -> resultCompletableFuture.complete(notInChat()))
            );
        }
        return resultCompletableFuture;
    }

    private Result notInChat() {
        return new Result("not in chat", 403);
    }

    private Result canCreateMessage(UUID messageUuid) {
        return new Result(new JSONObject().put("messageUuid", messageUuid.toString()).toString(), 201);
    }

    private Result canNotCreateMessage() {
        return new Result("Can not create Message", 502);
    }

    @Override
    public String getSubUrl() {
        return "/message";
    }

    @Override
    public List<RequestMethod> getRequestMethods() {
        return List.of(RequestMethod.GET, RequestMethod.POST);
    }

    @Override
    public boolean checkFormat(JSONObject jsonObject, RequestMethod requestMethod) {
        return (requestMethod == RequestMethod.GET && jsonObject.has("chatUuid")
                && jsonObject.has("timeStamp"))
                ||
                (requestMethod == RequestMethod.POST && jsonObject.has("senderUuid")
                && jsonObject.has("chatUuid") && jsonObject.has("timeStamp")
                && jsonObject.has("contentType") && jsonObject.has("content")
                && ContentType.getValueOf(jsonObject.getString("contentType")) != null);
    }

    @Override
    public MessageData parseJsonObject(JSONObject jsonObject, RequestMethod requestMethod) {
        return requestMethod == RequestMethod.GET ? new MessageData(null, null,
                UUID.fromString(jsonObject.getString("chatUuid")), jsonObject.getLong("timeStamp"),
                null, null) {}
                : requestMethod == RequestMethod.POST ? new MessageData(UUID.randomUUID(),
                UUID.fromString(jsonObject.getString("senderUuid")),
                UUID.fromString(jsonObject.getString("chatUuid")), jsonObject.getLong("timeStamp"),
                ContentType.getValueOf(jsonObject.getString("contentType")),
                jsonObject.getString("content").getBytes()) {}
                : null;
    }
}
