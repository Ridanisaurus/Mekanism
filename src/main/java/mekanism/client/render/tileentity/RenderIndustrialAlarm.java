package mekanism.client.render.tileentity;

import javax.annotation.ParametersAreNonnullByDefault;
import com.mojang.blaze3d.matrix.MatrixStack;
import mekanism.api.Pos3D;
import mekanism.client.model.ModelIndustrialAlarm;
import mekanism.client.render.BoltRenderer;
import mekanism.common.base.ProfilerConstants;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.tile.TileEntityIndustrialAlarm;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.profiler.IProfiler;

@ParametersAreNonnullByDefault
public class RenderIndustrialAlarm extends MekanismTileEntityRenderer<TileEntityIndustrialAlarm> {

    private static final float ROTATE_SPEED = 10F;
    private final ModelIndustrialAlarm model = new ModelIndustrialAlarm();

    public RenderIndustrialAlarm(TileEntityRendererDispatcher renderer) {
        super(renderer);
    }

    @Override
    protected void render(TileEntityIndustrialAlarm tile, float partialTick, MatrixStack matrix, IRenderTypeBuffer renderer, int light, int overlayLight, IProfiler profiler) {
        performTranslations(tile, matrix);
        float rotation = (tile.getWorld().getDayTime() + partialTick) * ROTATE_SPEED % 360;
        model.render(matrix, renderer, light, overlayLight, Attribute.isActive(tile.getBlockState()), rotation, false);
        BoltRenderer.render(new Pos3D(0, 0, 0), new Pos3D(10, 10, 10), 5, 15, partialTick, matrix, renderer, light);
        matrix.pop();
    }

    @Override
    protected String getProfilerSection() {
        return ProfilerConstants.INDUSTRIAL_ALARM;
    }

    @Override
    public boolean isGlobalRenderer(TileEntityIndustrialAlarm tile) {
        return true;
    }

    /**
     * Make sure to call matrix.pop afterwards
     */
    private void performTranslations(TileEntityIndustrialAlarm tile, MatrixStack matrix) {
        matrix.push();
        matrix.translate(0.5, 0, 0.5);
        switch (tile.getDirection()) {
            case DOWN: {
                matrix.translate(0, 1, 0);
                matrix.rotate(Vector3f.XP.rotationDegrees(180));
                break;
            }
            case NORTH: {
                matrix.translate(0, 0.5, 0.5);
                matrix.rotate(Vector3f.XN.rotationDegrees(90));
                break;
            }
            case SOUTH: {
                matrix.translate(0, 0.5, -0.5);
                matrix.rotate(Vector3f.XP.rotationDegrees(90));
                break;
            }
            case EAST: {
                matrix.translate(-0.5, 0.5, 0);
                matrix.rotate(Vector3f.ZN.rotationDegrees(90));
                break;
            }
            case WEST: {
                matrix.translate(0.5, 0.5, 0);
                matrix.rotate(Vector3f.ZP.rotationDegrees(90));
                break;
            }
            default: break;
        }
    }
}
