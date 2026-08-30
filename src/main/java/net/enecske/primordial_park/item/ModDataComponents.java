package net.enecske.primordial_park.item;

import net.enecske.primordial_park.PrimordialPark;
import net.enecske.primordial_park.item.component.*;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, PrimordialPark.MODID);

    public static final Supplier<DataComponentType<HandbookComponent>> HANDBOOK = DATA_COMPONENTS.registerComponentType(
            "handbook",
            builder -> builder
                    .persistent(HandbookComponent.CODEC)
                    .networkSynchronized(HandbookComponent.STREAM_CODEC)
    );

    public static final Supplier<DataComponentType<HandbookUpgradeComponent>> HANDBOOK_UPGRADE = DATA_COMPONENTS.registerComponentType(
            "handbook_upgrade",
            builder -> builder
                    .persistent(HandbookUpgradeComponent.CODEC)
                    .networkSynchronized(HandbookUpgradeComponent.STREAM_CODEC)
    );

    public static final Supplier<DataComponentType<FossilPouchComponent>> FOSSIL_POUCH = DATA_COMPONENTS.registerComponentType(
            "fossil_pouch",
            builder -> builder
                    .persistent(FossilPouchComponent.CODEC)
                    .networkSynchronized(FossilPouchComponent.STREAM_CODEC)
    );

    public static final Supplier<DataComponentType<Component>> UNKNOWN_FOSSIL_HINT = DATA_COMPONENTS.registerComponentType(
            "unknown_fossil_hint",
            builder -> builder
                    .persistent(ComponentSerialization.CODEC)
                    .networkSynchronized(ComponentSerialization.STREAM_CODEC)
    );

    public static final Supplier<DataComponentType<FossilComponent>> FOSSIL = DATA_COMPONENTS.registerComponentType(
            "fossil",
            builder -> builder
                    .persistent(FossilComponent.CODEC)
                    .networkSynchronized(FossilComponent.STREAM_CODEC)
    );

    public static final Supplier<DataComponentType<ReportCardComponent>> REPORT_CARD = DATA_COMPONENTS.registerComponentType(
            "report_card",
            builder -> builder
                    .persistent(ReportCardComponent.CODEC)
                    .networkSynchronized(ReportCardComponent.STREAM_CODEC)
    );

    public static final Supplier<DataComponentType<CalibrationMatrixComponent>> CALIBRATION_MATRIX = DATA_COMPONENTS.registerComponentType(
            "calibration_matrix",
            builder -> builder
                    .persistent(CalibrationMatrixComponent.CODEC)
                    .networkSynchronized(CalibrationMatrixComponent.STREAM_CODEC)
    );

    public static void register(IEventBus eventBus) {
        DATA_COMPONENTS.register(eventBus);
    }
}
