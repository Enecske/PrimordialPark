package net.enecske.primordial_park.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.enecske.primordial_park.TimePeriod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ReportCardComponent(TimePeriod timePeriod, String species) {
    public static final Codec<ReportCardComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    TimePeriod.CODEC.fieldOf("time_period").forGetter(ReportCardComponent::timePeriod),
                    Codec.STRING.fieldOf("species").forGetter(ReportCardComponent::species)
            ).apply(instance, ReportCardComponent::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ReportCardComponent> STREAM_CODEC = StreamCodec.composite(
            TimePeriod.STREAM_CODEC, ReportCardComponent::timePeriod,
            ByteBufCodecs.STRING_UTF8, ReportCardComponent::species,
            ReportCardComponent::new
    );

    public static ReportCardComponent copyFrom(FossilComponent fossilComponent) {
        if (fossilComponent == null) return null;
        if (fossilComponent.data().isEmpty()) return null;
        if (!fossilComponent.data().get().type().equals("report_card")) return null;

        FossilComponent.ReportCardDataComponent reportCardDataComponent = (FossilComponent.ReportCardDataComponent) fossilComponent.data().get();
        return new ReportCardComponent(reportCardDataComponent.timePeriod(), fossilComponent.species());
    }
}
