package net.enecske.primordial_park.client.handbook;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.enecske.primordial_park.PrimordialPark;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class HandbookContentLoader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final HandbookContentLoader INSTANCE = new HandbookContentLoader();

    private static Map<ResourceLocation, HandbookContent> CACHED_CONTENT = Collections.emptyMap();

    public HandbookContentLoader() {
        super(GSON, "handbook");
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> loadedJsonFiles, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        Map<ResourceLocation, HandbookContent> filtered = new HashMap<>();

        loadedJsonFiles.forEach((location, jsonElement) -> {
            try {
                if (location.getNamespace().equals(PrimordialPark.MODID))
                    filtered.put(location, HandbookContent.parse(jsonElement));
            } catch (HandbookContent.HandbookContentException e) {
                PrimordialPark.LOGGER.error("Error parsing handbook content file {}", location, e);
            }
        });

        CACHED_CONTENT = Map.copyOf(filtered);
    }

    public static Map<ResourceLocation, HandbookContent> getCachedContent() {
        return CACHED_CONTENT;
    }

    private static ResourceLocation getContentLocation() {
        String lang = Minecraft.getInstance().getLanguageManager().getSelected();
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(PrimordialPark.MODID, lang);

        if (CACHED_CONTENT.containsKey(location))
            return location;

        return ResourceLocation.fromNamespaceAndPath(PrimordialPark.MODID, "en_us");
    }

    public static HandbookContent getContent() {
        return CACHED_CONTENT.get(getContentLocation());
    }
}
