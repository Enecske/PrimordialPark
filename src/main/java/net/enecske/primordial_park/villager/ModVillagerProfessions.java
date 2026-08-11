package net.enecske.primordial_park.villager;

import com.google.common.collect.ImmutableSet;
import net.enecske.primordial_park.PrimordialPark;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModVillagerProfessions {
    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS =
            DeferredRegister.create(BuiltInRegistries.VILLAGER_PROFESSION, PrimordialPark.MODID);

    // TODO custom sound
    public static final Holder<VillagerProfession> PALEONTOLOGIST = VILLAGER_PROFESSIONS.register("paleontologist",
            () -> new VillagerProfession("paleontologist", holder -> holder.value() == ModPoiTypes.PALEONTOLOGIST.value(),
                    poiTypeHolder -> poiTypeHolder.value() == ModPoiTypes.PALEONTOLOGIST.value(), ImmutableSet.of(), ImmutableSet.of(),
                    SoundEvents.VILLAGER_WORK_ARMORER));

    public static void register(IEventBus bus) {
        VILLAGER_PROFESSIONS.register(bus);
    }
}
