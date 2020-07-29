package io.github.thebusybiscuit.slimefun4.implementation.items.electric;

import io.github.thebusybiscuit.cscorelib2.chat.ChatColors;
import io.github.thebusybiscuit.cscorelib2.math.DoubleHandler;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetProvider;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.reactors.Reactor;
import io.github.thebusybiscuit.slimefun4.utils.NumberUtils;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AGenerator;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineFuel;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.interfaces.InventoryBlock;
import me.mrCookieSlime.Slimefun.Objects.handlers.GeneratorTicker;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This is an abstract super class for machines that produce energy.
 *
 * @author TheBusyBiscuit
 * @see AGenerator
 * @see Reactor
 */
public abstract class AbstractEnergyProvider extends SlimefunItem implements InventoryBlock, RecipeDisplayItem, EnergyNetProvider {

    protected final Set<MachineFuel> fuelTypes = new HashSet<>();

    protected AbstractEnergyProvider(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    /**
     * This method returns the title that is used for the {@link Inventory} of an
     * {@link AGenerator} that has been opened by a Player.
     *
     * Override this method to set the title.
     *
     * @return The title of the {@link Inventory} of this {@link AGenerator}
     */
    public abstract String getInventoryTitle();

    /**
     * This method returns the {@link ItemStack} that this {@link AGenerator} will
     * use as a progress bar.
     *
     * Override this method to set the progress bar.
     *
     * @return The {@link ItemStack} to use as the progress bar
     */
    public abstract ItemStack getProgressBar();

    /**
     * This method returns the amount of energy that is produced per tick.
     *
     * @return The rate of energy generation
     */
    public abstract int getEnergyProduction();

    /**
     * This method is used to register the default fuel types.
     */
    protected abstract void registerDefaultFuelTypes();

    @Override
    public EnergyNetComponentType getEnergyComponentType() {
        return EnergyNetComponentType.GENERATOR;
    }

    /**
     * @return A {@link GeneratorTicker}
     * @deprecated Please implement the methods
     * {@link #getGeneratedOutput(org.bukkit.Location, me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config)}
     * and {@link #willExplode(org.bukkit.Location, me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config)}
     * instead
     */
    @Deprecated
    protected GeneratorTicker onTick() {
        return null;
    }

    @Override
    public int getGeneratedOutput(Location l, Config data) {
        if (generatorTicker != null) {
            return (int) generatorTicker.generateEnergy(l, this, data);
        } else {
            return 0;
        }
    }

    @Override
    public boolean willExplode(Location l, Config data) {
        if (generatorTicker != null) {
            return generatorTicker.explode(l);
        } else {
            return false;
        }
    }

    @Override
    public void preRegister() {
        super.preRegister();

        GeneratorTicker ticker = onTick();

        if (ticker != null) {
            addItemHandler(ticker);
        }
    }

    public void registerFuel(MachineFuel fuel) {
        fuelTypes.add(fuel);
    }

    public Set<MachineFuel> getFuelTypes() {
        return this.fuelTypes;
    }

    @Override
    public String getLabelLocalPath() {
        return "guide.tooltips.recipes.generator";
    }

    @Override
    public List<ItemStack> getDisplayRecipes() {
        List<ItemStack> list = new ArrayList<>();

        for (MachineFuel fuel : fuelTypes) {
            ItemStack item = fuel.getInput().clone();
            ItemMeta im = item.getItemMeta();
            List<String> lore = new ArrayList<>();
            lore.add(ChatColors.color("&8\u21E8 &7持续时间 " + NumberUtils.getTimeLeft(fuel.getTicks() / 2)));
            lore.add(ChatColors.color("&8\u21E8 &e\u26A1 &7" + getEnergyProduction() * 2) + " J/s");
            lore.add(ChatColors.color("&8\u21E8 &e\u26A1 &7最大储存量: " + DoubleHandler.getFancyDouble((double) fuel.getTicks() * getEnergyProduction()) + " J"));
            im.setLore(lore);
            item.setItemMeta(im);
            list.add(item);
        }

        return list;
    }

}