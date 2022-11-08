package fy.GB.cases;

import java.util.logging.Level;
import java.util.logging.Logger;

public class case1 {

    boolean a = true;
    Logger logger = Logger.getAnonymousLogger();

    private boolean analog() {
        try {
            return a;
        } catch (IllegalStateException e) {
            logger.log(Level.FINE, "123");
            return false;
        }
        catch (RuntimeException e) {
            return false;
        }
    }
}
