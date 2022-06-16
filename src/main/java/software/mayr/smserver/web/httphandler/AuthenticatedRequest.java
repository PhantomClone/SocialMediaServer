package software.mayr.smserver.web.httphandler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.json.JSONObject;
import software.mayr.smserver.data.Data;
import software.mayr.smserver.data.userdata.UserData;
import software.mayr.smserver.data.userdata.UserDataAccess;
import software.mayr.smserver.web.httphandler.Request;
import software.mayr.smserver.web.httphandler.RequestMethod;
import software.mayr.smserver.web.httphandler.Result;

import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * @author PhantomClone
 */
public abstract class AuthenticatedRequest<T extends Data> implements Request<T> {

    public CompletableFuture<Result> handleFromJsonObject(RequestMethod requestMethod, JSONObject jsonObject, HttpExchange httpExchange) {
        return handle(requestMethod, parseJsonObject(jsonObject, requestMethod), httpExchange);
    }

    @Override
    public CompletableFuture<Result> handle(RequestMethod requestMethod, T data, HttpExchange httpExchange) {
        CompletableFuture<Result> completableFuture = new CompletableFuture<>();
        authentication(httpExchange).whenComplete((optionalUserData, throwable) ->
            optionalUserData.ifPresentOrElse(userData ->
                handle(requestMethod, data, userData).whenComplete((result, throwable1) ->
                    completableFuture.complete(result)
                ), () -> completableFuture.complete(new Result("Unauthorized", 401)))
        );
        return completableFuture;
    }

    @Override
    public CompletableFuture<Result> handle(RequestMethod requestMethod, T data) {
        throw new UnsupportedOperationException();
    }

    private Result badLogin() {
        return new Result("Bad Login", 401);
    }

    public abstract CompletableFuture<Result> handle(RequestMethod requestMethod, T data, UserData loggedInUserData);

    public Result getBadAuthorizedResult() {
        return new Result("BadAuthorizedResult", 402);
    }

    private CompletableFuture<Optional<UserData>> authentication(HttpExchange httpExchange) {
        Headers requestHeaders = httpExchange.getRequestHeaders();
        String auth = requestHeaders.getFirst ("Authorization");
        if (auth == null) {
            return CompletableFuture.completedFuture(Optional.empty());
        }
        byte[] b = Base64.getDecoder().decode(auth);
        String authString = new String(b);
        int colon = authString.indexOf (':');
        if (colon != -1) {
            String username = authString.substring (0, colon);
            String password = authString.substring (colon+1);
            return getDataRegistry().getDataSupplier(UserData.class).getData(UserDataAccess.getUserNameDataAccess(username, password));
        } else {
            //TODO implement SessionHandler
            return CompletableFuture.completedFuture(Optional.empty());
        }
    }

}
