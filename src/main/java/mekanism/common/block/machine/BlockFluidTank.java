package mekanism.common.block.machine;

import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.text.EnumColor;
import mekanism.common.Mekanism;
import mekanism.common.base.IActiveState;
import mekanism.common.base.LazyOptionalHelper;
import mekanism.common.block.BlockMekanismContainer;
import mekanism.common.block.interfaces.IBlockDisableable;
import mekanism.common.block.interfaces.IColoredBlock;
import mekanism.common.block.interfaces.IHasGui;
import mekanism.common.block.interfaces.IHasInventory;
import mekanism.common.block.interfaces.IHasModel;
import mekanism.common.block.interfaces.IHasTileEntity;
import mekanism.common.block.interfaces.ISupportsComparator;
import mekanism.common.block.interfaces.ITieredBlock;
import mekanism.common.block.states.IStateActive;
import mekanism.common.config.MekanismConfig;
import mekanism.common.tier.FluidTankTier;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.base.WrenchResult;
import mekanism.common.tile.fluid_tank.TileEntityAdvancedFluidTank;
import mekanism.common.tile.fluid_tank.TileEntityBasicFluidTank;
import mekanism.common.tile.fluid_tank.TileEntityCreativeFluidTank;
import mekanism.common.tile.fluid_tank.TileEntityEliteFluidTank;
import mekanism.common.tile.fluid_tank.TileEntityFluidTank;
import mekanism.common.tile.fluid_tank.TileEntityUltimateFluidTank;
import mekanism.common.util.FluidContainerUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.PipeUtils;
import mekanism.common.util.SecurityUtils;
import mekanism.common.util.StackUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

public class BlockFluidTank extends BlockMekanismContainer implements IHasModel, IHasGui, IColoredBlock, IStateActive, ITieredBlock<FluidTankTier>, IHasInventory,
      IHasTileEntity<TileEntityFluidTank>, IBlockDisableable, ISupportsComparator {

    private static final VoxelShape TANK_BOUNDS = VoxelShapes.create(0.125F, 0.0F, 0.125F, 0.875F, 1.0F, 0.875F);

    private BooleanValue enabledReference;

    private final FluidTankTier tier;

    public BlockFluidTank(FluidTankTier tier) {
        super(Block.Properties.create(Material.IRON).hardnessAndResistance(3.5F, 16F));
        this.tier = tier;
        setRegistryName(new ResourceLocation(Mekanism.MODID, tier.getBaseTier().getSimpleName().toLowerCase(Locale.ROOT) + "_fluid_tank"));
    }

    @Override
    public FluidTankTier getTier() {
        return tier;
    }

    @Override
    public int getLightValue(BlockState state, IEnviromentBlockReader world, BlockPos pos) {
        if (MekanismConfig.client.enableAmbientLighting.get()) {
            TileEntity tileEntity = MekanismUtils.getTileEntitySafe(world, pos);
            if (tileEntity instanceof IActiveState && ((IActiveState) tileEntity).lightUpdate() && ((IActiveState) tileEntity).wasActiveRecently()) {
                return MekanismConfig.client.ambientLightingLevel.get();
            }
        }
        return 0;
    }

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (world.isRemote) {
            return true;
        }
        TileEntityMekanism tileEntity = (TileEntityMekanism) world.getTileEntity(pos);
        if (tileEntity.tryWrench(state, player, hand, hit) != WrenchResult.PASS) {
            return true;
        }
        //Handle filling fluid tank
        if (!player.isSneaking()) {
            if (SecurityUtils.canAccess(player, tileEntity)) {
                ItemStack stack = player.getHeldItem(hand);
                if (!stack.isEmpty() && FluidContainerUtils.isFluidContainer(stack) && manageInventory(player, (TileEntityFluidTank) tileEntity, hand, stack)) {
                    player.inventory.markDirty();
                    return true;
                }
            } else {
                SecurityUtils.displayNoAccess(player);
            }
            return true;
        }
        if (tileEntity.openGui(player)) {
            return true;
        }
        return false;
    }

    @Override
    public TileEntity createTileEntity(@Nonnull BlockState state, @Nonnull IBlockReader world) {
        switch (tier) {
            case BASIC:
                return new TileEntityBasicFluidTank();
            case ADVANCED:
                return new TileEntityAdvancedFluidTank();
            case ELITE:
                return new TileEntityEliteFluidTank();
            case ULTIMATE:
                return new TileEntityUltimateFluidTank();
            case CREATIVE:
                return new TileEntityCreativeFluidTank();
        }
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    @Nonnull
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    @Deprecated
    public float getPlayerRelativeBlockHardness(BlockState state, @Nonnull PlayerEntity player, @Nonnull IBlockReader world, @Nonnull BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return SecurityUtils.canAccess(player, tile) ? super.getPlayerRelativeBlockHardness(state, player, world, pos) : 0.0F;
    }

    @Override
    public float getExplosionResistance(BlockState state, IWorldReader world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
        //TODO: This is how it was before, but should it be divided by 5 like in Block.java
        return blockResistance;
    }

    private boolean manageInventory(PlayerEntity player, TileEntityFluidTank tileEntity, Hand hand, ItemStack itemStack) {
        ItemStack copyStack = StackUtils.size(itemStack.copy(), 1);
        return new LazyOptionalHelper<>(FluidUtil.getFluidHandler(copyStack)).getIfPresentElse(
              handler -> new LazyOptionalHelper<>(FluidUtil.getFluidContained(copyStack)).getIfPresentElseDo(
                    itemFluid -> {
                        int needed = tileEntity.getCurrentNeeded();
                        if (tileEntity.fluidTank.getFluid() != null && !tileEntity.fluidTank.getFluid().isFluidEqual(itemFluid)) {
                            return false;
                        }
                        boolean filled = false;
                        FluidStack drained = handler.drain(needed, !player.isCreative());
                        ItemStack container = handler.getContainer();
                        if (container.getCount() == 0) {
                            container = ItemStack.EMPTY;
                        }
                        if (drained != null) {
                            if (player.isCreative()) {
                                filled = true;
                            } else if (!container.isEmpty()) {
                                if (container.getCount() == 1) {
                                    player.setHeldItem(hand, container);
                                    filled = true;
                                } else if (player.inventory.addItemStackToInventory(container)) {
                                    itemStack.shrink(1);

                                    filled = true;
                                }
                            } else {
                                itemStack.shrink(1);
                                if (itemStack.getCount() == 0) {
                                    player.setHeldItem(hand, ItemStack.EMPTY);
                                }
                                filled = true;
                            }

                            if (filled) {
                                int toFill = tileEntity.fluidTank.getCapacity() - tileEntity.fluidTank.getFluidAmount();
                                if (tileEntity.tier != FluidTankTier.CREATIVE) {
                                    toFill = Math.min(toFill, drained.amount);
                                }
                                tileEntity.fluidTank.fill(PipeUtils.copy(drained, toFill), true);
                                if (drained.amount - toFill > 0) {
                                    tileEntity.pushUp(PipeUtils.copy(itemFluid, drained.amount - toFill), true);
                                }
                                return true;
                            }
                        }
                        return false;
                    },
                    () -> {
                        if (tileEntity.fluidTank.getFluid() != null) {
                            int filled = handler.fill(tileEntity.fluidTank.getFluid(), !player.isCreative());
                            ItemStack container = handler.getContainer();
                            if (filled > 0) {
                                if (itemStack.getCount() == 1) {
                                    player.setHeldItem(hand, container);
                                } else if (itemStack.getCount() > 1 && player.inventory.addItemStackToInventory(container)) {
                                    itemStack.shrink(1);
                                } else {
                                    player.dropItem(container, false, true);
                                    itemStack.shrink(1);
                                }
                                if (tileEntity.tier != FluidTankTier.CREATIVE) {
                                    tileEntity.fluidTank.drain(filled, true);
                                }
                                return true;
                            }
                        }
                        return false;
                    }
              ),
              false
        );
    }

    @Override
    @Deprecated
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
        if (!world.isRemote) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof TileEntityMekanism) {
                ((TileEntityMekanism) tileEntity).onNeighborChange(neighborBlock);
            }
        }
    }

    @Nonnull
    @Override
    @Deprecated
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return TANK_BOUNDS;
    }

    @Override
    public int getGuiID() {
        return 41;
    }

    @Override
    public EnumColor getColor() {
        return getTier().getBaseTier().getColor();
    }

    @Override
    public int getInventorySize() {
        return 2;
    }

    @Nullable
    @Override
    public Class<? extends TileEntityFluidTank> getTileClass() {
        switch (tier) {
            case BASIC:
                return TileEntityBasicFluidTank.class;
            case ADVANCED:
                return TileEntityAdvancedFluidTank.class;
            case ELITE:
                return TileEntityEliteFluidTank.class;
            case ULTIMATE:
                return TileEntityUltimateFluidTank.class;
            case CREATIVE:
                return TileEntityCreativeFluidTank.class;
        }
        return null;
    }

    @Override
    public boolean isEnabled() {
        return enabledReference == null ? true : enabledReference.get();
    }

    @Override
    public void setEnabledConfigReference(BooleanValue enabledReference) {
        this.enabledReference = enabledReference;
    }
}