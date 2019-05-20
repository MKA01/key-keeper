package utils;

import org.apache.log4j.Logger;

public class KeyKeeperException extends RuntimeException {

    private final static Logger logger = Logger.getLogger(KeyKeeperException.class);

    public KeyKeeperException() {
    }

    public KeyKeeperException(String message) {
        super(message);
        logger.error(message);
    }

    public KeyKeeperException(String message, Throwable cause) {
        super(message, cause);
        logger.error(message + "\n" + cause);
    }

    public KeyKeeperException(Throwable cause) {
        super(cause);
        logger.error(cause);
    }

    public KeyKeeperException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        logger.error(message + "\n" + cause);
    }
}
