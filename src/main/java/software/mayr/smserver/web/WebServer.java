package software.mayr.smserver.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;
import software.mayr.smserver.database.DataRegistry;
import software.mayr.smserver.util.SystemShutdownable;
import software.mayr.smserver.web.httphandler.*;
import software.mayr.smserver.web.httphandler.request.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

public record WebServer(HttpServer httpServer) implements HttpHandler, SystemShutdownable {

    public WebServer(int port) throws IOException {
        this (HttpServer.create(new InetSocketAddress(port), 0));
    }

    public void registerHandlers(DataRegistry dataRegistry) {
        registerRequest(new CreateUserRequest(){
            @Override
            public DataRegistry getDataRegistry() {
                return dataRegistry;
            }
        });
        registerRequest(new MessageRequest() {
            @Override
            public DataRegistry getDataRegistry() {
                return dataRegistry;
            }
        });
        registerRequest(new ChatActionRequest() {
            @Override
            public DataRegistry getDataRegistry() {
                return dataRegistry;
            }
        });
    }

    @Override
    public void registerRequest(Request<?> handler) {
            httpServer().createContext(handler.getSubUrl(), exchange -> {
                RequestMethod requestMethod = RequestMethod.getValueOf(exchange.getRequestMethod());
                if (requestMethod == null || !handler.getRequestMethods().contains(requestMethod))
                    return;
                try {
                    JSONObject jsonObject = new JSONObject(new String(exchange.getRequestBody().readAllBytes()));
                    if (handler.checkFormat(jsonObject, requestMethod)) {
                        try {
                            CompletableFuture<Result> completableFuture =
                                    handler instanceof AuthenticatedRequest<?> authenticatedRequest ?
                                            authenticatedRequest.handleFromJsonObject(requestMethod, jsonObject, exchange)
                                            : handler.handleFromJsonObject(requestMethod, jsonObject);
                            completableFuture.whenComplete((result, throwable) ->
                                    writeResponseAndClose(exchange, result)
                            );
                            return;
                        } catch (Exception ignored) {
                        }
                    }
                } catch (Exception ignored) {
                }
                writeResponseAndClose(exchange, new Result("Invalid Format", 400));
            });
    }

    private void writeResponseAndClose(HttpExchange httpExchange, Result result) {
        try {
            byte[] response = result.body().getBytes();
            httpExchange.sendResponseHeaders(result.code(), response.length);
            OutputStream responseBodyOutputStream = httpExchange.getResponseBody();
            responseBodyOutputStream.write(response);
            responseBodyOutputStream.flush();
            responseBodyOutputStream.close();
            httpExchange.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void start() {
        httpServer().start();
    }

    @Override
    public void shutdown() {
        httpServer().stop(3);
    }
}
