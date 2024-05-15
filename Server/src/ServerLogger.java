import java.io.IOException;
import java.util.logging.*;

public class ServerLogger {
    private static final Logger logger = Logger.getLogger(ServerLogger.class.getName());
    private static final String LOG_FILE = "server.log";
    private static ServerLogger instance = null;

    private ServerLogger() {
        try {
            // Set logging level
            logger.setLevel(Level.INFO);

            // Create file handler and set its level
            FileHandler fileHandler = new FileHandler(LOG_FILE, false); // append mode
            fileHandler.setLevel(Level.INFO);

            // Add the file handler to the logger
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to initialize logger", e);
        }
    }

    public static synchronized ServerLogger getInstance() {
        if (instance == null) {
            instance = new ServerLogger();
        }
        return instance;
    }

    public void logInfo(String message) {
        logger.info(message);
    }

    public void logWarning(String message) {
        logger.warning(message);
    }

    public void logError(String message, Throwable throwable) {
        logger.log(Level.SEVERE, message, throwable);
    }
}
