package fr.atesab.xray.config;

import fr.atesab.xray.XrayMain;
import fr.atesab.xray.color.EnumElement;
import fr.atesab.xray.utils.LocationUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocationFormatTool implements EnumElement {
    private static final String ID_PATTERN = "([A-Za-z0-9_]+)";
    private static final Pattern ID_PATTERN_MATCHER = Pattern.compile("%" + ID_PATTERN);
    private static final Map<String, LocationFormatTool> TOOLS = new HashMap<>();

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
    public static final LocationFormatTool FPS = register("x13.mod.location.opt.fps", Items.ITEM_FRAME, "fps", (mc, player, world) -> mc.fpsDebugString);
    public static final LocationFormatTool BIOME = register("x13.mod.location.opt.biome", Items.OAK_LOG, "bio",
    		(mc, player, world) -> world.getBiome(player.getBlockPos()).getKey().map(registry -> registry.getValue().toString()).orElse("???"));
    public static final LocationFormatTool PLAYER_CHUNK_X = register("x13.mod.location.opt.chunkX", Items.BOOK, "cx",
    		(mc, player, world) -> String.valueOf(player.getChunkPos().x));
    public static final LocationFormatTool PLAYER_CHUNK_Z = register("x13.mod.location.opt.chunkZ", Items.BOOK, "cz",
    		(mc, player, world) -> String.valueOf(player.getChunkPos().z));
    public static final LocationFormatTool BLOCK_LIGHT = register("x13.mod.location.opt.blockLight", Items.TORCH, "blocklight",
    		(mc, player, world) -> String.valueOf(world.getLightLevel(LightType.BLOCK,player.getBlockPos().add(1,0,0))));
    public static final LocationFormatTool SKY_LIGHT = register("x13.mod.location.opt.skyLight", Items.ELYTRA, "skylight",
    		(mc, player, world) -> String.valueOf(world.getLightLevel(LightType.SKY,player.getBlockPos())));
    public static final LocationFormatTool LOOKING_BLOCK_LIGHT = register("x13.mod.location.opt.lookingBlockLight", Items.REDSTONE_TORCH, "lookinglight",
    		(mc, player, world) -> String.valueOf(world.getLightLevel(LightType.BLOCK,LocationUtils.getLookingFaceBlockPos(mc, player))));
    public static final LocationFormatTool LOOKINGBLOCK = register("x13.mod.location.opt.lookingBlock", Items.DIAMOND_ORE, "lookingblock",
    		(mc, player, world) -> Registry.BLOCK.getKey(world.getBlockState(LocationUtils.getLookingBlockPos(mc)).getBlock()).map(k -> k.getRegistry().toString()).orElse("???"));
    public static final LocationFormatTool LOOKINGBLOCK_TRANSLATE = register("x13.mod.location.opt.lookingTranslate", Items.DIAMOND_ORE, "lookingtranslate",
    		(mc, player, world) -> I18n.translate(world.getBlockState(LocationUtils.getLookingBlockPos(mc))
                    .getBlock().getTranslationKey()));
    public static final LocationFormatTool FACING = register("x13.mod.location.opt.facing", Items.COMPASS, "face",
    		(mc, player, world) -> player.getHorizontalFacing().getName());
    public static final LocationFormatTool DAYS_COUNT = register("x13.mod.location.opt.daysCount", Items.CLOCK, "d",
    		(mc, player, world) -> String.valueOf((world.getTime() / 24000)));
    public static final LocationFormatTool TIME_OF_DAY = register("x13.mod.location.opt.timeOfDay", Items.CLOCK, "timeday",
    		(mc, player, world) -> String.valueOf((world.getTime() % 24000) / 24000.0));
    public static final LocationFormatTool  TIME_HOURS_PADDING = register("x13.mod.location.opt.hoursPadding", Items.CLOCK, "hh",
    		(mc, player, world) -> LocationUtils.getTwoDigitNumberFormat().format(((world.getTime() + 6000) % 24000) / 1000));
    public static final LocationFormatTool TIME_HOURS = register("x13.mod.location.opt.hours", Items.CLOCK, "h",
    		(mc, player, world) -> String.valueOf(((world.getTime() + 6000) % 24000) / 1000));
    public static final LocationFormatTool TIME_MINUTES_PADDING = register("x13.mod.location.opt.minutesPadding", Items.CLOCK, "mm",
    		(mc, player, world) -> LocationUtils.getTwoDigitNumberFormat().format(((world.getTime() % 1000) / 1000.0 * 60)));
    public static final LocationFormatTool TIME_SECONDS_PADDING = register("x13.mod.location.opt.secondsPadding", Items.CLOCK, "ss",
    		(mc, player, world) -> LocationUtils.getTwoDigitNumberFormat().format(((world.getTime() % 1000) / 1000.0 * 3600) % 60));
    public static final LocationFormatTool IS_SLIME = register("x13.mod.location.opt.isSlime", Items.SLIME_BALL, "slime",
    		(mc, player, world) -> String.valueOf(LocationUtils.isSlimeChunk(mc, player.getChunkPos())));
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
                    tool.add(new StringToolFunction(format.substring(end - idStart, end)));
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

    public static String applyColor(String text) {
        return text.replaceAll("&([0-9a-fk-or])", "§$1");
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
}
