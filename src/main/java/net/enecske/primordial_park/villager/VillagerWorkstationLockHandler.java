package net.enecske.primordial_park.villager;

import net.enecske.primordial_park.PrimordialPark;
import net.enecske.primordial_park.block.entity.PaleontologyTableBlockEntity;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.npc.Villager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.Optional;

@EventBusSubscriber(modid = PrimordialPark.MODID)
public class VillagerWorkstationLockHandler {
    @SubscribeEvent
    public static void onVillagerTick(EntityTickEvent.Pre event) {
        if (event.getEntity() instanceof Villager villager && !villager.level().isClientSide()) {
            if (villager.isSleeping()) return;

            if (villager.getVillagerData().getProfession() == ModVillagerProfessions.PALEONTOLOGIST.value()) {
                Optional<GlobalPos> jobSiteMemory = villager.getBrain().getMemory(MemoryModuleType.JOB_SITE);

                if (jobSiteMemory.isPresent()) {
                    GlobalPos globalPos = jobSiteMemory.get();

                    if (globalPos.dimension() == villager.level().dimension()) {
                        if (villager.level().getBlockEntity(globalPos.pos()) instanceof PaleontologyTableBlockEntity paleontologyTable) {
                            if (paleontologyTable.isReady()) {
                                double distanceSq = villager.distanceToSqr(
                                        globalPos.pos().getX() + 0.5,
                                        globalPos.pos().getY(),
                                        globalPos.pos().getZ() + 0.5
                                );

                                if (distanceSq > 2.25) {
                                    villager.getBrain().setMemory(
                                            MemoryModuleType.WALK_TARGET,
                                            new WalkTarget(new BlockPosTracker(globalPos.pos()), 0.5f, 1));
                                } else {
                                    villager.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
                                    villager.getNavigation().stop();

                                    villager.getLookControl().setLookAt(
                                            globalPos.pos().getX() + 0.5,
                                            globalPos.pos().getY() + 0.5,
                                            globalPos.pos().getZ() + 0.5
                                    );
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
