package fr.atesab.xray.config;

import fr.atesab.xray.XrayMain;
import fr.atesab.xray.color.EnumElement;
import fr.atesab.xray.utils.GuiUtils;
import fr.atesab.xray.utils.LocationUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class LocationFormatTool implements EnumElement {
    private static final String ID_PATTERN = "([A-Za-z0-9_]+)";
    // format:
    // base color &6
    // custom color &!RR,GG,BB!
    // random color &??
    // random color with frequency &?FREQUENCY?
    // random color with frequency, shift &?FREQUENCY,SHIFT_PERCENTAGE?
    private static final Pattern COLOR_PATTERN = Pattern.compile("&([\\da-fk-orA-FK-OR]|(![\\dA-F]{0,2},[\\dA-F]{0,2},[\\dA-F]{0,2}!)|([?](\\d*(,\\d*)?)[?]))");
    private static final int DEFAULT_FREQUENCY = 3000;
    private static final Pattern ID_PATTERN_MATCHER = Pattern.compile("%" + ID_PATTERN);
    private static final Map<String, LocationFormatTool> TOOLS = new HashMap<>();

    public static long currentDayTime;
    public static double currentTimeOfDay;
    public static long currentDays;
    public static long currentHours;
    public static long currentMinutes;
    public static long currentSeconds;

    public static final ToolFunction EMPTY_FUNCTION = (mc, player, world) -> "";
    public static final LocationFormatTool PLAYER_LOCATION_X = register("x13.mod.location.opt.x", Items.BOOK, "x",
            (mc, player, world) -> XrayMain.significantNumbers(player.getPos().x));
    public static final LocationFormatTool PLAYER_LOCATION_Y = register("x13.mod.location.opt.y", Items.BOOK, "y",
            (mc, player, world) -> XrayMain.significantNumbers(player.getPos().y));
    public static final LocationFormatTool PLAYER_LOCATION_Z = register("x13.mod.location.opt.z", Items.BOOK, "z",
            (mc, player, world) -> XrayMain.significantNumbers(player.getPos().z));
    public static final LocationFormatTool PLAYER_LOCATION_FLOOR_X = register("x13.mod.location.opt.fx", Items.BOOK, "fx",
            (mc, player, world) -> String.valueOf((int) player.getPos().x));
    public static final LocationFormatTool PLAYER_LOCATION_FLOOR_Y = register("x13.mod.location.opt.fy", Items.BOOK, "fy",
            (mc, player, world) -> String.valueOf((int) player.getPos().y));
    public static final LocationFormatTool PLAYER_LOCATION_FLOOR_Z = register("x13.mod.location.opt.fz", Items.BOOK, "fz",
            (mc, player, world) -> String.valueOf((int) player.getPos().z));
    public static final LocationFormatTool PLAYER_NAME = register("x13.mod.location.opt.name", Items.NAME_TAG, "name", (mc, player, world) -> player.getGameProfile().getName());
    public static final LocationFormatTool FPS = register("x13.mod.location.opt.fps", Items.ITEM_FRAME, "fps", (mc, player, world) -> ""+mc.getCurrentFps());
    public static final LocationFormatTool FPS_DEBUG = register("x13.mod.location.opt.fps.debug", Items.ITEM_FRAME, "dfps", (mc, player, world) -> mc.fpsDebugString);
    public static final LocationFormatTool BIOME = register("x13.mod.location.opt.biome", Items.OAK_LOG, "bio",
    		(mc, player, world) -> world.getBiome(player.getBlockPos()).getKey().map(registry -> registry.getValue().getPath()).orElse("???"));
    public static final LocationFormatTool BIOME_TRANSLATE = register("x13.mod.location.opt.biomeTranslate", Items.STRIPPED_OAK_LOG, "biotranslate",
            (mc, player, world) -> world.getBiome(player.getBlockPos()).getKey().map(registry -> I18n.translate(Util.createTranslationKey("biome",registry.getValue()))).orElse("???"));
    public static final LocationFormatTool PLAYER_CHUNK_X = register("x13.mod.location.opt.chunkX", Items.BOOK, "cx",
    		(mc, player, world) -> String.valueOf(player.getChunkPos().x));
    public static final LocationFormatTool PLAYER_CHUNK_Z = register("x13.mod.location.opt.chunkZ", Items.BOOK, "cz",
    		(mc, player, world) -> String.valueOf(player.getChunkPos().z));
    public static final LocationFormatTool BLOCK_LIGHT = register("x13.mod.location.opt.blockLight", Items.TORCH, "blocklight",
    		(mc, player, world) -> String.valueOf(world.getLightLevel(LightType.BLOCK,player.getBlockPos().add(0.5, 0.5, 0.5))));
    public static final LocationFormatTool SKY_LIGHT = register("x13.mod.location.opt.skyLight", Items.ELYTRA, "skylight",
    		(mc, player, world) -> String.valueOf(world.getLightLevel(LightType.SKY,player.getBlockPos().add(0.5, 0.5, 0.5))));
    public static final LocationFormatTool LOOKING_BLOCK_LIGHT = register("x13.mod.location.opt.lookingBlockLight", Items.REDSTONE_TORCH, "lookinglight",
    		(mc, player, world) -> String.valueOf(world.getLightLevel(LightType.BLOCK,LocationUtils.getLookingFaceBlockPos(mc, player))));
    public static final LocationFormatTool LOOKINGBLOCK = register("x13.mod.location.opt.lookingBlock", Items.DIAMOND_ORE, "lookingblock",
    		(mc, player, world) -> Registries.BLOCK.getKey(world.getBlockState(LocationUtils.getLookingBlockPos(mc)).getBlock()).map(b -> b.getValue().getPath()).orElse("???"));
    public static final LocationFormatTool LOOKINGBLOCK_TRANSLATE = register("x13.mod.location.opt.lookingTranslate", Items.DIAMOND_ORE, "lookingtranslate",
    		(mc, player, world) -> I18n.translate(world.getBlockState(LocationUtils.getLookingBlockPos(mc))
                    .getBlock().getTranslationKey()));
    public static final LocationFormatTool FACING = register("x13.mod.location.opt.facing", Items.COMPASS, "face",
    		(mc, player, world) -> player.getHorizontalFacing().getName());
    public static final LocationFormatTool DAYS_COUNT = register("x13.mod.location.opt.daysCount", Items.CLOCK, "d",
    		(mc, player, world) -> String.valueOf(currentDays));
    public static final LocationFormatTool TIME_OF_DAY = register("x13.mod.location.opt.timeOfDay", Items.CLOCK, "timeday",
    		(mc, player, world) -> String.valueOf(currentTimeOfDay));
    public static final LocationFormatTool  TIME_HOURS_PADDING = register("x13.mod.location.opt.hoursPadding", Items.CLOCK, "hh",
    		(mc, player, world) -> LocationUtils.getTwoDigitNumberFormat().format(currentHours));
    public static final LocationFormatTool TIME_HOURS = register("x13.mod.location.opt.hours", Items.CLOCK, "h",
    		(mc, player, world) -> String.valueOf(currentHours));
    public static final LocationFormatTool TIME_MINUTES_PADDING = register("x13.mod.location.opt.minutesPadding", Items.CLOCK, "mm",
    		(mc, player, world) -> LocationUtils.getTwoDigitNumberFormat().format(currentMinutes));
    public static final LocationFormatTool TIME_SECONDS_PADDING = register("x13.mod.location.opt.secondsPadding", Items.CLOCK, "ss",
    		(mc, player, world) -> LocationUtils.getTwoDigitNumberFormat().format(currentSeconds));
    public static final LocationFormatTool IS_SLIME = register("x13.mod.location.opt.isSlime", Items.SLIME_BALL, "slime",
    		(mc, player, world) -> LocationUtils.isSlimeChunk(mc, player.getChunkPos()));
    public static final LocationFormatTool NEW_LINE = register("x13.mod.location.opt.lineFeed",Items.WRITABLE_BOOK, "lf",
    		(mc, player, world) -> "\n");

    public static Collection<LocationFormatTool> values() {
        return Collections.unmodifiableCollection(TOOLS.values());
    }

    /**
     * construct a tool function to produce location data from a format
     * @param format the format
     * @return the tool function
     */
    public static ToolFunction construct(String format) {
        ListToolFunction tool = new ListToolFunction();
        int location = 0;

        Matcher m = ID_PATTERN_MATCHER.matcher(format);

        while (m.find()) {
            String id = m.group(1);
            int start = m.start();
            int end = m.end();

            if (start != location) {
                // add previous text
                tool.add(new StringToolFunction(format.substring(location, start)));
            }

            // search best matching element "%xabc" = "%x" + "abc"
            int idStart = id.length();
            while (idStart > 0) {
                LocationFormatTool found = TOOLS.get(id.substring(0, idStart));

                if (found != null) {
                    tool.add(found.getAction());
                    break;
                }

                idStart--;
            }


            if (idStart != id.length()) {
                // not everything was consumed while reading
                if (idStart == 0) {
                    // nothing was found, add all the match
                    tool.add(new StringToolFunction(format.substring(start, end)));
                } else {
                    // add only the suffix
                    tool.add(new StringToolFunction(format.substring(start + idStart + 1, end)));
                }
            }

            // set new start
            location = end;
        }

        if (location < format.length()) {
            tool.add(new StringToolFunction(format.substring(location)));
        }

        return tool.clearValue();
    }
    public static LocationFormatTool register(String translation, ItemConvertible icon, String txt, ToolFunction action) {
        LocationFormatTool tool = new LocationFormatTool(translation, icon, txt, action);
        TOOLS.put(tool.getID(), tool);
        return tool;
    }

    private final String regex;
    private final ToolFunction action;
    private final ItemStack icon;
    private final Text title;

    private LocationFormatTool(String translation, ItemConvertible icon, String txt, ToolFunction action) {
        if (ID_PATTERN.matches(txt)) {
            throw new IllegalArgumentException("id should match the format " + ID_PATTERN);
        }
        this.regex = txt;
        this.action = action;
        this.icon = new ItemStack(icon);
        this.title = Text.translatable(translation);
    }

    public String getOption() {
        return "%" + regex;
    }

    public String getID() {
        return regex;
    }

    public ToolFunction getAction() {
        return action;
    }

    public String apply(String old, MinecraftClient mc) {
        ClientPlayerEntity player = mc.player;
        ClientWorld world = mc.world;
        if (player == null || world == null) {
            return "";
        }
        return old.replaceAll(regex, action.apply(mc, player, world));
    }

    @Override
    public ItemStack getIcon() {
        return icon;
    }

    @Override
    public Text getTitle() {
        return title;
    }


    public interface ToolFunction {
        String apply(MinecraftClient client, ClientPlayerEntity player, World world);
    }

    public static class StringToolFunction implements ToolFunction {
        private final String text;

        public StringToolFunction(String text) {
            this.text = text;
        }

        @Override
        public String apply(MinecraftClient client, ClientPlayerEntity player, World world) {
            return text;
        }
    }

    private static Function<Style, Style> toColorApplier(Function<Style, Style> prev, String colorBlock) {
        if (colorBlock.length() == 0) {
            return s -> s.withColor(Formatting.RESET);
        }

        char start = colorBlock.charAt(0);
        return switch (start) {
            // random color with frequency &?FREQUENCY?
            // random color with frequency, shift &?FREQUENCY,SHIFT_PERCENTAGE?
            case '?' -> {
                String data = colorBlock.substring(1, colorBlock.length() -1);
                long delta;
                int frequency;
                if (!data.isEmpty()) {
                    // random color with shift &?SHIFT_PERCENTAGE?
                    String[] freDelta = data.split(",");
                    if (freDelta.length > 0) {
                        frequency = freDelta[0].isEmpty() ? DEFAULT_FREQUENCY : Integer.parseInt(freDelta[0]);
                        if (freDelta.length > 1) {
                            delta = freDelta[1].isEmpty() ? 0 : Long.parseLong(freDelta[1]) * 500;
                        } else {
                            delta = 0;
                        }
                    } else {
                        frequency = DEFAULT_FREQUENCY;
                        delta = 0;
                    }
                } else {
                    // random color &??
                    frequency = DEFAULT_FREQUENCY;
                    delta = 0;
                }
                int timeColor = GuiUtils.getTimeColor(delta, frequency > 0 ? frequency : DEFAULT_FREQUENCY, 100, 50);
                yield s -> s.withColor(timeColor);
            }
            // custom color &!RR,GG,BB!
            case '!' -> {
                String[] colors = colorBlock.substring(1, colorBlock.length() -1).split(",");
                int r, g, b;
                if (colors.length > 0) {
                    r = colors[0].isEmpty() ? 0 : Integer.parseInt(colors[0], 16);
                    if (colors.length > 1) {
                        g = colors[1].isEmpty() ? 0 : Integer.parseInt(colors[1], 16);
                        if (colors.length > 2) {
                            b = colors[2].isEmpty() ? 0 : Integer.parseInt(colors[2], 16);
                        } else {
                            b = 0;
                        }
                    } else {
                        g = 0;
                        b = 0;
                    }
                } else {
                    r = 0;
                    g = 0;
                    b = 0;
                }

                int rgba = GuiUtils.asRGBA(r, g, b, 0xFF);
                yield s -> s.withColor(rgba);
            }
            // base color &6
            default -> {
                Formatting chatFormatting = Objects.requireNonNullElse(Formatting.byCode(start), Formatting.RESET);
                if (chatFormatting == Formatting.RESET) {
                    yield s -> s;
                }
                if (chatFormatting.isColor()) {
                    yield s -> s.withColor(chatFormatting);
                }
                switch (chatFormatting) {
                    case OBFUSCATED -> {
                        yield s -> prev.apply(s).withObfuscated(true);
                    }
                    case BOLD -> {
                        yield s -> prev.apply(s).withBold(true);
                    }
                    case STRIKETHROUGH -> {
                        yield s -> prev.apply(s).withStrikethrough(true);
                    }
                    case UNDERLINE -> {
                        yield s -> prev.apply(s).withUnderline(true);
                    }
                    case ITALIC -> {
                        yield s -> prev.apply(s).withItalic(true);
                    }
                    default -> {
                        yield s -> s.withColor(chatFormatting);
                    }
                }
            }
        };
    }

    /**
     * convert a location text to lines of components
     * @param text text
     * @return components
     */
    public static Text[] applyColor(String text) {
        return Stream.of(text.split("\n")).map(l -> {
            Matcher matcher = COLOR_PATTERN.matcher(l);
            Function<Style, Style> style = (s) -> s;

            int last = 0;
            MutableText current = Text.literal("");

            while (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();

                // append previous literal if required
                if (start != last) {
                    current = current.append(Text.literal(l.substring(last, start)).styled(style::apply));
                }
                // set the new end
                last = end;

                // get the color info
                String colorInfoBlock = matcher.group(1);
                try {
                    style = toColorApplier(style, colorInfoBlock);
                } catch (Exception e) {
                    return Text.literal("error &" + colorInfoBlock + ": " + e.getMessage());
                }
            }
            // append previous literal if required
            if (last != l.length()) {
                current = current.append(Text.literal(l.substring(last)).styled(style::apply));
            }

            return current;
        }).toArray(Text[]::new);
    }

    public static class ListToolFunction implements ToolFunction {
        private final List<ToolFunction> functions;

        public ListToolFunction(List<ToolFunction> functions) {
            this.functions = functions;
        }

        public ListToolFunction() {
            this(new ArrayList<>());
        }

        @Override
        public String apply(MinecraftClient client, ClientPlayerEntity player, World world) {
            StringBuilder bld = new StringBuilder();
        	updateTimeField(client, player, world);

            for (ToolFunction func : functions) {
                bld.append(func.apply(client, player, world));
            }
            return bld.toString();
        }

        public void add(ToolFunction function) {
            functions.add(function);
        }

        /**
         * @return a clear version of this tool function if available
         */
        public ToolFunction clearValue() {
            return switch (functions.size()) {
                case 0 -> EMPTY_FUNCTION;
                case 1 -> functions.get(0);
                default -> new ListToolFunction(List.of(functions.toArray(ToolFunction[]::new)));
            };
        }
    }

    private static void updateTimeField(MinecraftClient client, ClientPlayerEntity player, World world) {
    	if (currentDayTime == world.getTimeOfDay())
    		return;
    	currentDayTime = world.getTimeOfDay();
    	long fixedDayTime = currentDayTime + 6000;
    	currentDays = Math.floorDiv(fixedDayTime, 24000);
    	long fixedTime = Math.floorMod(fixedDayTime, 24000);
    	currentTimeOfDay = (float) (fixedDayTime / 24000);
    	currentHours = Math.floorDiv(fixedTime, 1000);
    	long fixedMinutes = Math.floorMod(fixedTime, 1000); //0-999
    	currentMinutes = Math.floorDiv(fixedMinutes * 60, 1000);
    	long fixedSeconds = Math.floorMod(fixedMinutes * 60, 1000);
    	currentSeconds = Math.floorDiv(fixedSeconds * 60, 1000);
    }
}
