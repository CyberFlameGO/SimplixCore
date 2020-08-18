package dev.simplix.core.minecraft.bungeecord.plugin.libloader;

import dev.simplix.core.minecraft.bungeecord.plugin.SimplixPlugin;
import lombok.extern.slf4j.Slf4j;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.PluginDescription;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

@Slf4j
public class PluginClassLoaderFabricator implements Function<File, ClassLoader> {

    private void unfinalize(Field loadersField) throws NoSuchFieldException, IllegalAccessException {
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.set(loadersField, loadersField.getModifiers() & ~Modifier.FINAL);
    }

    @Override
    public ClassLoader apply(File file) {
        try {
            Class<?> classLoaderClass = Class.forName("net.md_5.bungee.api.plugin.PluginClassLoader");
            Constructor<?> constructor = classLoaderClass.getDeclaredConstructor(
                    ProxyServer.class,
                    PluginDescription.class,
                    URL[].class);
            constructor.setAccessible(true);

            SimplixPlugin plugin = (SimplixPlugin) ProxyServer.getInstance().getPluginManager().getPlugin("SimplixCore");
            PluginDescription pluginDescription = new PluginDescription(
                    "Dummy",
                    "Dummy",
                    "1.0",
                    "SimplixSoftworks",
                    Collections.emptySet(),
                    Collections.emptySet(),
                    null,
                    ""
            );
            Object loader = constructor.newInstance(
                    ProxyServer.getInstance(),
                    pluginDescription,
                    new URL[] {file.toURI().toURL()});

            Field loadersField = classLoaderClass.getDeclaredField("allLoaders");
            loadersField.setAccessible(true);
            if (Modifier.isFinal(loadersField.getModifiers())) {
                unfinalize(loadersField);
            }
            Set<Object> loaders = (Set<Object>) loadersField.get(null);
            loaders.add(loader);
            loadersField.set(null, loaders);
            return (ClassLoader) loader;
        } catch (Exception exception) {
            log.error("[Simplix | LibLoader] Cannot fabricate PluginClassLoader", exception);
        }
        return null;
    }

}
