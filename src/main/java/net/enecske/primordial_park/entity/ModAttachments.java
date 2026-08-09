package net.enecske.primordial_park.entity;

import net.enecske.primordial_park.PrimordialPark;
import net.enecske.primordial_park.entity.attachment.SpeciesIndexAttachment;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, PrimordialPark.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<SpeciesIndexAttachment>> SPECIES_INDEX =
            ATTACHMENT_TYPES.register("species_index", () -> AttachmentType.builder(
                                    () -> SpeciesIndexAttachment.EMPTY
                            )
                            .serialize(SpeciesIndexAttachment.CODEC)
                            .copyOnDeath()
                            .build()
            );

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}
