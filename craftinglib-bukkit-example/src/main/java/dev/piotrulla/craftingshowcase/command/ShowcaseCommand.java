package dev.piotrulla.craftingshowcase.command;

import dev.piotrulla.craftingshowcase.gui.ShowcaseMenus;
import dev.piotrulla.craftingshowcase.kit.KitGiver;
import dev.piotrulla.craftingshowcase.showcase.RecipeShowcase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * {@code /showcase} opens the menu, {@code /showcase <kit|all>} gives the ingredients right away.
 */
public final class ShowcaseCommand implements TabExecutor {

    private static final String ALL_KITS = "all";
    private static final String KIT_SEPARATOR = ", ";
    private static final int KIT_ARGUMENT = 0;
    private static final int NO_ARGUMENTS = 0;
    private static final int KIT_ARGUMENTS = 1;

    private final Map<String, RecipeShowcase> showcases = new LinkedHashMap<>();
    private final ShowcaseMenus menus;
    private final KitGiver kitGiver;

    public ShowcaseCommand(@NotNull List<RecipeShowcase> showcases, @NotNull ShowcaseMenus menus, @NotNull KitGiver kitGiver) {
        Objects.requireNonNull(showcases, "showcases");
        showcases.forEach(showcase -> this.showcases.put(showcase.getName(), showcase));
        this.menus = Objects.requireNonNull(menus, "menus");
        this.kitGiver = Objects.requireNonNull(kitGiver, "kitGiver");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("Only players can use the showcase.", NamedTextColor.RED));
            return true;
        }

        Player player = (Player) sender;
        if (args.length == NO_ARGUMENTS) {
            this.menus.openMain(player);
            return true;
        }
        if (args.length != KIT_ARGUMENTS) {
            player.sendMessage(Component.text("Usage: /" + label + " [" + String.join("|", this.kitNames()) + "]",
                    NamedTextColor.YELLOW));
            return true;
        }

        String kitName = args[KIT_ARGUMENT].toLowerCase(Locale.ROOT);
        List<RecipeShowcase> selected = this.select(kitName);
        if (selected.isEmpty()) {
            player.sendMessage(Component.text("Unknown kit '" + kitName + "'. Available: "
                    + String.join(KIT_SEPARATOR, this.kitNames()), NamedTextColor.RED));
            return true;
        }

        this.kitGiver.give(player, selected);
        return true;
    }

    @NotNull
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != KIT_ARGUMENTS) {
            return List.of();
        }

        String prefix = args[KIT_ARGUMENT].toLowerCase(Locale.ROOT);
        return this.kitNames().stream()
                .filter(name -> name.startsWith(prefix))
                .collect(Collectors.toList());
    }

    private List<RecipeShowcase> select(String kitName) {
        if (ALL_KITS.equals(kitName)) {
            return new ArrayList<>(this.showcases.values());
        }

        RecipeShowcase showcase = this.showcases.get(kitName);
        return showcase == null ? List.of() : List.of(showcase);
    }

    private List<String> kitNames() {
        List<String> names = new ArrayList<>(this.showcases.keySet());
        names.add(ALL_KITS);
        return names;
    }
}
