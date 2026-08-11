package net.enecske.primordial_park.block;

import net.enecske.primordial_park.PrimordialPark;
import net.enecske.primordial_park.block.entity.PaleontologyTableBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, PrimordialPark.MODID);

    public static final Supplier<BlockEntityType<PaleontologyTableBlockEntity>> PALEONTOLOGY_TABLE = BLOCK_ENTITIES.register("paleontology_table",
            () -> BlockEntityType.Builder.of(PaleontologyTableBlockEntity::new, ModBlocks.PALEONTOLOGY_TABLE.get()).build(null));

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}
