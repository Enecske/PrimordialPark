package net.enecske.primordial_park.client.helper;

import net.enecske.primordial_park.client.gui.HandbookScreen;
import net.enecske.primordial_park.entity.attachment.SpeciesIndexAttachment;
import net.enecske.primordial_park.item.component.HandbookComponent;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientScreenHelper {
    public static void openHandbookScreen(HandbookComponent data, SpeciesIndexAttachment attachment) {
        Minecraft.getInstance().setScreen(new HandbookScreen(data, attachment));
    }
}
