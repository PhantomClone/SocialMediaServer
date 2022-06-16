package software.mayr.smserver;

import org.json.JSONObject;
import software.mayr.smserver.network.NotificationSocketServer;
import software.mayr.smserver.web.WebServer;

import java.io.IOException;

/**
 * @author PhantomClone
 */
public class Main {

    public static void main(String[] args) throws IOException {
        System.setProperty("org.jline.terminal.dumb", "true");
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "error");

        WebServer webServer = new WebServer(8000);
        NotificationSocketServer notificationSocketServer = new NotificationSocketServer(8001);

        SMServer smServer = new SMServer(webServer, notificationSocketServer);
        Runtime.getRuntime().addShutdownHook(new Thread(smServer::shutdown));
        smServer.init();
        smServer.start();
        smServer.console().initConsole(smServer);
    }

}
