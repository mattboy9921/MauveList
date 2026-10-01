package net.mattlabs.mauvelist.common.config;

import io.leangen.geantyref.TypeToken;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

public class ConfigTools {

    public static <T> T initializeConfig(
            Path dataFolder,
            ConfigurateManager configurateManager,
            String configFileName,
            Class<T> configClass,
            Supplier<T> defaultSupplier) {
        // Data Folder
        try {
            Files.createDirectories(dataFolder);
        }
        catch (IOException e) {
            throw new IllegalStateException("Cannot create directory: " + dataFolder);
        }

        // Add config
        T defaultConfig = defaultSupplier.get();
        configurateManager.add(configFileName, TypeToken.get(configClass), defaultConfig, defaultSupplier, ConfigurateFormat.HOCON);

        // Try to save default values
        if (!configurateManager.saveDefaults(configFileName)) throw new IllegalStateException("Failed to save default config");

        // Load config
        configurateManager.load(configFileName);

        // Save/update config
        configurateManager.save(configFileName);

        return configurateManager.get(configFileName);
    }
}
