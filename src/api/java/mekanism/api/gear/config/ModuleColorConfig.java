package mekanism.api.gear.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import mekanism.api.NBTConstants;
import mekanism.api.annotations.NothingNullByDefault;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

//TODO - 1.20.5: Docs
@NothingNullByDefault
public class ModuleColorConfig extends ModuleConfig<Integer> {

    public static final Codec<ModuleColorConfig> ARGB_CODEC = RecordCodecBuilder.create(instance -> baseCodec(instance)
          .and(Codec.INT.fieldOf(NBTConstants.VALUE).forGetter(ModuleConfig::get))
          .apply(instance, ModuleColorConfig::argb));
    public static final StreamCodec<ByteBuf, ModuleColorConfig> ARGB_STREAM_CODEC = StreamCodec.composite(
          ByteBufCodecs.STRING_UTF8, ModuleConfig::name,
          //Note: We don't do varint as we include alpha data
          ByteBufCodecs.INT, ModuleConfig::get,
          ModuleColorConfig::argb
    );
    public static final Codec<ModuleColorConfig> RGB_CODEC = RecordCodecBuilder.create(instance -> baseCodec(instance)
          .and(Codec.INT.fieldOf(NBTConstants.VALUE).forGetter(ModuleConfig::get))
          //If we don't handle alpha make sure we do have the alpha component present
          .apply(instance, ModuleColorConfig::rgb));
    public static final StreamCodec<ByteBuf, ModuleColorConfig> RGB_STREAM_CODEC = StreamCodec.composite(
          ByteBufCodecs.STRING_UTF8, ModuleConfig::name,
          //Note: We can use var int here and just not send the alpha data over the network
          ByteBufCodecs.VAR_INT, module -> module.get() & 0x00FFFFFF,
          ModuleColorConfig::rgb
    );

    /**
     * Creates a new {@link ModuleColorConfig} that supports alpha and has a default value of white ({@code 0xFFFFFFFF}).
     *
     * @implNote Color format is ARGB.
     */
    public static ModuleColorConfig argb(String name) {
        return argb(name, 0xFFFFFFFF);
    }

    /**
     * Creates a new {@link ModuleColorData} that supports alpha and has the given default color.
     *
     * @param defaultColor Default color.
     *
     * @implNote Color format is ARGB.
     */
    public static ModuleColorConfig argb(String name, int defaultColor) {
        return new ModuleColorConfig(name, true, defaultColor);
    }

    /**
     * Creates a new {@link ModuleColorData} that doesn't support alpha and has a default value of white ({@code 0xFFFFFFFF}).
     *
     * @implNote Color format is ARGB with the alpha component being locked to {@code 0xFF}.
     */
    public static ModuleColorConfig rgb(String name) {
        return rgb(name, 0xFFFFFFFF);
    }

    /**
     * Creates a new {@link ModuleColorData} that doesn't support alpha and has the given default color.
     *
     * @param defaultColor Default color.
     *
     * @implNote Color format is ARGB with the alpha component being locked to {@code 0xFF}.
     */
    public static ModuleColorConfig rgb(String name, int defaultColor) {
        //If we don't handle alpha make sure we do have the alpha component present
        return new ModuleColorConfig(name, false, defaultColor);
    }

    private final boolean supportsAlpha;
    private final int value;

    private ModuleColorConfig(String name, boolean supportsAlpha, int value) {
        super(name);
        this.supportsAlpha = supportsAlpha;
        //If we don't handle alpha make sure we do have the alpha component present though
        this.value = this.supportsAlpha ? value : value | 0xFF000000;
    }

    public boolean supportsAlpha() {
        return supportsAlpha;
    }

    @Override
    public StreamCodec<ByteBuf, ModuleConfig<Integer>> namedStreamCodec(String name) {
        if (supportsAlpha) {
            //Note: We don't do varint as we include alpha data
            return ByteBufCodecs.INT.map(val -> ModuleColorConfig.argb(name, val), ModuleConfig::get);
        }
        //Note: We can use var int here and just not send the alpha data over the network
        return ByteBufCodecs.VAR_INT.map(val -> ModuleColorConfig.rgb(name, val), module -> module.get() & 0x00FFFFFF);
    }

    @Override
    public Integer get() {
        return value;
    }

    @Override
    public ModuleColorConfig with(Integer value) {
        Objects.requireNonNull(value, "Value cannot be null.");
        int sanitizedValue = this.supportsAlpha ? value : value | 0xFF000000;
        return this.value == sanitizedValue ? this : new ModuleColorConfig(name(), supportsAlpha, sanitizedValue);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!super.equals(o)) {
            return false;
        }
        ModuleColorConfig other = (ModuleColorConfig) o;
        return supportsAlpha == other.supportsAlpha && value == other.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), supportsAlpha, value);
    }
}