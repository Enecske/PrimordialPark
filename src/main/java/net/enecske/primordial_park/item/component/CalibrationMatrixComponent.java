package net.enecske.primordial_park.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.enecske.primordial_park.TimePeriod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record CalibrationMatrixComponent(TimePeriod timePeriod) {
    public static final Codec<CalibrationMatrixComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    TimePeriod.CODEC.fieldOf("time_period").forGetter(CalibrationMatrixComponent::timePeriod)
            ).apply(instance, CalibrationMatrixComponent::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, CalibrationMatrixComponent> STREAM_CODEC = StreamCodec.composite(
            TimePeriod.STREAM_CODEC, CalibrationMatrixComponent::timePeriod,
            CalibrationMatrixComponent::new
    );
}
