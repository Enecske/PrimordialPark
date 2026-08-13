package net.enecske.primordial_park.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.component.ItemContainerContents;

public record FossilPouchComponent(ItemContainerContents contents, boolean autoPickup) {
    public static final Codec<FossilPouchComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ItemContainerContents.CODEC.fieldOf("contents").forGetter(FossilPouchComponent::contents),
                    Codec.BOOL.fieldOf("autoPickup").forGetter(FossilPouchComponent::autoPickup)
            ).apply(instance, FossilPouchComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FossilPouchComponent> STREAM_CODEC = StreamCodec.composite(
            ItemContainerContents.STREAM_CODEC, FossilPouchComponent::contents,
            ByteBufCodecs.BOOL, FossilPouchComponent::autoPickup,
            FossilPouchComponent::new
    );

    public static final FossilPouchComponent EMPTY = new FossilPouchComponent(ItemContainerContents.EMPTY, false);
}
