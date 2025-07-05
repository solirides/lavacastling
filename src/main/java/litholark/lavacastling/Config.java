package litholark.lavacastling;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Config {

    public static List<BlockConfig> blockConfigs = new ArrayList<BlockConfig>();
    public static List<List<String>> chiselBlocks = new ArrayList<>();

    public static void loadConfig() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("lavacastling.json");
        File configFile = configPath.toFile();
        Gson gson = new Gson();

        if (!configFile.exists()) {
            try (InputStream in = Config.class.getResourceAsStream("/default_config.json")) {
                 if (in == null) {
                    Lavacastling.LOGGER.error("default config file is null");
                    throw new IOException("default config file is null");
                 }
                Files.copy(in, configPath);
            } catch (IOException e) {
                Lavacastling.LOGGER.error("error loading config file: " + e.getMessage());
            }
        }

        try (FileReader reader = new FileReader(configFile)) {
            JsonObject obj = gson.fromJson(reader, JsonObject.class);
            if (obj == null) {
                throw new JsonSyntaxException("Json object is null");
            }
            JsonArray genBlocksJson = obj.getAsJsonArray("gen_blocks");
            JsonArray chiselBlocksJson = obj.getAsJsonArray("chisel_blocks");

            Type type = new TypeToken<ArrayList<BlockConfig>>() {}.getType();
            Type type2 = new TypeToken<List<List<String>>>() {}.getType();
            blockConfigs = gson.fromJson(genBlocksJson, type);
            chiselBlocks = gson.fromJson(chiselBlocksJson, type2);

//            for (int i = 0; i < blocks.size(); i++) {
//                JsonObject block = blocks.get(i).getAsJsonObject();
//
//                String mode = block.has("mode") ? block.get("mode").getAsString() : "solid"; // Default to solid
//                String id = block.has("id") ? block.get("id").getAsString() : null;
//                String id2 = block.has("id2") ? block.get("id2").getAsString() : null; // Optional second block
//                int minY = block.has("minY") ? block.get("minY").getAsInt() : null;
//                int maxY = block.has("maxY") ? block.get("maxY").getAsInt() : minY; // If maxY is not set, use minY
//
//                BlockConfig blockConfig = new BlockConfig(mode, id, id2, minY, maxY);
////                Lavacast.LOGGER.info(mode);
////                Lavacast.LOGGER.info(id);
//
//                blockConfigs.add(blockConfig);
//            }

        } catch (IOException e) {
            Lavacastling.LOGGER.error("error loading config file: " + e.getMessage());
        } catch (JsonSyntaxException e) {
            Lavacastling.LOGGER.error("error parsing config file: " + e.getMessage());
        }
    }

    public static String getBlockAtY(int y) {
        for (BlockConfig config : blockConfigs) {
//            Lavacast.LOGGER.info("block config");
            if (y >= config.minY && y <= config.maxY) {
                Random random = new Random();
//                Lavacast.LOGGER.info(config.id);
                if (config.mode != null)
                {
                    if (config.mode.equals("blend") && config.id2 != null) {
                        if (random.nextFloat() < (float)(y - config.minY)/(config.maxY - config.minY) ) {
                            return config.id2;
                        }
                    }
                }
                return config.id;
            }
        }
        return "minecraft:cobblestone"; // if no config matches
    }


    public record BlockConfig(String mode, String id, String id2, int minY, int maxY) {

    }


}
