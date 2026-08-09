package net.enecske.primordial_park.client.species_index;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;

public class EntryContent {
    public final Component[] paragraphs;

    private EntryContent(JsonElement json) throws EntryContentException {
        if (!json.isJsonObject()) throw new EntryContentException("Json root must be an Object");

        JsonObject root = json.getAsJsonObject();

        if (!root.has("paragraphs")) throw new EntryContentException("Missing 'paragraphs' member");
        if (!root.get("paragraphs").isJsonArray()) throw new EntryContentException("'paragraphs' must be an Array");

        JsonArray paragraphs = root.getAsJsonArray("paragraphs");
        this.paragraphs = new Component[paragraphs.size()];

        for (int i = 0; i < paragraphs.size(); i++) {
            try {
                this.paragraphs[i] = ComponentSerialization.CODEC
                        .parse(JsonOps.INSTANCE, paragraphs.get(i))
                        .resultOrPartial()
                        .orElseThrow(() -> new EntryContentException("Invalid JSON component"));
            } catch (EntryContentException e) {
                throw new EntryContentException("%s at index %s".formatted(e.getMessage(), i));
            } catch (Exception e) {
                throw new EntryContentException("%s: %s".formatted(e.getClass().getName(), e.getMessage()));
            }
        }
    }

    public static EntryContent parse(JsonElement json) throws EntryContentException {
        return new EntryContent(json);
    }

    public static class EntryContentException extends Exception {
        public EntryContentException(String message) {
            super(message);
        }
    }
}
