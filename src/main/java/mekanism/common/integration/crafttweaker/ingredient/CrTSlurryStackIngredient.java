package mekanism.common.integration.crafttweaker.ingredient;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.data.IData;
import com.blamejared.crafttweaker.api.data.op.IDataOps;
import com.blamejared.crafttweaker.api.tag.type.KnownTag;
import com.blamejared.crafttweaker.api.util.Many;
import com.blamejared.crafttweaker_annotations.annotations.NativeTypeRegistration;
import java.util.ArrayList;
import java.util.List;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.recipes.ingredients.SlurryStackIngredient;
import mekanism.api.recipes.ingredients.chemical.ISlurryIngredient;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.common.integration.crafttweaker.CrTConstants;
import mekanism.common.integration.crafttweaker.CrTUtils;
import mekanism.common.integration.crafttweaker.chemical.ICrTChemicalStack.ICrTSlurryStack;
import net.minecraft.tags.TagKey;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@NativeTypeRegistration(value = SlurryStackIngredient.class, zenCodeName = CrTConstants.CLASS_SLURRY_STACK_INGREDIENT)
public class CrTSlurryStackIngredient {

    private CrTSlurryStackIngredient() {
    }

    /**
     * Creates a {@link SlurryStackIngredient} that matches a given slurry and amount.
     *
     * @param instance Slurry to match
     * @param amount   Amount needed
     *
     * @return A {@link SlurryStackIngredient} that matches a given slurry and amount.
     */
    @ZenCodeType.StaticExpansionMethod
    public static SlurryStackIngredient from(Slurry instance, long amount) {
        CrTIngredientHelper.assertValid(instance, amount, "SlurryStackIngredients", "slurry");
        return IngredientCreatorAccess.slurryStack().from(instance, amount);
    }

    /**
     * Creates a {@link SlurryStackIngredient} that matches a given slurry stack.
     *
     * @param instance Slurry stack to match
     *
     * @return A {@link SlurryStackIngredient} that matches a given slurry stack.
     */
    @ZenCodeType.StaticExpansionMethod
    public static SlurryStackIngredient from(ICrTSlurryStack instance) {
        CrTIngredientHelper.assertValid(instance, "SlurryStackIngredients");
        return IngredientCreatorAccess.slurryStack().from(instance.getImmutableInternal());
    }

    /**
     * Creates a {@link SlurryStackIngredient} that matches the given slurries and amount.
     *
     * @param amount   Amount needed
     * @param slurries Slurries to match
     *
     * @return A {@link SlurryStackIngredient} that matches the given slurries and amount.
     */
    @ZenCodeType.StaticExpansionMethod
    public static SlurryStackIngredient from(long amount, Slurry... slurries) {
        CrTIngredientHelper.assertMultiple(amount, "SlurryStackIngredients", "slurry", slurries);
        return IngredientCreatorAccess.slurryStack().from(amount, slurries);
    }

    /**
     * Creates a {@link SlurryStackIngredient} that matches the given slurries and amount.
     *
     * @param amount   Amount needed
     * @param slurries Slurries to match
     *
     * @return A {@link SlurryStackIngredient} that matches the given slurries and amount.
     */
    @ZenCodeType.StaticExpansionMethod
    public static SlurryStackIngredient from(long amount, ICrTSlurryStack... slurries) {
        CrTIngredientHelper.assertMultiple(amount, "SlurryStackIngredients", "slurry", slurries);
        return IngredientCreatorAccess.slurryStack().from(amount, slurries);
    }

    /**
     * Creates a {@link SlurryStackIngredient} that matches the given slurry stacks. The first stack's size will be used for this ingredient.
     *
     * @param slurries Slurry stacks to match
     *
     * @return A {@link SlurryStackIngredient} that matches a given slurry stack.
     */
    @ZenCodeType.StaticExpansionMethod
    public static SlurryStackIngredient from(ICrTSlurryStack... slurries) {
        long amount = CrTIngredientHelper.assertMultiple("SlurryStackIngredients", "slurry", slurries);
        return IngredientCreatorAccess.slurryStack().from(amount, slurries);
    }

    /**
     * Creates a {@link SlurryStackIngredient} that matches a given slurry tag with a given amount.
     *
     * @param slurryTag Tag to match
     * @param amount    Amount needed
     *
     * @return A {@link SlurryStackIngredient} that matches a given slurry tag with a given amount.
     */
    @ZenCodeType.StaticExpansionMethod
    public static SlurryStackIngredient from(KnownTag<Slurry> slurryTag, long amount) {
        TagKey<Slurry> tag = CrTIngredientHelper.assertValidAndGet(slurryTag, amount, "SlurryStackIngredients");
        return IngredientCreatorAccess.slurryStack().from(tag, amount);
    }

    /**
     * Creates a {@link SlurryStackIngredient} that matches a given slurry tag with amount.
     *
     * @param slurryTag Tag and amount to match
     *
     * @return A {@link SlurryStackIngredient} that matches a given slurry tag with amount.
     */
    @ZenCodeType.StaticExpansionMethod
    public static SlurryStackIngredient from(Many<KnownTag<Slurry>> slurryTag) {
        return from(slurryTag.getData(), slurryTag.getAmount());
    }

    /**
     * Converts this {@link SlurryStackIngredient} into JSON ({@link IData}).
     *
     * @return {@link SlurryStackIngredient} as JSON.
     */
    @ZenCodeType.Method
    @ZenCodeType.Caster(implicit = true)
    public static IData asIData(SlurryStackIngredient _this) {
        return IngredientCreatorAccess.slurryStack().codec().encodeStart(IDataOps.INSTANCE, _this).getOrThrow();
    }

    /**
     * Checks if a given {@link ICrTSlurryStack} has a type match for this {@link SlurryStackIngredient}. Type matches ignore stack size.
     *
     * @param type Type to check for a match
     *
     * @return {@code true} if the type is supported by this {@link SlurryStackIngredient}.
     */
    @ZenCodeType.Method
    public static boolean testType(SlurryStackIngredient _this, ICrTSlurryStack type) {
        return _this.testType(type.getInternal());
    }

    /**
     * Checks if a given {@link ICrTSlurryStack} matches this {@link SlurryStackIngredient}. (Checks size for >=)
     *
     * @param stack Stack to check for a match
     *
     * @return {@code true} if the stack fulfills the requirements for this {@link SlurryStackIngredient}.
     */
    @ZenCodeType.Method
    public static boolean test(SlurryStackIngredient _this, ICrTSlurryStack stack) {
        return _this.test(stack.getInternal());
    }

    /**
     * Gets a list of valid instances for this {@link SlurryStackIngredient}, may not include all or may be empty depending on how complex the ingredient is as the
     * internal version is mostly used for JEI display purposes.
     */
    @ZenCodeType.Method
    @ZenCodeType.Getter("representations")
    public static List<ICrTSlurryStack> getRepresentations(SlurryStackIngredient _this) {
        return CrTUtils.convertChemical(_this.getRepresentations());
    }

    /**
     * OR's this {@link SlurryStackIngredient} with another {@link SlurryStackIngredient} to create a multi {@link SlurryStackIngredient}
     *
     * @param other {@link SlurryStackIngredient} to combine with.
     *
     * @return Multi {@link SlurryStackIngredient} that matches both the source {@link SlurryStackIngredient} and the OR'd {@link SlurryStackIngredient}.
     */
    @ZenCodeType.Method
    @ZenCodeType.Operator(ZenCodeType.OperatorType.OR)
    public static SlurryStackIngredient or(SlurryStackIngredient _this, SlurryStackIngredient other) {
        if (_this.amount() != other.amount()) {
            throw new IllegalArgumentException("SlurryStack ingredients can only be or'd if they have the same counts");
        }
        List<ISlurryIngredient> ingredients = new ArrayList<>();
        CrTIngredientHelper.addIngredient(ingredients, _this.ingredient());
        CrTIngredientHelper.addIngredient(ingredients, other.ingredient());
        return IngredientCreatorAccess.slurryStack().from(IngredientCreatorAccess.slurry().ofIngredients(ingredients), _this.amount());
    }
}