package mekanism.client.gui.element.bar;

import java.util.List;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.MekanismLang;
import mekanism.common.network.to_server.PacketDropperUse.TankType;
import mekanism.common.util.ChemicalUtil;
import mekanism.common.util.text.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuiChemicalBar<CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>> extends GuiTankBar<STACK> {

    public GuiChemicalBar(IGuiWrapper gui, TankInfoProvider<STACK> infoProvider, int x, int y, int width, int height, boolean horizontal) {
        super(gui, infoProvider, x, y, width, height, horizontal);
    }

    @Override
    protected boolean isEmpty(STACK stack) {
        return stack.isEmpty();
    }

    @Nullable
    @Override
    protected TankType getType(STACK stack) {
        CHEMICAL type = getHandler().getStack().getChemical();
        return switch (type) {
            case Gas gas -> TankType.GAS_TANK;
            case InfuseType infuseType -> TankType.INFUSION_TANK;
            case Pigment pigment -> TankType.PIGMENT_TANK;
            case Slurry slurry -> TankType.SLURRY_TANK;
            default -> null;
        };
    }

    @Override
    protected List<Component> getTooltip(STACK stack) {
        List<Component> tooltips = super.getTooltip(stack);
        ChemicalUtil.addChemicalDataToTooltip(tooltips, stack.getChemical(), Minecraft.getInstance().options.advancedItemTooltips);
        return tooltips;
    }

    @Override
    protected void applyRenderColor(GuiGraphics guiGraphics, STACK stack) {
        MekanismRenderer.color(guiGraphics, stack);
    }

    @Override
    protected TextureAtlasSprite getIcon(STACK stack) {
        return MekanismRenderer.getChemicalTexture(stack.getChemical());
    }

    public static <STACK extends ChemicalStack<?>, TANK extends IChemicalTank<?, STACK>> TankInfoProvider<STACK> getProvider(TANK tank, List<TANK> tanks) {
        return new TankInfoProvider<>() {
            @NotNull
            @Override
            public STACK getStack() {
                return tank.getStack();
            }

            @Override
            public int getTankIndex() {
                return tanks.indexOf(tank);
            }

            @Override
            public Component getTooltip() {
                if (tank.isEmpty()) {
                    return MekanismLang.EMPTY.translate();
                } else if (tank.getStored() == Long.MAX_VALUE) {
                    return MekanismLang.GENERIC_STORED.translate(tank.getType(), MekanismLang.INFINITE);
                }
                return MekanismLang.GENERIC_STORED_MB.translate(tank.getType(), TextUtils.format(tank.getStored()));
            }

            @Override
            public double getLevel() {
                return tank.getStored() / (double) tank.getCapacity();
            }
        };
    }
}