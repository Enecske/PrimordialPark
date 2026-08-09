package net.enecske.primordial_park.client.handbook;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.enecske.primordial_park.client.helper.TextureHelper;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class HandbookContent {
    private final HashMap<String, HandbookChapter> chapters = new HashMap<>();

    private HandbookContent(JsonElement json) throws HandbookContentException {
        if (!json.isJsonObject()) throw new HandbookContentException("Json root must be an Object");

        JsonObject root = json.getAsJsonObject();

        if (!root.has("chapters")) throw new HandbookContentException("Missing 'chapters' member");
        if (!root.get("chapters").isJsonObject()) throw new HandbookContentException("'chapters' must be an Object");

        JsonObject chapters = root.getAsJsonObject("chapters");

        for (String id : chapters.keySet()) {
            if (!chapters.get(id).isJsonObject())
                throw new HandbookContentException("'chapters' may only contain Objects");

            HandbookChapter chapter = new HandbookChapter(id, chapters.getAsJsonObject(id));

            this.chapters.put(id, chapter);
        }
    }

    public Map<String, HandbookChapter> getChapters() {
        return Map.copyOf(chapters);
    }

    public static HandbookContent parse(JsonElement json) throws HandbookContentException {
        return new HandbookContent(json);
    }

    @OnlyIn(Dist.CLIENT)
    public static class HandbookChapter {
        public final String id;
        public final String title;

        public final HandbookPage[] pages;

        private HandbookChapter(String id, JsonObject json) throws HandbookContentException {
            this.id = id;

            if (!json.has("title")) throw new HandbookContentException("'chapter' must contain String field 'title'");
            if (!json.get("title").isJsonPrimitive() || !json.getAsJsonPrimitive("title").isString())
                throw new HandbookContentException("'title' must be a String");

            title = json.get("title").getAsString();

            if (!json.has("pages")) throw new HandbookContentException("'chapter' must contain Array 'pages'");
            if (!json.get("pages").isJsonArray()) throw new HandbookContentException("'pages' must be an Array");

            JsonArray pages = json.getAsJsonArray("pages");
            this.pages = new HandbookPage[pages.size()];

            for (int i = 0; i < pages.size(); i++) {
                if (!pages.get(i).isJsonObject()) throw new HandbookContentException("'pages' must contain Objects");

                HandbookPage page = new HandbookPage(pages.get(i).getAsJsonObject());
                this.pages[i] = page;
            }
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder("%s: { ".formatted(id));

            builder.append("title: '%s', [ ".formatted(title));

            for (HandbookPage page : pages) {
                builder.append(page);
                builder.append(", ");
            }

            builder.append("] }");

            return builder.toString();
        }

        public HandbookPage[] getPagesWithCondition(String[] conditions) {
            ArrayList<HandbookPage> filtered = new ArrayList<>();

            for (HandbookPage page : pages) {
                if (page.condition == null) {
                    filtered.add(page);
                    break;
                }
                for (String condition : conditions) {
                    if (page.condition.equals(condition)) {
                        filtered.add(page);
                        break;
                    }
                }
            }

            return filtered.toArray(new HandbookPage[0]);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class HandbookPage {
        public final HandbookParagraph[] paragraphs;
        public final String condition;

        private HandbookPage(JsonObject json) throws HandbookContentException {
            if (!json.has("paragraphs")) throw new HandbookContentException("Page must contain Array 'paragraphs'");
            if (!json.get("paragraphs").isJsonArray())
                throw new HandbookContentException("'paragraphs' must be an Array");

            JsonArray paragraphs = json.getAsJsonArray("paragraphs");
            this.paragraphs = new HandbookParagraph[paragraphs.size()];

            for (int i = 0; i < paragraphs.size(); i++) {
                if (!paragraphs.get(i).isJsonObject())
                    throw new HandbookContentException("'paragraphs' must contain Objects");

                JsonObject object = paragraphs.get(i).getAsJsonObject();

                if (!object.has("type"))
                    throw new HandbookContentException("Paragraph must contain String field 'type'");
                if (!object.get("type").isJsonPrimitive() || !object.getAsJsonPrimitive("type").isString())
                    throw new HandbookContentException("'type' must be a String");

                switch (object.getAsJsonPrimitive("type").getAsString()) {
                    case "text":
                        this.paragraphs[i] = new HandbookTextParagraph(object);
                        break;
                    case "image":
                        this.paragraphs[i] = new HandbookImageParagraph(object);
                        break;
                    default:
                        throw new HandbookContentException("'type' must be one of: 'text', 'image'");
                }
            }

            if (json.has("condition")) {
                if (!json.get("condition").isJsonPrimitive() || !json.getAsJsonPrimitive("condition").isString())
                    throw new HandbookContentException("'condition' must be a String");

                this.condition = json.get("condition").getAsString();
            } else this.condition = null;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder("{ paragraphs: [ ");

            for (HandbookParagraph paragraph : paragraphs) {
                builder.append(paragraph);
                builder.append(", ");
            }

            builder.append("], condition: %s }".formatted(condition));

            return builder.toString();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static abstract class HandbookParagraph {
    }

    @OnlyIn(Dist.CLIENT)
    public static class HandbookTextParagraph extends HandbookParagraph {
        public final String text;

        private HandbookTextParagraph(JsonObject object) throws HandbookContentException {
            if (!object.has("text")) throw new HandbookContentException("Paragraph must contain String field 'text'");
            if (!object.get("text").isJsonPrimitive() || !object.getAsJsonPrimitive("text").isString())
                throw new HandbookContentException("'text' must be a String");

            this.text = object.get("text").getAsString();
        }

        @Override
        public String toString() {
            return "{ type: text, text: [length: %s] }".formatted(text.length());
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class HandbookImageParagraph extends HandbookParagraph {
        public final ResourceLocation imageLocation;

        public final int width;
        public final int height;

        private HandbookImageParagraph(JsonObject object) throws HandbookContentException {
            if (!object.has("image")) throw new HandbookContentException("Paragraph must contain String field 'image'");
            if (!object.get("image").isJsonPrimitive() || !object.getAsJsonPrimitive("image").isString())
                throw new HandbookContentException("'image' must be a String");

            this.imageLocation = ResourceLocation.parse(object.get("image").getAsString());

            TextureHelper.ImageDimensions dimensions = TextureHelper.getImageDimensions(imageLocation);
            if (dimensions == null)
                throw new HandbookContentException("Image file '%s' does not exist".formatted(imageLocation));

            this.width = dimensions.width();
            this.height = dimensions.height();
        }

        @Override
        public String toString() {
            return "{ type: image, image: %s }".formatted(imageLocation);
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{ chapters: ");

        chapters.values().forEach(handbookChapter -> {
            stringBuilder.append(handbookChapter.toString());
            stringBuilder.append(", ");
        });

        stringBuilder.append(" }");

        return stringBuilder.toString();
    }

    public static class HandbookContentException extends Exception {
        public HandbookContentException(String message) {
            super(message);
        }
    }
}
