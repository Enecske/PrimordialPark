package net.enecske.primordial_park.client.species_index;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.enecske.primordial_park.PrimordialPark;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class EntryContentLoader extends SimplePreparableReloadListener<Map<ResourceLocation, Map<String, JsonElement>>> {
    public static final EntryContentLoader INSTANCE = new EntryContentLoader();

    private static Map<ResourceLocation, Map<String, EntryContent>> CACHED_CONTENT = new HashMap<>();

    @Override
    protected @NotNull Map<ResourceLocation, Map<String, JsonElement>> prepare(@NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        Map<ResourceLocation, Map<String, JsonElement>> loadedData = new HashMap<>();

        Map<ResourceLocation, Resource> resources = resourceManager.listResources(
                "species_index",
                location -> location.getNamespace().equals(PrimordialPark.MODID) && location.getPath().endsWith(".json")
        );

        for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
            ResourceLocation fullPath = entry.getKey();
            String path = fullPath.getPath();

            String relativePath = path.substring("species_index/".length(), path.length() - ".json".length());
            String[] parts = relativePath.split("/");

            if (parts.length == 2) {
                String entityPath = parts[0];
                String langCode = parts[1];

                ResourceLocation entityId = ResourceLocation.fromNamespaceAndPath(fullPath.getNamespace(), entityPath);

                try (Reader reader = entry.getValue().openAsReader()){
                    JsonElement json = JsonParser.parseReader(reader);

                    loadedData.computeIfAbsent(entityId, k -> new HashMap<>())
                            .put(langCode, json);
                } catch (IOException e) {
                    PrimordialPark.LOGGER.error("Failed to load species index entry for {}", fullPath, e);
                }
            }
        }

        return loadedData;
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, Map<String, JsonElement>> preparedData, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        Map<ResourceLocation, Map<String, EntryContent>> parsed = new HashMap<>();

        preparedData.forEach((location, stringJsonElementMap) -> stringJsonElementMap.forEach((langCode, jsonElement) -> {
            try {
                EntryContent content = EntryContent.parse(jsonElement);

                if(!parsed.containsKey(location)) parsed.put(location, new HashMap<>());

                parsed.get(location).putIfAbsent(langCode, content);
            } catch (EntryContent.EntryContentException e) {
                PrimordialPark.LOGGER.error("Error loading species index entry for lang code '{}' of '{}'", langCode, location, e);
            }
        }));

        CACHED_CONTENT = Map.copyOf(parsed);
    }

    public static Map<ResourceLocation, Map<String, EntryContent>> getCachedContent() {
        return CACHED_CONTENT;
    }

    public static EntryContent getContent(ResourceLocation entity) {
        String lang = Minecraft.getInstance().getLanguageManager().getSelected();

        Map<String, EntryContent> langMap = CACHED_CONTENT.get(entity);
        if (langMap == null) return null;

        return langMap.getOrDefault(lang, langMap.get("en_us"));
    }
}
