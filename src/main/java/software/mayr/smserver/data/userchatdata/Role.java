package software.mayr.smserver.data.userchatdata;

/**
 * @author PhantomClone
 */
public enum Role {

    MEMBER, MODERATOR, ADMIN;

    public static Role getValueOf(String stringRole) {
        stringRole = stringRole.toUpperCase();
        for (Role role : values()) {
            if (role.name().equals(stringRole))
                return role;
        }
        return null;
    }
}
