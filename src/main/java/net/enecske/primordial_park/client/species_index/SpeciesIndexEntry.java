package net.enecske.primordial_park.client.species_index;

import net.enecske.primordial_park.TimePeriod;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Map;

public interface SpeciesIndexEntry {
    @NotNull String id();
    @NotNull Map<String, ItemStack> fossils();
    @NotNull TimePeriod timePeriod();
    @OnlyIn(Dist.CLIENT) @NotNull LivingEntity entity();
    @NotNull Dimensions entityDimensions();
    @NotNull Map<String, Component> hints();

    record Dimensions(int xOffset, int yOffset, float scale, Vector3f rotationOffset, Quaternionf rotation, float followMouseYOffset) {}
}
