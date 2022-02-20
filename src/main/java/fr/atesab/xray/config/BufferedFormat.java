package fr.atesab.xray.config;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;

public class BufferedFormat {
    @FunctionalInterface
    public interface BufferedComponent {
        /**
         * create a simple formatted string BufferedComponent
         * 
         * @param text the text
         * @return the BufferedComponent
         */
        static BufferedComponent stringComponent(String text) {
            String formatted = text.replaceAll("&([0-9a-fk-or])", ChatFormatting.PREFIX_CODE + "$1");
            return mc -> formatted;
        }

        /**
         * compute the text
         * 
         * @param mc minecraft
         * @return the computed text
         */
        String apply(Minecraft mc);

        /**
         * @return the last computed value, should be overide if
         *         {@link #apply(Minecraft)} requires mc
         */
        default String getLastComputedValue() {
            return apply(null);
        }
    }

    @FunctionalInterface
    public interface LineWriter {
        void writeLine(int index, String text);
    }

    private List<List<BufferedComponent>> components = new ArrayList<>();

    /**
     * sync the format buffer
     * 
     * @param format the format text
     */
    public void sync(List<String> format) {
        components.clear();

        format.forEach(line -> components.add(getLine(line)));
    }

    /**
     * get buffer line
     * 
     * @param line line to bufferise
     * @return buffered line
     */
    public List<BufferedComponent> getLine(String line) {
        List<BufferedComponent> list = new ArrayList<>();

        Matcher m = LocationFormatTool.OBJ_PATTERN.matcher(line);

        int end = 0;

        // find tool usage
        while (m.find()) {
            int matchStart = m.start();

            // previous text to cat?
            if (matchStart > end) {
                list.add(BufferedComponent.stringComponent(line.substring(end, matchStart)));
            }

            String objName = m.group(1);

            // find the tool
            LocationFormatTool tool = LocationFormatTool.getByName(objName);

            // if the tool doesn't exists, set the text, otherwise set the tool
            if (tool == null) {
                list.add(BufferedComponent.stringComponent(m.group()));
            } else {
                list.add(tool);
            }

            end = m.end();
        }

        // add end text if required
        if (line.length() > end) {
            list.add(BufferedComponent.stringComponent(line.substring(end, line.length())));
        }

        return list;
    }

    /**
     * compute the lines
     * 
     * @param mc mc
     * @return the lines
     */
    public void apply(Minecraft mc, LineWriter writer) {
        if (mc.level == null) {
            int line = 0;
            for (List<BufferedComponent> list : components) {
                writer.writeLine(line++, list.stream().map(
                        BufferedComponent::getLastComputedValue).collect(Collectors.joining()));
            }
        } else {
            int line = 0;
            for (List<BufferedComponent> list : components) {
                writer.writeLine(line++, list.stream().map(buff -> buff.apply(mc)).collect(Collectors.joining()));
            }
        }

    }
}
