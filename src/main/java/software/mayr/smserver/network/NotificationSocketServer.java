package software.mayr.smserver.network;

import software.mayr.smserver.util.Debugger;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author PhantomClone
 */
public record NotificationSocketServer(Map<Socket, Long> waitForLogin/*, UserRegistry userRegistry*/, int port) implements Debugger {

    public NotificationSocketServer(int port) {
        this (new HashMap<>()/*, new UserRegistry(new ArrayList<>())*/, port);
    }

    public Runnable create(AtomicBoolean running) {
        return () -> {
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                serverSocket.setSoTimeout(100);
                while (running.get()) {
                    try {
                        Socket socket = serverSocket.accept();
                        waitForLogin.put(socket, System.currentTimeMillis());
                    } catch (SocketTimeoutException e) {
                        checkLogin();
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        };
    }

    private void checkLogin() throws IOException {
        Iterator<Map.Entry<Socket, Long>> iterator = waitForLogin().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Socket, Long> socketEntry = iterator.next();
            if (socketEntry.getKey().isClosed() ||  System.currentTimeMillis() - socketEntry.getValue() > 2000) {
                socketEntry.getKey().close();
                iterator.remove();
                continue;
            }
            InputStream inputStream = socketEntry.getKey().getInputStream();
            if (inputStream.available() == 0)
                continue;

            byte[] bytes = inputStream.readAllBytes();
            if (bytes.length != 16) {
                socketEntry.getKey().close();
                return;
            }
            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

            long high = byteBuffer.getLong();
            long low = byteBuffer.getLong();
            UUID userUuid = new UUID(high, low);

           // userRegistry.onlineUsers().add(new User(userUuid, socketEntry.getKey()));
            iterator.remove();
        }
    }

}
