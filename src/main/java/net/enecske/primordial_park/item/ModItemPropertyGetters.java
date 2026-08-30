package net.enecske.primordial_park.item;

import net.enecske.primordial_park.PrimordialPark;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModItemPropertyGetters {
    public static void register() {
        ItemProperties.register(
                ModItems.CALIBRATION_MATRIX.get(),
                ResourceLocation.fromNamespaceAndPath(PrimordialPark.MODID, "calibration_matrix_active"),
                (stack, level, entity, seed) -> {
                    boolean active = stack.get(ModDataComponents.CALIBRATION_MATRIX.get()) != null;
                    return active ? 1 : 0;
                });
    }
}
