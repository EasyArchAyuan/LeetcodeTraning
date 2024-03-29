package design.chain_of_responsibility;

/**
 * @author Ayuan
 */
public abstract class AbstractLogger {

    public static int INFO = 1;
    public static int DEBUG = 2;
    public static int ERROR = 3;

    protected int level;

    /**
     * 责任链的下一个元素
     */
    protected AbstractLogger nextLogger;

    protected abstract void write(String message);

    public void setNextLogger(AbstractLogger nextLogger) {
        this.nextLogger = nextLogger;
    }

    public void logMessage(int level, String message) {
        if (this.level <= level) {
            write(message);
        }

        //递归，不断调用下一级的logMessage
        if (nextLogger != null) {
            nextLogger.logMessage(level, message);
        }
    }

}
