package io.github.thebusybiscuit.slimefun4.implementation.items.magical;

import io.github.thebusybiscuit.slimefun4.core.handlers.ItemDropHandler;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.Slimefun;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This {@link SlimefunItem} allows you to enchant any enchantable {@link ItemStack} with a random
 * {@link Enchantment}. It is also one of the very few utilisations of {@link ItemDropHandler}.
 *
 * @author Linox
 * @see ItemDropHandler
 */
public class EnchantmentRune extends SimpleSlimefunItem<ItemDropHandler> {

    private static final double RANGE = 1.5;
    private final Map<Material, List<Enchantment>> applicableEnchantments = new EnumMap<>(Material.class);

    public EnchantmentRune(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        for (Material mat : Material.values()) {
            List<Enchantment> enchantments = new ArrayList<>();

            for (Enchantment enchantment : Enchantment.values()) {
                if (enchantment == Enchantment.BINDING_CURSE || enchantment == Enchantment.VANISHING_CURSE) {
                    continue;
                }

                if (enchantment.canEnchantItem(new ItemStack(mat))) {
                    enchantments.add(enchantment);
                }
            }

            applicableEnchantments.put(mat, enchantments);
        }
    }

    @Override
    public ItemDropHandler getItemHandler() {
        return (e, p, item) -> {
            if (isItem(item.getItemStack())) {
                if (Slimefun.hasUnlocked(p, this, true)) {
                    Slimefun.runSync(() -> {
                        try {
                            addRandomEnchantment(p, e, item);
                        } catch (Exception x) {
                            error("An Exception occured while trying to apply an Enchantment Rune", x);
                        }
                    }, 20L);
                }

                return true;
            }

            return false;
        };
    }

    private void addRandomEnchantment(Player p, PlayerDropItemEvent e, Item item) {
        // Being sure the entity is still valid and not picked up or whatsoever.
        if (!item.isValid()) {
            return;
        }

        Location l = item.getLocation();
        Collection<Entity> entites = l.getWorld().getNearbyEntities(l, RANGE, RANGE, RANGE, this::findCompatibleItem);
        Optional<Entity> optional = entites.stream().findFirst();

        if (optional.isPresent()) {
            Item entity = (Item) optional.get();
            ItemStack target = entity.getItemStack();

            List<Enchantment> potentialEnchantments = applicableEnchantments.get(target.getType());

            if (potentialEnchantments == null) {
                SlimefunPlugin.getLocalization().sendMessage(p, "messages.enchantment-rune.fail", true);
                return;
            } else {
                potentialEnchantments = new ArrayList<>(potentialEnchantments);
            }

            // Removing the enchantments that the item already has from enchantmentSet
            // This also removes any conflicting enchantments
            removeIllegalEnchantments(target, potentialEnchantments);

            if (potentialEnchantments.isEmpty()) {
                SlimefunPlugin.getLocalization().sendMessage(p, "messages.enchantment-rune.no-enchantment", true);
                return;
            }

            Enchantment enchantment = potentialEnchantments.get(ThreadLocalRandom.current().nextInt(potentialEnchantments.size()));
            int level = 1;

            if (enchantment.getMaxLevel() != 1) {
                level = ThreadLocalRandom.current().nextInt(enchantment.getMaxLevel()) + 1;
            }

            target.addEnchantment(enchantment, level);

            if (target.getAmount() == 1) {
                e.setCancelled(true);

                // This lightning is just an effect, it deals no damage.
                l.getWorld().strikeLightningEffect(l);

                Slimefun.runSync(() -> {
                    // Being sure entities are still valid and not picked up or whatsoever.
                    if (item.isValid() && entity.isValid() && target.getAmount() == 1) {

                        l.getWorld().spawnParticle(Particle.CRIT_MAGIC, l, 1);
                        l.getWorld().playSound(l, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1F, 1F);

                        entity.remove();
                        item.remove();
                        l.getWorld().dropItemNaturally(l, target);

                        SlimefunPlugin.getLocalization().sendMessage(p, "messages.enchantment-rune.success", true);
                    }
                }, 10L);
            } else {
                SlimefunPlugin.getLocalization().sendMessage(p, "messages.enchantment-rune.fail", true);
            }
        }
    }

    private void removeIllegalEnchantments(ItemStack target, List<Enchantment> potentialEnchantments) {
        for (Enchantment enchantment : target.getEnchantments().keySet()) {

            // Duplicate or conflict
            potentialEnchantments.removeIf(possibleEnchantment -> possibleEnchantment.equals(enchantment) || possibleEnchantment.conflictsWith(enchantment));
        }
    }

    private boolean findCompatibleItem(Entity n) {
        if (n instanceof Item) {
            Item item = (Item) n;

            return !isItem(item.getItemStack());
        }

        return false;
    }

}