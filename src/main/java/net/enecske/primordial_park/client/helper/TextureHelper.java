package net.enecske.primordial_park.client.helper;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.io.IOException;
import java.io.InputStream;

@OnlyIn(Dist.CLIENT)
public class TextureHelper {
    public static ImageDimensions getImageDimensions(ResourceLocation location) {
        try (InputStream stream = Minecraft.getInstance().getResourceManager().open(location)) {
            NativeImage image = NativeImage.read(stream);

            int width = image.getWidth();
            int height = image.getHeight();

            return new ImageDimensions(width, height);
        } catch (IOException e) {
            return null;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public record ImageDimensions(int width, int height) {
    }
}
