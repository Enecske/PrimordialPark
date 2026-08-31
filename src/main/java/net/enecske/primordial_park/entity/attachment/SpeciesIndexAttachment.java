package net.enecske.primordial_park.entity.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.ArrayList;
import java.util.List;

public record SpeciesIndexAttachment(List<SpeciesEntryAttachment> entries) {
    public static final Codec<SpeciesIndexAttachment> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    SpeciesEntryAttachment.CODEC.listOf().fieldOf("entries").forGetter(SpeciesIndexAttachment::entries)
            ).apply(instance, SpeciesIndexAttachment::new)
    );

    public static final SpeciesIndexAttachment EMPTY = new SpeciesIndexAttachment(List.of());

    public boolean hasSpecies(String id) {
        return this.entries.stream().anyMatch(entry -> entry.id.equals(id));
    }

    public boolean hasFossil(String id, String fossil) {
        if (!this.hasSpecies(id)) return false;

        SpeciesEntryAttachment entry = entries.stream().filter(speciesEntryAttachment ->
                speciesEntryAttachment.id.equals(id)).findFirst().orElseGet(() -> new SpeciesEntryAttachment(id, List.of(), false));

        return entry.fossils().stream().anyMatch(fossilId -> fossilId.equals(fossil));
    }

    public SpeciesIndexAttachment addSpecies(String id) {
        if (hasSpecies(id)) return this;

        List<SpeciesEntryAttachment> updated = new ArrayList<>(this.entries);
        updated.add(new SpeciesEntryAttachment(id, List.of(), false));
        return new SpeciesIndexAttachment(updated);
    }

    public SpeciesIndexAttachment addFossil(String targetId, String fossil) {
        if (!hasSpecies(targetId)) return this;

        List<SpeciesEntryAttachment> updated = this.entries.stream()
                .map(entry -> entry.id().equals(targetId) ? entry.addFossil(fossil) : entry)
                .toList();

        return new SpeciesIndexAttachment(updated);
    }

    public SpeciesIndexAttachment removeFossil(String targetId, String fossil) {
        if (!hasSpecies(targetId)) return this;

        List<SpeciesEntryAttachment> updated = this.entries.stream()
                .map(entry -> entry.id().equals(targetId) ? entry.removeFossil(fossil) : entry)
                .toList();

        return new SpeciesIndexAttachment(updated);
    }

    public SpeciesIndexAttachment sighted(String targetId, boolean sighted) {
        if (!hasSpecies(targetId)) return this;

        List<SpeciesEntryAttachment> updated = this.entries.stream()
                .map(entry -> entry.id().equals(targetId) ? entry.sighted(sighted) : entry)
                .toList();

        return new SpeciesIndexAttachment(updated);
    }

    public record SpeciesEntryAttachment(String id, List<String> fossils, boolean sighted) {
        public static final Codec<SpeciesEntryAttachment> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.STRING.fieldOf("id").forGetter(SpeciesEntryAttachment::id),
                        Codec.STRING.listOf().fieldOf("fossils").forGetter(SpeciesEntryAttachment::fossils),
                        Codec.BOOL.fieldOf("sighted").forGetter(SpeciesEntryAttachment::sighted)
                ).apply(instance, SpeciesEntryAttachment::new)
        );

        public SpeciesEntryAttachment addFossil(String fossil) {
            if (this.fossils.contains(fossil)) return this;

            List<String> updated = new ArrayList<>(this.fossils);
            updated.add(fossil);
            return new SpeciesEntryAttachment(this.id, updated, this.sighted);
        }

        public SpeciesEntryAttachment removeFossil(String fossil) {
            List<String> updated = new ArrayList<>(this.fossils);
            updated.remove(fossil);
            return new SpeciesEntryAttachment(this.id, updated, this.sighted);
        }

        public SpeciesEntryAttachment sighted(boolean sighted) {
            return new SpeciesEntryAttachment(this.id, this.fossils, sighted);
        }
    }
}
