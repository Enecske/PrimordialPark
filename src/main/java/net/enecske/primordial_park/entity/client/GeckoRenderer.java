package net.enecske.primordial_park.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.enecske.primordial_park.PrimordialPark;
import net.enecske.primordial_park.entity.custom.GeckoEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class GeckoRenderer extends MobRenderer<GeckoEntity, GeckoModel<GeckoEntity>> {
    public GeckoRenderer(EntityRendererProvider.Context context) {
        super(context, new GeckoModel<>(context.bakeLayer(GeckoModel.LAYER_LOCATION)), .25f);
    }

    @Override
    public ResourceLocation getTextureLocation(GeckoEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(PrimordialPark.MODID, "textures/entity/gecko.png");
    }

    @Override
    public void render(GeckoEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(.45f, .45f, .45f);
        }
        else poseStack.scale(1f, 1f, 1f);

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
