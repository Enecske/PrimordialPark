package net.enecske.primordial_park.villager;

import com.google.common.collect.ImmutableSet;
import net.enecske.primordial_park.PrimordialPark;
import net.enecske.primordial_park.block.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModPoiTypes {
    public static final DeferredRegister<PoiType> POI_TYPES =
            DeferredRegister.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE, PrimordialPark.MODID);

    public static final Holder<PoiType> PALEONTOLOGIST = POI_TYPES.register("paleontologist",
            () -> new PoiType(ImmutableSet.copyOf(ModBlocks.PALEONTOLOGY_TABLE.get().getStateDefinition().getPossibleStates()), 1, 1));

    public static void register(IEventBus bus) {
        POI_TYPES.register(bus);
    }
}
