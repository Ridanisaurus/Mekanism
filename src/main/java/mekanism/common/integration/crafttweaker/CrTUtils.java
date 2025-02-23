package mekanism.common.integration.crafttweaker;

import com.blamejared.crafttweaker.api.fluid.CTFluidIngredient;
import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import com.blamejared.crafttweaker.api.ingredient.IIngredientWithAmount;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.recipe.component.IDecomposedRecipe;
import com.blamejared.crafttweaker.api.recipe.component.IRecipeComponent;
import com.blamejared.crafttweaker.api.tag.CraftTweakerTagRegistry;
import com.blamejared.crafttweaker.api.tag.manager.type.KnownTagManager;
import com.blamejared.crafttweaker.api.tag.type.KnownTag;
import com.blamejared.crafttweaker.natives.ingredient.ExpandCTFluidIngredientNeoForge;
import com.blamejared.crafttweaker.natives.ingredient.ExpandIIngredientWithAmountNeoForge;
import com.blamejared.crafttweaker.natives.ingredient.ExpandSizedFluidIngredient;
import com.blamejared.crafttweaker.natives.ingredient.ExpandSizedIngredient;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.ChemicalType;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.merged.BoxedChemicalStack;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.recipes.ingredients.FluidStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.common.integration.MekanismHooks;
import mekanism.common.integration.crafttweaker.chemical.CrTChemicalStack.CrTGasStack;
import mekanism.common.integration.crafttweaker.chemical.CrTChemicalStack.CrTInfusionStack;
import mekanism.common.integration.crafttweaker.chemical.CrTChemicalStack.CrTPigmentStack;
import mekanism.common.integration.crafttweaker.chemical.CrTChemicalStack.CrTSlurryStack;
import mekanism.common.integration.crafttweaker.chemical.ICrTChemicalStack;
import mekanism.common.integration.crafttweaker.chemical.ICrTChemicalStack.ICrTGasStack;
import mekanism.common.integration.crafttweaker.chemical.ICrTChemicalStack.ICrTInfusionStack;
import mekanism.common.integration.crafttweaker.chemical.ICrTChemicalStack.ICrTPigmentStack;
import mekanism.common.integration.crafttweaker.chemical.ICrTChemicalStack.ICrTSlurryStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class CrTUtils {

    public static final Function<GasStack, ICrTGasStack> GAS_CONVERTER = CrTGasStack::new;
    public static final Function<InfusionStack, ICrTInfusionStack> INFUSION_CONVERTER = CrTInfusionStack::new;
    public static final Function<PigmentStack, ICrTPigmentStack> PIGMENT_CONVERTER = CrTPigmentStack::new;
    public static final Function<SlurryStack, ICrTSlurryStack> SLURRY_CONVERTER = CrTSlurryStack::new;

    /**
     * Creates a {@link ResourceLocation} in CraftTweaker's domain from the given path.
     *
     * @param path Path of the resource location
     *
     * @return Resource location in CraftTweaker's domain.
     */
    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MekanismHooks.CRAFTTWEAKER_MOD_ID, path);
    }

    /**
     * Helper to create an {@link ICrTGasStack} from a {@link Gas} with a stack size of one mB.
     */
    public static ICrTGasStack stackFromGas(Gas gas) {
        return new CrTGasStack(gas.getStack(1));
    }

    /**
     * Helper to create an {@link ICrTInfusionStack} from a {@link InfuseType} with a stack size of one mB.
     */
    public static ICrTInfusionStack stackFromInfuseType(InfuseType infuseType) {
        return new CrTInfusionStack(infuseType.getStack(1));
    }

    /**
     * Helper to create an {@link ICrTPigmentStack} from a {@link Pigment} with a stack size of one mB.
     */
    public static ICrTPigmentStack stackFromPigment(Pigment pigment) {
        return new CrTPigmentStack(pigment.getStack(1));
    }

    /**
     * Helper to create an {@link ICrTSlurryStack} from a {@link Slurry} with a stack size of one mB.
     */
    public static ICrTSlurryStack stackFromSlurry(Slurry slurry) {
        return new CrTSlurryStack(slurry.getStack(1));
    }

    /**
     * Helper method to convert a {@link BoxedChemicalStack} to an {@link ICrTChemicalStack}.
     *
     * @return {@link ICrTChemicalStack} representation of the given stack or {@code null} if empty.
     */
    @Nullable
    public static ICrTChemicalStack<?, ?, ?> fromBoxedStack(BoxedChemicalStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        return switch (stack.getChemicalType()) {
            case GAS -> new CrTGasStack((GasStack) stack.getChemicalStack());
            case INFUSION -> new CrTInfusionStack((InfusionStack) stack.getChemicalStack());
            case PIGMENT -> new CrTPigmentStack((PigmentStack) stack.getChemicalStack());
            case SLURRY -> new CrTSlurryStack((SlurryStack) stack.getChemicalStack());
        };
    }

    /**
     * Helper method to convert a {@link Chemical} to an {@link ICrTChemicalStack}.
     */
    @SuppressWarnings("unchecked")
    public static <CHEMICAL extends Chemical<CHEMICAL>, CRT_STACK extends ICrTChemicalStack<CHEMICAL, ?, CRT_STACK>> CRT_STACK fromChemical(CHEMICAL chemical, int size) {
        return (CRT_STACK) switch (chemical) {
            case Gas gas -> new CrTGasStack(gas.getStack(size));
            case InfuseType infuseType -> new CrTInfusionStack(infuseType.getStack(size));
            case Pigment pigment -> new CrTPigmentStack(pigment.getStack(size));
            case Slurry slurry -> new CrTSlurryStack(slurry.getStack(size));
            default -> throw new IllegalArgumentException("Unknown chemical type");
        };
    }

    /**
     * Converts a CrT item ingredient to one of ours.
     */
    public static ItemStackIngredient fromCrT(IIngredientWithAmount ingredient) {
        return IngredientCreatorAccess.item().from(ExpandIIngredientWithAmountNeoForge.asSizedIngredient(ingredient));
    }

    /**
     * Converts one of our item ingredients to a CrT item ingredient.
     */
    public static IIngredientWithAmount toCrT(ItemStackIngredient ingredient) {
        return ExpandSizedIngredient.asIIngredientWithAmount(ingredient.ingredient());
    }

    /**
     * Converts a CrT fluid ingredient to one of ours.
     */
    public static FluidStackIngredient fromCrT(CTFluidIngredient ingredient) {
        return IngredientCreatorAccess.fluid().from(ExpandCTFluidIngredientNeoForge.asSizedFluidIngredient(ingredient));
    }

    /**
     * Converts one of our fluid ingredients to a CrT fluid ingredient.
     */
    public static CTFluidIngredient toCrT(FluidStackIngredient ingredient) {
        return ExpandSizedFluidIngredient.asCTFluidIngredient(ingredient.ingredient());
    }

    /**
     * Helper method to get a single output from a recipe component if it is present.
     *
     * @param recipe    Decomposed recipe
     * @param component Recipe component
     *
     * @throws IllegalArgumentException if component is present but result is not single.
     */
    public static <C> Optional<C> getSingleIfPresent(IDecomposedRecipe recipe, IRecipeComponent<C> component) {
        List<C> values = recipe.get(component);
        if (values == null) {
            return Optional.empty();
        }
        if (values.size() != 1) {
            final String message = String.format(Locale.ROOT,
                  "Expected a list with a single element for %s, but got %d-sized list: %s",
                  component.getCommandString(),
                  values.size(),
                  values
            );
            throw new IllegalArgumentException(message);
        }
        return Optional.of(values.getFirst());
    }

    /**
     * Helper method to get a pair based output from a recipe component if it is present.
     *
     * @param recipe    Decomposed recipe
     * @param component Recipe component
     *
     * @throws IllegalArgumentException if component is not present or doesn't have two elements.
     */
    public static <C> UnaryTypePair<C> getPair(IDecomposedRecipe recipe, IRecipeComponent<C> component) {
        List<C> list = recipe.getOrThrow(component);
        if (list.size() != 2) {
            final String message = String.format(Locale.ROOT,
                  "Expected a list with two elements element for %s, but got %d-sized list: %s",
                  component.getCommandString(),
                  list.size(),
                  list
            );
            throw new IllegalArgumentException(message);
        }
        return new UnaryTypePair<>(list.get(0), list.get(1));
    }

    /**
     * Helper method for describing the outputs of a recipe that may have multiple outputs.
     */
    public static <CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>> String describeOutputs(List<STACK> outputs) {
        if (outputs.isEmpty()) {
            return "";
        }
        return describeOutputs(outputs, getConverter(outputs.getFirst()));
    }

    /**
     * Helper method for describing the outputs of a recipe that may have multiple outputs.
     */
    public static <TYPE> String describeOutputs(List<TYPE> outputs, Function<TYPE, ?> converter) {
        int size = outputs.size();
        if (size == 0) {
            return "";
        } else if (size == 1) {
            return converter.apply(outputs.getFirst()).toString();
        }
        //Note: This isn't the best way to describe multiple outputs, but it is probably as close as we can get
        StringBuilder description = new StringBuilder();
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                description.append(", or ");
            }
            description.append(converter.apply(outputs.get(i)));
        }
        return description.toString();
    }

    /**
     * Helper method for describing the outputs of a recipe that may have multiple outputs.
     */
    public static String describeOutputs(long[] outputs) {
        int size = outputs.length;
        if (size == 0) {
            return "";
        } else if (size == 1) {
            return Long.toString(outputs[0]);
        }
        //Note: This isn't the best way to describe multiple outputs, but it is probably as close as we can get
        StringBuilder description = new StringBuilder();
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                description.append(", or ");
            }
            description.append(outputs[i]);
        }
        return description.toString();
    }

    /**
     * Helper to convert a CraftTweaker type tag to a regular tag and validate it exists
     */
    public static <TYPE> TagKey<TYPE> validateTagAndGet(KnownTag<TYPE> tag) {
        if (tag.exists()) {
            return tag.getTagKey();
        }
        throw new IllegalArgumentException("Tag " + tag.getCommandString() + " does not exist.");
    }

    /**
     * Helper to convert a list of one type to a list of another.
     */
    public static <TYPE, CRT_TYPE> List<CRT_TYPE> convert(List<TYPE> elements, Function<TYPE, CRT_TYPE> converter) {
        return elements.stream().map(converter).toList();
    }

    /**
     * Helper to get a function that converts a chemical stack into the corresponding CraftTweaker chemical stack.
     */
    @SuppressWarnings("unchecked")
    private static <CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>, CRT_STACK extends ICrTChemicalStack<CHEMICAL, STACK, CRT_STACK>>
    Function<STACK, CRT_STACK> getConverter(STACK stack) {
        return (Function<STACK, CRT_STACK>) switch (ChemicalType.getTypeFor(stack)) {
            case GAS -> GAS_CONVERTER;
            case INFUSION -> INFUSION_CONVERTER;
            case PIGMENT -> PIGMENT_CONVERTER;
            case SLURRY -> SLURRY_CONVERTER;
        };
    }

    /**
     * Helper to convert a list of chemicals to a list of crafttweaker chemicals.
     */
    public static <CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>, CRT_STACK extends ICrTChemicalStack<CHEMICAL, STACK, CRT_STACK>>
    List<CRT_STACK> convertChemical(List<STACK> elements) {
        if (elements.isEmpty()) {
            return Collections.emptyList();
        }
        return convert(elements, CrTUtils.<CHEMICAL, STACK, CRT_STACK>getConverter(elements.getFirst()));
    }

    /**
     * Helper to convert a list of items to a list of crafttweaker items.
     */
    public static List<IItemStack> convertItems(List<ItemStack> elements) {
        return convert(elements, IItemStack::of);
    }

    /**
     * Helper to convert a list of items to a list of crafttweaker items.
     */
    public static List<IFluidStack> convertFluids(List<FluidStack> elements) {
        return convert(elements, IFluidStack::of);
    }

    /**
     * Helper to get CraftTweaker's item tag manager.
     */
    public static KnownTagManager<Item> itemTags() {
        return CraftTweakerTagRegistry.INSTANCE.knownTagManager(Registries.ITEM);
    }

    /**
     * Helper to get CraftTweaker's fluid tag manager.
     */
    public static KnownTagManager<Fluid> fluidTags() {
        return CraftTweakerTagRegistry.INSTANCE.knownTagManager(Registries.FLUID);
    }

    /**
     * Helper to get CraftTweaker's gas tag manager.
     */
    public static KnownTagManager<Gas> gasTags() {
        return CraftTweakerTagRegistry.INSTANCE.knownTagManager(MekanismAPI.GAS_REGISTRY_NAME);
    }

    /**
     * Helper to get CraftTweaker's infuse type tag manager.
     */
    public static KnownTagManager<InfuseType> infuseTypeTags() {
        return CraftTweakerTagRegistry.INSTANCE.knownTagManager(MekanismAPI.INFUSE_TYPE_REGISTRY_NAME);
    }

    /**
     * Helper to get CraftTweaker's pigment tag manager.
     */
    public static KnownTagManager<Pigment> pigmentTags() {
        return CraftTweakerTagRegistry.INSTANCE.knownTagManager(MekanismAPI.PIGMENT_REGISTRY_NAME);
    }

    /**
     * Helper to get CraftTweaker's slurry tag manager.
     */
    public static KnownTagManager<Slurry> slurryTags() {
        return CraftTweakerTagRegistry.INSTANCE.knownTagManager(MekanismAPI.SLURRY_REGISTRY_NAME);
    }

    public record UnaryTypePair<TYPE>(TYPE a, TYPE b) {
    }
}