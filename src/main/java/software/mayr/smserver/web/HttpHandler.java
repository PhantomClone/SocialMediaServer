package software.mayr.smserver.web;

import software.mayr.smserver.web.httphandler.Request;

public interface HttpHandler {

    void registerRequest(Request<?> handler);

}
