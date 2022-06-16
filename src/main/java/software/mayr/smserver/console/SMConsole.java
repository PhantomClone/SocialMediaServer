package software.mayr.smserver.console;

import software.mayr.smserver.SMServer;
import software.mayr.smserver.util.SystemShutdownable;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author PhantomClone
 */
public class SMConsole implements SystemShutdownable {

    private Thread readingThread;

    public void initConsole(SMServer smServer) {
        this.readingThread = Thread.currentThread();
        Console console = System.console();
        if (console != null)
            console.readLine("PRESS <Enter> TO STOP");
        else {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(System.in))) {
                reader.readLine();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        System.out.println("Shutting down...");
        new Thread(smServer::shutdown).start();
    }

    @Override
    public void shutdown() {
        if (this.readingThread != null && !this.readingThread.isInterrupted())
            this.readingThread.interrupt();
    }
}
