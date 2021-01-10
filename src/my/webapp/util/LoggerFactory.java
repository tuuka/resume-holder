package my.webapp.util;

import java.util.Date;
import java.util.logging.*;

public class LoggerFactory {
    private static final Logger LOGGER;

    private static final SimpleFormatter sf = new SimpleFormatter() {
//        private static final String format = "[%1$tF %1$tT] [%2$-7s] %3$s %n";
        private static final String format =
            "[%1$td/%1$tm/%1tY %1$tT] [%3$-7s] %4$s (%2$s)%n";

//    static {
//        //https://www.logicbig.com/tutorials/core-java-tutorial/logging/customizing-default-format.html
//        System.setProperty("java.util.logging.SimpleFormatter.format",
//                "[%1$tF %1$tT] [%4$-7s] %5$s %n");
//        LOGGER = Logger.getLogger(ArrayStorage.class.getName());
//    }

        @Override
//        public synchronized String format(LogRecord record) {
//            return String.format(format,
//                    new Date(record.getMillis()),
//                    record.getLevel().getLocalizedName(),
//                    record.getMessage()
//            );
//        }
        public synchronized String format(LogRecord record) {
            String source;
            if (record.getSourceClassName() != null) {
                source = record.getSourceClassName();
                if (record.getSourceMethodName() != null) {
                    source += ":" + record.getSourceMethodName();
                }
            } else {
                source = record.getLoggerName();
            }
//            String message = formatMessage(record);
            String message = record.getMessage();
//            String throwable = "";
//            if (record.getThrown() != null) {
//                StringWriter sw = new StringWriter();
//                PrintWriter pw = new PrintWriter(sw);
//                pw.println();
//                int size = record.getThrown().getStackTrace().length;
//                pw.print(record.getThrown().getStackTrace()[size-1]);
////                record.getThrown().printStackTrace(pw);
//                pw.close();
//                throwable = sw.toString();
//            }
            return String.format(format,
                    new Date(record.getMillis()),
                    source,
                    record.getLevel().getLocalizedName(),
                    message
//                    ,throwable
            );
        }
    };

    static {
        LOGGER = Logger.getLogger("my.webapp");
        LOGGER.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(sf);
        LOGGER.addHandler(handler);
        LOGGER.setLevel(Level.INFO);
        // Adding log to file
//        FileHandler fileHandler;
//        try {
//            fileHandler = new FileHandler("D:\\temp\\storage.log", true);
//            fileHandler.setFormatter(sf);
//            mainLogger.addHandler(fileHandler);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public static Logger getLogger(){
        return LOGGER;
    }

    public static Logger getLogger(Class<?> cl){
        Logger l = Logger.getLogger(cl.getName());
        l.setLevel(Level.WARNING);
        return l;
    }


}
