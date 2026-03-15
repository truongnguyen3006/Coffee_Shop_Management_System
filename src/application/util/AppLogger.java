package application.util;


import application.*;
import application.model.*;
import application.repository.*;
import application.service.*;
import application.messaging.*;
import application.util.*;
import application.config.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class AppLogger {
    private AppLogger() {
    }

    public static Logger getLogger(Class<?> type) {
        return Logger.getLogger(type.getName());
    }

    public static void error(Logger logger, String message, Throwable throwable) {
        if (throwable == null) {
            logger.severe(message);
            return;
        }
        logger.log(Level.SEVERE, message + System.lineSeparator() + getStackTrace(throwable));
    }

    private static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }
}
