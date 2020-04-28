package io.github.thebusybiscuit.slimefun4.core.commands;

import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.SlimefunPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.*;

class SlimefunTabCompleter implements TabCompleter {

    private static final int MAX_SUGGESTIONS = 80;

    private final SlimefunCommand command;

    public SlimefunTabCompleter(SlimefunCommand command) {
        this.command = command;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            return createReturnList(command.getSubCommandNames(), args[0]);
        }
        else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("give")) {
                return createReturnList(getSlimefunItems(), args[2]);
            }
            else if (args[0].equalsIgnoreCase("research")) {
                Set<NamespacedKey> researches = SlimefunPlugin.getRegistry().getResearchIds().keySet();
                List<String> suggestions = new LinkedList<>();

                suggestions.add("all");
                suggestions.add("reset");

                for (NamespacedKey key : researches) {
                    suggestions.add(key.toString().toLowerCase(Locale.ROOT));
                }

                return createReturnList(suggestions, args[2]);
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }
    }

    /***
     * Returns a sublist from a given list containing items that start with the given string if string is not empty
     *
     * @param list
     *            The list to process
     * @param string
     *            The typed string
     * @return Sublist if string is not empty
     */
    private List<String> createReturnList(List<String> list, String string) {
        if (string.length() == 0) {
            return list;
        }

        String input = string.toLowerCase(Locale.ROOT);
        List<String> returnList = new LinkedList<>();

        for (String item : list) {
            if (item.toLowerCase(Locale.ROOT).contains(input)) {
                returnList.add(item);

                if (returnList.size() >= MAX_SUGGESTIONS) {
                    break;
                }
            } else if (item.equalsIgnoreCase(input)) {
                return Collections.emptyList();
            }
        }

        return returnList;
    }

    private List<String> getSlimefunItems() {
        List<SlimefunItem> items = SlimefunPlugin.getRegistry().getEnabledSlimefunItems();
        List<String> list = new ArrayList<>(items.size());

        for (SlimefunItem item : items) {
            list.add(item.getID());
        }

        return list;
    }

}
