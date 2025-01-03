package org.e11eman.crackutilities.utilities.systems;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SuppressWarnings("DataFlowIssue")
public class ConfigSystem {
    public final File configPath;
    private final Gson gson = new Gson();
    private final Path modConfigPath = FabricLoader.getInstance().getConfigDir();
    private JsonObject config;
    public InputStream defFile = getClass().getClassLoader().getResourceAsStream("assets/crackutilities/defaultConfig.json");
    public String jsonConfigDefault;

    public ConfigSystem(File configPath) {
        try {
            jsonConfigDefault = new String(defFile.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.configPath = configPath;
        updateConfig();
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    private void createBrokenConfig() {
        File brokenConfig = new File(modConfigPath + "/CrackUtilities/broken-config.json");

        try {
            brokenConfig.createNewFile();
            FileWriter fileWriter = new FileWriter(brokenConfig);
            fileWriter.write(getConfig().toString());
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    public void fixConfig() {
        try {
            if (configPath.exists()) {
                createBrokenConfig();
            }

            Files.createDirectories(Paths.get(modConfigPath + "/CrackUtilities/"));
            FileWriter fixConfig = new FileWriter(configPath);

            configPath.delete();
            configPath.createNewFile();

            fixConfig.write(jsonConfigDefault);
            fixConfig.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateConfig() {
        try {
            Reader reader = Files.newBufferedReader(configPath.toPath());
            this.config = gson.fromJson(reader, JsonObject.class);
            reader.close();
        } catch (IOException e) {
            fixConfig();
        }
    }

    public JsonObject getConfig() {
        if (configPath.exists()) {
            try {
                return config;
            } catch (Exception e) {
                System.out.println(
                        "Error while getting config");
                e.printStackTrace();
            }
        } else {
            fixConfig();

            return null;
        }

        return null;
    }

    public JsonElement getValue(String... categoryName) {
        JsonObject foundConfig = getConfig();
        JsonElement value = null;

        try {
            for(int cat = 0; cat < categoryName.length; cat++) {
                if(cat == categoryName.length - 1) {
                    value = foundConfig.get(categoryName[cat]);
                } else {
                    foundConfig = foundConfig.getAsJsonObject(categoryName[cat]);
                }
            }

            return value;
        } catch (Exception e) {
            fixConfig();
        }

        return foundConfig;
    }
}