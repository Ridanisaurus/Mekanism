package mekanism.common.integration.crafttweaker.recipe.handler;

import com.blamejared.crafttweaker.api.bracket.CommandStringDisplayable;
import com.blamejared.crafttweaker.api.fluid.CTFluidIngredient;
import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import com.blamejared.crafttweaker.api.ingredient.IIngredientWithAmount;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.recipe.component.BuiltinRecipeComponents;
import com.blamejared.crafttweaker.api.recipe.component.DecomposedRecipeBuilder;
import com.blamejared.crafttweaker.api.recipe.component.IDecomposedRecipe;
import com.blamejared.crafttweaker.api.recipe.component.IRecipeComponent;
import com.blamejared.crafttweaker.api.recipe.handler.IRecipeHandler;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.blamejared.crafttweaker.api.tag.manager.type.KnownTagManager;
import com.blamejared.crafttweaker.api.tag.type.KnownTag;
import com.blamejared.crafttweaker.api.util.ItemStackUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.ChemicalType;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.merged.BoxedChemicalStack;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.recipes.ElectrolysisRecipe.ElectrolysisRecipeOutput;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.PressurizedReactionRecipe.PressurizedReactionRecipeOutput;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.FluidStackIngredient;
import mekanism.api.recipes.ingredients.GasStackIngredient;
import mekanism.api.recipes.ingredients.InfusionStackIngredient;
import mekanism.api.recipes.ingredients.InputIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import mekanism.api.recipes.ingredients.PigmentStackIngredient;
import mekanism.api.recipes.ingredients.SlurryStackIngredient;
import mekanism.api.recipes.ingredients.chemical.IChemicalIngredient;
import mekanism.api.recipes.ingredients.chemical.TagChemicalIngredient;
import mekanism.common.integration.crafttweaker.CrTConstants;
import mekanism.common.integration.crafttweaker.CrTRecipeComponents;
import mekanism.common.integration.crafttweaker.CrTRecipeComponents.ChemicalRecipeComponent;
import mekanism.common.integration.crafttweaker.CrTUtils;
import mekanism.common.integration.crafttweaker.chemical.CrTChemicalStack.CrTGasStack;
import mekanism.common.integration.crafttweaker.chemical.CrTChemicalStack.CrTInfusionStack;
import mekanism.common.integration.crafttweaker.chemical.CrTChemicalStack.CrTPigmentStack;
import mekanism.common.integration.crafttweaker.chemical.CrTChemicalStack.CrTSlurryStack;
import mekanism.common.integration.crafttweaker.chemical.ICrTChemicalStack;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;

public abstract class MekanismRecipeHandler<RECIPE extends MekanismRecipe<?>> implements IRecipeHandler<RECIPE> {

    protected static final Object SKIP_OPTIONAL_PARAM = new Object();

    @Override
    public abstract <U extends Recipe<?>> boolean doesConflict(final IRecipeManager<? super RECIPE> manager, final RECIPE recipe, final U other);

    protected <TYPE, INGREDIENT extends InputIngredient<TYPE>> boolean ingredientConflicts(INGREDIENT a, INGREDIENT b) {
        return a.getRepresentations().stream().anyMatch(b::testType);
    }

    @SuppressWarnings("unchecked")
    protected <CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>> boolean chemicalIngredientConflicts(ChemicalStackIngredient<CHEMICAL, STACK, ?> a,
          ChemicalStackIngredient<?, ?, ?> b) {
        //If types of inputs match then check if they conflict
        return ChemicalType.getTypeFor(a) == ChemicalType.getTypeFor(b) && ingredientConflicts(a, (ChemicalStackIngredient<CHEMICAL, STACK, ?>) b);
    }

    protected String buildCommandString(IRecipeManager<? super RECIPE> manager, RecipeHolder<RECIPE> recipe, Object... params) {
        return buildCommandString(manager, "addRecipe", recipe, params);
    }

    protected String buildCommandString(IRecipeManager<? super RECIPE> manager, String method, RecipeHolder<RECIPE> recipe, Object... params) {
        StringBuilder commandString = new StringBuilder(manager.getCommandString())
              .append('.')
              .append(method)
              .append("(\"")
              //Note: Uses path rather than entire location as we only allow adding recipes to the CrT namespace
              .append(recipe.id().getPath())
              .append('"');
        for (Object param : params) {
            if (param != SKIP_OPTIONAL_PARAM) {
                commandString.append(", ")
                      .append(convertParam(param));
            }
        }
        return commandString.append(");").toString();
    }

    /**
     * Super simplified/watered down version of BaseCrTExampleProvider#getConversionRepresentations
     */
    private String convertParam(Object param) {
        if (param instanceof CommandStringDisplayable displayable) {
            return displayable.getCommandString();
        } else if (param instanceof ItemStack stack) {
            return ItemStackUtil.getCommandString(stack);
        } else if (param instanceof FluidStack stack) {
            return IFluidStack.of(stack).getCommandString();
        } else if (param instanceof GasStack stack) {
            return new CrTGasStack(stack).getCommandString();
        } else if (param instanceof InfusionStack stack) {
            return new CrTInfusionStack(stack).getCommandString();
        } else if (param instanceof PigmentStack stack) {
            return new CrTPigmentStack(stack).getCommandString();
        } else if (param instanceof SlurryStack stack) {
            return new CrTSlurryStack(stack).getCommandString();
        } else if (param instanceof BoxedChemicalStack stack) {
            return convertParam(stack.getChemicalStack());
        } else if (param instanceof Number || param instanceof Boolean) {//Handle integers and the like
            return param.toString();
        } else if (param instanceof ItemStackIngredient ingredient) {
            return convertParam(CrTUtils.toCrT(ingredient));
        } else if (param instanceof FluidStackIngredient ingredient) {
            return convertParam(CrTUtils.toCrT(ingredient));
        } else if (param instanceof GasStackIngredient ingredient) {
            return convertIngredient(CrTConstants.CLASS_GAS_STACK_INGREDIENT, CrTUtils.gasTags(), ingredient.ingredient(), ingredient.amount());
        } else if (param instanceof InfusionStackIngredient ingredient) {
            return convertIngredient(CrTConstants.CLASS_INFUSION_STACK_INGREDIENT, CrTUtils.infuseTypeTags(), ingredient.ingredient(), ingredient.amount());
        } else if (param instanceof PigmentStackIngredient ingredient) {
            return convertIngredient(CrTConstants.CLASS_PIGMENT_STACK_INGREDIENT, CrTUtils.pigmentTags(), ingredient.ingredient(), ingredient.amount());
        } else if (param instanceof SlurryStackIngredient ingredient) {
            return convertIngredient(CrTConstants.CLASS_SLURRY_STACK_INGREDIENT, CrTUtils.slurryTags(), ingredient.ingredient(), ingredient.amount());
        } else if (param instanceof List<?> list) {
            if (list.isEmpty()) {
                //Shouldn't happen
                return "Invalid (output) list, no outputs";
            }
            //Outputs sometimes are as lists, try wrapping them into a single element
            // eventually we may want to try listing them all somehow?
            return convertParam(list.getFirst());
        } else if (param instanceof long[] longs) {
            if (longs.length == 0) {
                //Shouldn't happen
                return "Invalid (output) array, no outputs";
            }
            //Outputs sometimes are as arrays, try wrapping them into a single element
            // eventually we may want to try listing them all somehow?
            return convertParam(longs[0]);
        } else if (param instanceof ElectrolysisRecipeOutput output) {
            return convertParam(output.left()) + ", " + convertParam(output.right());
        }
        //Shouldn't happen
        return "Unimplemented: " + param;
    }

    private static <CHEMICAL extends Chemical<CHEMICAL>, INGREDIENT extends IChemicalIngredient<CHEMICAL, INGREDIENT>,
          CRT_STACK extends ICrTChemicalStack<CHEMICAL, ?, CRT_STACK>> String convertIngredient(String crtClass, KnownTagManager<CHEMICAL> tagManager,
          INGREDIENT ingredient, long amount) {
        if (ingredient instanceof TagChemicalIngredient<?, ?> tagIngredient) {
            KnownTag<CHEMICAL> tag = tagManager.tag(tagIngredient.tag());
            if (amount == 1) {
                return tag.getCommandString();
            } else if (amount > 0 && amount <= Integer.MAX_VALUE) {
                return tag.withAmount((int) amount).getCommandString();
            }
            //Tag with amount can only handle up to max int, so we have to do it explicitly if we have more
            return crtClass + ".from(" + tag.getCommandString() + ", " + amount + ")";
        }
        List<CRT_STACK> list = new ArrayList<>();
        for (CHEMICAL chemical : ingredient.getChemicals()) {
            list.add(CrTUtils.fromChemical(chemical, 1));
        }
        if (list.size() == 1) {
            return list.getFirst().setAmount(amount).getCommandString();
        }
        String representation = list.stream()
              .map(ICrTChemicalStack::getCommandString)
              .collect(Collectors.joining(", "));
        return crtClass + ".from(" + amount + ", " + representation + ")";
    }

    /**
     * Helper to generically decompose data into the proper recipe components.
     */
    protected Optional<IDecomposedRecipe> decompose(Object... importantData) {
        TypeData<IIngredientWithAmount, CTFluidIngredient, ChemicalStackIngredient<?, ?, ?>> inputs = new TypeData<>(ChemicalType::getTypeFor);
        TypeData<IItemStack, IFluidStack, ChemicalStack<?>> outputs = new TypeData<>(ChemicalType::getTypeFor);
        int duration = -1;
        long energy = -1;
        for (Object data : importantData) {
            if (data instanceof List<?> dataList) {
                if (dataList.size() != 1) {
                    //Failed, output lists must be of length one or handled manually instead of using this helper
                    return Optional.empty();
                }
                //Update data to be the element
                data = dataList.getFirst();
            } else if (data instanceof long[] longs) {
                if (longs.length != 1) {
                    //Failed, output arrays must be of length one or handled manually instead of using this helper
                    return Optional.empty();
                }
                //Update data to be the element
                data = longs[0];
            }
            if (data instanceof ItemStackIngredient ingredient) {
                inputs.addItem(CrTUtils.toCrT(ingredient));
            } else if (data instanceof FluidStackIngredient ingredient) {
                inputs.addFluid(CrTUtils.toCrT(ingredient));
            } else if (data instanceof ChemicalStackIngredient<?, ?, ?> ingredient) {
                inputs.addChemical(ingredient);
            } else if (data instanceof ItemStack stack) {
                outputs.addItem(IItemStack.of(stack));
            } else if (data instanceof FluidStack stack) {
                outputs.addFluid(IFluidStack.of(stack));
            } else if (data instanceof ChemicalStack<?> stack) {
                outputs.addChemical(stack);
            } else if (data instanceof BoxedChemicalStack stack) {
                outputs.addChemical(stack.getChemicalStack());
            } else if (data instanceof PressurizedReactionRecipeOutput output) {
                if (!output.item().isEmpty()) {
                    outputs.addItem(IItemStack.of(output.item()));
                }
                if (!output.gas().isEmpty()) {
                    outputs.addChemical(output.gas());
                }
            } else if (data instanceof ElectrolysisRecipeOutput output) {
                outputs.addChemical(output.left());
                outputs.addChemical(output.right());
            } else if (data instanceof Integer i) {
                if (duration != -1) {
                    //Fail if we have multiple durations specified
                    return Optional.empty();
                }
                duration = i;
            } else if (data instanceof Long l) {
                if (energy != -1) {
                    //Fail if we have multiple energy values specified
                    return Optional.empty();
                }
                energy = l;
            } else {
                //Fail if we have important data we don't know how to handle
                return Optional.empty();
            }
        }
        DecomposedRecipeBuilder builder = IDecomposedRecipe.builder();
        inputs.addItemToBuilder(builder, CrTRecipeComponents.ITEM.input())
              .addFluidToBuilder(builder, CrTRecipeComponents.FLUID.input());
        outputs.addItemToBuilder(builder, CrTRecipeComponents.ITEM.output())
              .addFluidToBuilder(builder, CrTRecipeComponents.FLUID.output());
        for (ChemicalRecipeComponent<?, ?, ?, ?> chemicalComponent : CrTRecipeComponents.CHEMICAL_COMPONENTS) {
            addChemicals(builder, inputs, outputs, chemicalComponent);
        }
        if (duration != -1) {
            builder.with(BuiltinRecipeComponents.Processing.TIME, duration);
        }
        if (energy != -1) {
            builder.with(CrTRecipeComponents.ENERGY, energy);
        }
        return Optional.of(builder.build());
    }

    private <STACK extends ChemicalStack<?>, INGREDIENT extends ChemicalStackIngredient<?, STACK, ?>, CRT_STACK extends ICrTChemicalStack<?, STACK, CRT_STACK>>
    void addChemicals(DecomposedRecipeBuilder builder, TypeData<?, ?, ChemicalStackIngredient<?, ?, ?>> inputs, TypeData<?, ?, ChemicalStack<?>> outputs,
          ChemicalRecipeComponent<?, STACK, INGREDIENT, CRT_STACK> component) {
        List<INGREDIENT> data = (List<INGREDIENT>) inputs.chemicalData.getOrDefault(component.chemicalType(), Collections.emptyList());
        if (!data.isEmpty()) {
            builder.with(component.input(), data);
        }
        List<STACK> outputData = (List<STACK>) outputs.chemicalData.getOrDefault(component.chemicalType(), Collections.emptyList());
        if (!outputData.isEmpty()) {
            component.withOutput(builder, outputData);
        }
    }

    private static class TypeData<ITEM, FLUID, CHEMICAL> {

        private final List<ITEM> itemData = new ArrayList<>();
        private final List<FLUID> fluidData = new ArrayList<>();
        private final Map<ChemicalType, List<CHEMICAL>> chemicalData = new EnumMap<>(ChemicalType.class);
        private final Function<CHEMICAL, ChemicalType> typeExtractor;

        public TypeData(Function<CHEMICAL, ChemicalType> typeExtractor) {
            this.typeExtractor = typeExtractor;
        }

        private void addItem(ITEM data) {
            itemData.add(data);
        }

        private void addFluid(FLUID data) {
            fluidData.add(data);
        }

        private void addChemical(CHEMICAL data) {
            chemicalData.computeIfAbsent(typeExtractor.apply(data), type -> new ArrayList<>()).add(data);
        }

        private TypeData<ITEM, FLUID, CHEMICAL> addItemToBuilder(DecomposedRecipeBuilder builder, IRecipeComponent<ITEM> component) {
            if (!itemData.isEmpty()) {
                builder.with(component, itemData);
            }
            return this;
        }

        private TypeData<ITEM, FLUID, CHEMICAL> addFluidToBuilder(DecomposedRecipeBuilder builder, IRecipeComponent<FLUID> component) {
            if (!fluidData.isEmpty()) {
                builder.with(component, fluidData);
            }
            return this;
        }
    }
}