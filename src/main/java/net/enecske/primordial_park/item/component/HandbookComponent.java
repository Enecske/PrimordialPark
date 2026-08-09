package net.enecske.primordial_park.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record HandbookComponent(UpgradeComponent upgrade) {
    public static final Codec<HandbookComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UpgradeComponent.CODEC.fieldOf("upgrade").forGetter(HandbookComponent::upgrade)
    ).apply(instance, HandbookComponent::new));

    public static final StreamCodec<ByteBuf, HandbookComponent> STREAM_CODEC = StreamCodec.composite(
            UpgradeComponent.STREAM_CODEC, HandbookComponent::upgrade,
            HandbookComponent::new
    );

    public record UpgradeComponent(boolean zooBasic, boolean zooAdvanced, boolean biotechBasic, boolean biotechAdvanced,
                                   boolean biosynthesis) {
        public static final Codec<UpgradeComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.BOOL.fieldOf("zoo_basic").forGetter(UpgradeComponent::zooBasic),
                Codec.BOOL.fieldOf("zoo_advanced").forGetter(UpgradeComponent::zooAdvanced),
                Codec.BOOL.fieldOf("biotech_basic").forGetter(UpgradeComponent::biotechBasic),
                Codec.BOOL.fieldOf("biotech_advanced").forGetter(UpgradeComponent::biotechAdvanced),
                Codec.BOOL.fieldOf("biosynthesis").forGetter(UpgradeComponent::biosynthesis)
        ).apply(instance, UpgradeComponent::new));

        public static final StreamCodec<ByteBuf, UpgradeComponent> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL, UpgradeComponent::zooBasic,
                ByteBufCodecs.BOOL, UpgradeComponent::zooAdvanced,
                ByteBufCodecs.BOOL, UpgradeComponent::biotechBasic,
                ByteBufCodecs.BOOL, UpgradeComponent::biotechAdvanced,
                ByteBufCodecs.BOOL, UpgradeComponent::biosynthesis,
                UpgradeComponent::new
        );

        public boolean[] getValues() {
            return new boolean[]{
                zooBasic,
                zooAdvanced,
                biotechBasic,
                biotechAdvanced,
                biosynthesis
            };
        }

        public String[] getKeys() {
            return new String[]{
                    "zoo_basic",
                    "zoo_advanced",
                    "biotech_basic",
                    "biotech_advanced",
                    "biosynthesis"
            };
        }
    }
}
