package fr.atesab.xray.utils;

import net.minecraft.network.chat.Component;

public class XrayUtils {
    @FunctionalInterface
    public interface SoWhat<T> {
        T run() throws Exception;
    }

    private XrayUtils() {
    }

    public static boolean isHover(double mouseX, double mouseY, int x, int y, int x2, int y2) {
        return mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2;
    }

    /**
     * yep, it can cause an Exception, so what?
     * 
     * @param <T>    the return type
     * @param action the action with the exception
     * @return the return of the action
     */
    public static <T> T soWhat(SoWhat<T> action) {
        try {
            return action.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Component getToggleable(boolean value, String title) {
        return Component.translatable(title).append(" (")
                .append(Component.translatable(value ? "x13.mod.on" : "x13.mod.off")).append(")");
    }
}
