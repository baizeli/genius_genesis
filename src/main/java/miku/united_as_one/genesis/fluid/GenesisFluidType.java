package miku.united_as_one.genesis.fluid;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.function.Consumer;

public class GenesisFluidType extends FluidType {

    private static final ResourceLocation UNDERWATER_OVERLAY = new ResourceLocation("textures/misc/underwater.png");

    private final ResourceLocation stillTexture;
    private final ResourceLocation flowingTexture;
    private final Visual visual;

    public GenesisFluidType(Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture, Visual visual) {
        super(properties);
        this.stillTexture = stillTexture;
        this.flowingTexture = flowingTexture;
        this.visual = visual;
    }

    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return stillTexture;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return flowingTexture;
            }

            @Override
            public int getTintColor() {
                return 0xFFFFFFFF;
            }

            @Nullable
            @Override
            public ResourceLocation getRenderOverlayTexture(Minecraft minecraft) {
                return UNDERWATER_OVERLAY;
            }

            @NotNull
            @Override
            public Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
                return new Vector3f(visual.red(), visual.green(), visual.blue());
            }

            @Override
            public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape) {
                RenderSystem.setShaderFogStart(visual.fogStart());
                RenderSystem.setShaderFogEnd(visual.fogEnd());
                RenderSystem.setShaderFogShape(visual.fogShape().toVanilla());
            }
        });
    }

    public record Visual(float red, float green, float blue, float fogStart, float fogEnd, FogShapeType fogShape) {
    }

    public enum FogShapeType {
        SPHERE,
        CYLINDER;

        private FogShape toVanilla() {
            return switch (this) {
                case SPHERE -> FogShape.SPHERE;
                case CYLINDER -> FogShape.CYLINDER;
            };
        }
    }
}
