package net.karatek.randomstuff;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggingTest {

    private static final Logger logger = LogManager.getLogger(LoggingTest.class);

    public static void main(String[] args) {

        logger.info("Info");
        logger.warn("Warning");
        logger.error("Error");
        logger.fatal("Fatal");
        logger.debug("Debug");
        // in old days, we need to check the log level to increase performance
        /*if (logger.isDebugEnabled()) {
            logger.debug("{}", getNumber());
        }*/

        // with Java 8, we can do this, no need to check the log level

    }

    static int getNumber() {
        return 5;
    }
}
