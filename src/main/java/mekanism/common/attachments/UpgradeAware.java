package mekanism.common.attachments;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mekanism.api.NBTConstants;
import mekanism.api.Upgrade;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.inventory.IMekanismInventory;
import mekanism.common.attachments.containers.ContainerType;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.attribute.AttributeUpgradeSupport;
import mekanism.common.inventory.slot.UpgradeInventorySlot;
import mekanism.common.util.ItemDataUtils;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

@NothingNullByDefault
public class UpgradeAware implements INBTSerializable<CompoundTag>, IMekanismInventory {

    public static UpgradeAware create(IAttachmentHolder attachmentHolder) {
        if (attachmentHolder instanceof ItemStack stack && !stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem) {
            AttributeUpgradeSupport upgradeSupport = Attribute.get(blockItem.getBlock(), AttributeUpgradeSupport.class);
            if (upgradeSupport != null) {
                return new UpgradeAware(stack, upgradeSupport.supportedUpgrades());
            }
        }
        throw new IllegalArgumentException("Attempted to attach upgrade awareness to an object that does not support upgrades.");
    }

    private final Map<Upgrade, Integer> upgrades = new EnumMap<>(Upgrade.class);
    private final Set<Upgrade> supportedUpgrades;
    private final List<IInventorySlot> upgradeSlots;

    private UpgradeAware(ItemStack stack, Set<Upgrade> supportedUpgrades) {
        this.supportedUpgrades = supportedUpgrades;
        this.upgradeSlots = List.of(UpgradeInventorySlot.input(null, this.supportedUpgrades), UpgradeInventorySlot.output(null));
        loadLegacyData(stack);
    }

    @Deprecated//TODO - 1.21?: Remove this way of loading legacy data
    protected void loadLegacyData(ItemStack stack) {
        ItemDataUtils.getAndRemoveData(stack, NBTConstants.COMPONENT_UPGRADE, CompoundTag::getCompound).ifPresent(this::deserializeNBT);
    }

    public Set<Upgrade> getSupportedUpgrades() {
        return supportedUpgrades;
    }

    public Map<Upgrade, Integer> getUpgrades() {
        return upgrades;
    }

    public void setUpgrades(Map<Upgrade, Integer> upgrades) {
        this.upgrades.clear();
        this.upgrades.putAll(upgrades);
    }

    public int getUpgradeCount(Upgrade upgrade) {
        return upgrades.getOrDefault(upgrade, 0);
    }

    public boolean isCompatible(UpgradeAware other) {
        if (other == this) {
            return true;
        } else if (!upgrades.equals(other.upgrades)) {
            return false;
        }
        for (int i = 0, slots = upgradeSlots.size(); i < slots; i++) {
            if (!upgradeSlots.get(i).isCompatible(other.upgradeSlots.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<IInventorySlot> getInventorySlots(@Nullable Direction side) {
        return upgradeSlots;
    }

    @Override
    public void onContentsChanged() {
    }

    @Nullable
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag upgradeNBT = new CompoundTag();
        if (!upgrades.isEmpty()) {
            Upgrade.saveMap(upgrades, upgradeNBT);
        }
        //Save the inventory
        ContainerType.ITEM.saveTo(upgradeNBT, upgradeSlots);
        return upgradeNBT.isEmpty() ? null : upgradeNBT;
    }

    @Override
    public void deserializeNBT(CompoundTag upgradeNBT) {
        setUpgrades(Upgrade.buildMap(upgradeNBT));
        //Load the inventory
        ContainerType.ITEM.readFrom(upgradeNBT, upgradeSlots);
    }
}