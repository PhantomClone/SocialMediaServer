package software.mayr.smserver;

import software.mayr.smserver.console.SMConsole;
import software.mayr.smserver.database.DataRegistry;
import software.mayr.smserver.database.mysql.MySqlDataRegistry;
import software.mayr.smserver.network.NotificationSocketServer;
import software.mayr.smserver.web.WebServer;
import software.mayr.smserver.util.SystemShutdownable;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author PhantomClone
 */
public record SMServer(WebServer webServer, NotificationSocketServer notificationSocketServer, DataRegistry dataRegistry, SMConsole console, AtomicBoolean running) implements SystemShutdownable {

    public SMServer(WebServer webServer, NotificationSocketServer notificationSocketServer) {
        this(webServer, notificationSocketServer,
                MySqlDataRegistry.create(
                        "127.0.0.1",
                        "socialmedia",
                        3306,
                        "root",
                        "S0c1alM3dia!"
                        //mysqld --initialize --console
                        //jdozfciNf7=c
                ),
                new SMConsole(),
                new AtomicBoolean(false));
    }

    public void init() {
        dataRegistry().setUpDataRegistry();
        webServer().registerHandlers(dataRegistry());
    }

    public void start() {
        if (running().get())
            throw new IllegalStateException("Already started!");
        running().set(true);
        webServer.start();
        new Thread(() -> notificationSocketServer.create(running)
                .run()//Maybe need a ThreadExecutor late on
        ).start();
    }

    @Override
    public void shutdown() {
        if (!running.get())
            return;
        running.set(false);
        webServer.shutdown();
        console.shutdown();
        dataRegistry().close();
        System.out.println("Shutdown in 6s");
        new Thread(() -> {
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.exit(1);
        });
    }
}
