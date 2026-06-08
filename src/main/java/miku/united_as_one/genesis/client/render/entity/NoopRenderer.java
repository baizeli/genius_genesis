package miku.united_as_one.genesis.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class NoopRenderer<T extends Entity> extends EntityRenderer<T> {
    private static final ResourceLocation EMPTY = new ResourceLocation("minecraft", "textures/misc/unknown_pack.png");

    public NoopRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull T entity) {
        return EMPTY;
    }
}
