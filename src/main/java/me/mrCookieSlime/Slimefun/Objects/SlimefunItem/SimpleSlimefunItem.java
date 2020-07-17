package me.mrCookieSlime.Slimefun.Objects.SlimefunItem;

import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.handlers.ItemHandler;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import org.bukkit.inventory.ItemStack;

/**
 * @param <T> deprecated.
 * @deprecated Moved to {@link io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem}
 */
@Deprecated
public abstract class SimpleSlimefunItem<T extends ItemHandler> extends io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem<T> {

    public SimpleSlimefunItem(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    public SimpleSlimefunItem(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, ItemStack recipeOutput) {
        super(category, item, recipeType, recipe, recipeOutput);
    }

}