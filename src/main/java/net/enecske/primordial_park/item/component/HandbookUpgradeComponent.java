package net.enecske.primordial_park.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record HandbookUpgradeComponent(String id) {
    public static final Codec<HandbookUpgradeComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.fieldOf("id").forGetter(HandbookUpgradeComponent::id)
    ).apply(instance, HandbookUpgradeComponent::new));

    public static final StreamCodec<ByteBuf, HandbookUpgradeComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, HandbookUpgradeComponent::id,
            HandbookUpgradeComponent::new
    );
}
