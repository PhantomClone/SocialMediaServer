package software.mayr.smserver.network;

import software.mayr.smserver.data.userdata.UserData;

import java.util.UUID;

/**
 * @author PhantomClone
 */
public interface SessionHandler {

    UserData getUserData(UUID session);
    UUID generateSession(String userName, String password);

}
