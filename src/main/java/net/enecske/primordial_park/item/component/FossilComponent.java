package net.enecske.primordial_park.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.enecske.primordial_park.TimePeriod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record FossilComponent(String id, String species, Optional<DataComponent> data) {
    public static final Codec<FossilComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("id").forGetter(FossilComponent::id),
                    Codec.STRING.fieldOf("species").forGetter(FossilComponent::species),
                    DataComponent.CODEC.optionalFieldOf("data").forGetter(FossilComponent::data)
            ).apply(instance, FossilComponent::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, FossilComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, FossilComponent::id,
            ByteBufCodecs.STRING_UTF8, FossilComponent::species,
            ByteBufCodecs.optional(DataComponent.STREAM_CODEC), FossilComponent::data,
            FossilComponent::new
    );

    public sealed interface DataComponent permits ReportCardDataComponent, DnaDataComponent {
        String type();

        Codec<DataComponent> CODEC = Codec.STRING.dispatch(
                DataComponent::type,
                type -> switch (type) {
                    case "report_card" -> ReportCardDataComponent.MAP_CODEC;
                    case "dna" -> DnaDataComponent.MAP_CODEC;
                    default -> throw new IllegalArgumentException("Unknown fossil data type " + type);
                }
        );

        StreamCodec<RegistryFriendlyByteBuf, DataComponent> STREAM_CODEC = StreamCodec.of(
                (buf, value) -> {
                    if (value instanceof ReportCardDataComponent(TimePeriod timePeriod)) {
                        buf.writeByte(0);
                        TimePeriod.STREAM_CODEC.encode(buf, timePeriod);
                    } else if (value instanceof DnaDataComponent(String dna)) {
                        buf.writeByte(1);
                        ByteBufCodecs.STRING_UTF8.encode(buf, dna);
                    }
                },
                buf -> {
                    byte typeId = buf.readByte();
                    if (typeId == 0) {
                        return new ReportCardDataComponent(TimePeriod.STREAM_CODEC.decode(buf));
                    } else if (typeId == 1) {
                        return new DnaDataComponent(ByteBufCodecs.STRING_UTF8.decode(buf));
                    }
                    throw new IllegalArgumentException("Unknown fossil network type ID: " + typeId);
                }
        );
    }

    public record ReportCardDataComponent(TimePeriod timePeriod) implements DataComponent {
        public static final MapCodec<ReportCardDataComponent> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        TimePeriod.CODEC.fieldOf("time_period").forGetter(ReportCardDataComponent::timePeriod)
                ).apply(instance, ReportCardDataComponent::new));


        @Override
        public String type() {
            return "report_card";
        }
    }

    public record DnaDataComponent(String dna) implements DataComponent {
        public static final MapCodec<DnaDataComponent> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.STRING.fieldOf("dna").forGetter(DnaDataComponent::dna)
                ).apply(instance, DnaDataComponent::new));

        @Override
        public String type() {
            return "dna";
        }
    }

    public boolean matches(ReportCardComponent reportCardComponent) {
        if (reportCardComponent == null) return false;

        if (this.data().isEmpty()) return false;
        if (!this.data().get().type().equals("report_card")) return false;

        if (!this.species().equals(reportCardComponent.species())) return false;
        return ((ReportCardDataComponent) this.data().get()).timePeriod() == reportCardComponent.timePeriod();
    }
}
