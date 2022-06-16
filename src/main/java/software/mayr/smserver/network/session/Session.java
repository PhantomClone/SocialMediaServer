package software.mayr.smserver.network.session;

import software.mayr.smserver.data.userdata.UserData;

import java.util.UUID;

/**
 * @author PhantomClone
 */
public record Session(UserData userData, UUID session) {
}
