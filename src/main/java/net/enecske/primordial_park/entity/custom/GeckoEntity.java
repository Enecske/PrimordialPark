package net.enecske.primordial_park.entity.custom;

import net.enecske.primordial_park.TimePeriod;
import net.enecske.primordial_park.client.species_index.SpeciesIndexEntry;
import net.enecske.primordial_park.client.species_index.SpeciesIndexRegistry;
import net.enecske.primordial_park.entity.ModEntities;
import net.enecske.primordial_park.helper.MapUtils;
import net.enecske.primordial_park.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Map;

public class GeckoEntity extends Animal {
    public final AnimationState IDLE_ANIMATION_STATE = new AnimationState();
    private int idleAnimationTimeout = 0;

    @SuppressWarnings("unused")
    public static final SpeciesIndexEntry ENTRY = SpeciesIndexRegistry.register(new SpeciesIndexEntry() {
        private final Map<String, ItemStack> fossils = MapUtils.linkedMapOf(
                Map.entry("mammoth_cranium", new ItemStack(ModItems.MAMMOTH_CRANIUM.get())),
                Map.entry("frozen_mammoth_flesh", new ItemStack(ModItems.FROZEN_MAMMOTH_FLESH.get())),
                Map.entry("mammoth_ivory_tusk", new ItemStack(ModItems.FOSSIL_CONCRETION.get())));

        private final Map<String, Component> hints = Map.of(
                "mammoth_cranium", Component.literal("Found in the ").withStyle(ChatFormatting.GRAY).append(Component.translatable("biome.minecraft.snowy_plains").withStyle(ChatFormatting.AQUA)),
                "frozen_mammoth_flesh", Component.literal("Found in the ").withStyle(ChatFormatting.GRAY).append(Component.translatable("biome.minecraft.snowy_plains").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.OBFUSCATED))
        );

        @Override
        public @NotNull String id() {
            return "woolly_mammoth";
        }

        @Override
        public @NotNull Map<String, ItemStack> fossils() {
            return fossils;
        }

        @Override
        public @NotNull TimePeriod timePeriod() {
            return TimePeriod.ICE_AGE;
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public @NotNull LivingEntity entity() {
            return new GeckoEntity(ModEntities.GECKO.get(), Minecraft.getInstance().level);
        }

        @Override
        public @NotNull Dimensions entityDimensions() {
            return new Dimensions(
                    17, 24,
                    60,
                    new Vector3f(0f, 0f, 0f),
                    new Quaternionf().rotationXYZ((float) Math.toRadians(190), (float) Math.toRadians(20), (float) Math.toRadians(0)),
                    -.08f);
        }

        @Override
        public @NotNull Map<String, Component> hints() {
            return hints;
        }
    });

    public GeckoEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));

        goalSelector.addGoal(1, new PanicGoal(this, 2));
        goalSelector.addGoal(2, new BreedGoal(this, 1));

        goalSelector.addGoal(3, new FollowParentGoal(this, 1.25));

        goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1));
        goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6f));
        goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 10d)
                .add(Attributes.MOVEMENT_SPEED, .25d)
                .add(Attributes.FOLLOW_RANGE, 24d);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.BRICK);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(@NotNull ServerLevel level, @NotNull AgeableMob otherParent) {
        return ModEntities.GECKO.get().create(level);
    }

    private void setupAnimationStates() {
        if (idleAnimationTimeout <= 0) {
            idleAnimationTimeout = 80;
            IDLE_ANIMATION_STATE.start(tickCount);
        } else --idleAnimationTimeout;
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide()) setupAnimationStates();
    }
}
