package fr.atesab.xray.utils;

public class XrayUtils {
    @FunctionalInterface
    public interface SoWhat<T> {
        T run() throws Exception;
    }

    private XrayUtils() {
    }

    public static <T> T soWhat(SoWhat<T> action) {
        try {
            return action.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
