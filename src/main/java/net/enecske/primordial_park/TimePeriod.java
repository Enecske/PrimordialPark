package net.enecske.primordial_park;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum TimePeriod implements StringRepresentable {
    ICE_AGE("ice_age"),
    CALABRIAN_STAGE("calabrian_stage"),
    MAASTRICHTIAN_STAGE("maastrichtian_stage");

    public final String id;

    TimePeriod(String id) {
        this.id = id;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.id;
    }

    public static final Codec<TimePeriod> CODEC =
            StringRepresentable.fromEnum(TimePeriod::values);

    public static final StreamCodec<RegistryFriendlyByteBuf, TimePeriod> STREAM_CODEC =
            ByteBufCodecs.idMapper(id -> values()[id], TimePeriod::ordinal).cast();
}
