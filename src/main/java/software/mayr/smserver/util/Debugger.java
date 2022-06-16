package software.mayr.smserver.util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author PhantomClone
 */
public interface Debugger {

    default void log(String message, Level level) {
        Logger.getLogger(this.getClass().getSimpleName()).log(level, message);
    }

    default void log(String message, Level level, Exception exception) {
        Logger.getLogger(this.getClass().getSimpleName()).log(level, message, exception);
    }

}
