package software.mayr.smserver.web.httphandler;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONObject;
import software.mayr.smserver.data.Data;
import software.mayr.smserver.database.DataRegistry;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface Request<T extends Data> {

    String getSubUrl();

    DataRegistry getDataRegistry();

    List<RequestMethod> getRequestMethods();

    default CompletableFuture<Result> handleFromJsonObject(RequestMethod requestMethod, JSONObject jsonObject) {
        return handle(requestMethod, parseJsonObject(jsonObject, requestMethod));
    }

    default CompletableFuture<Result> handle(RequestMethod requestMethod, T data, HttpExchange httpExchange) {
        return handle(requestMethod, data);
    }

    CompletableFuture<Result> handle(RequestMethod requestMethod, T data);

    boolean checkFormat(JSONObject jsonObject, RequestMethod requestMethod);

    T parseJsonObject(JSONObject jsonObject, RequestMethod requestMethod);


    default Result internalError() {
        return new Result("internal error", 500);
    }


}
