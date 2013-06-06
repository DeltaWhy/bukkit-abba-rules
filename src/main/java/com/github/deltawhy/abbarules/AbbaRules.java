package com.github.deltawhy.abbarules;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class AbbaRules extends JavaPlugin implements Listener {
    Scoreboard sb;
    Objective obj;
    List<String> playerList;
    Map<Material, Integer> blockValues;

    public void onDisable() {
        if (obj != null) {
            obj.unregister();
        }
    }

    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        playerList = new ArrayList<String>();
        blockValues = new HashMap<Material, Integer>();

        //static for now
        blockValues.put(Material.REDSTONE_ORE, 1);
        blockValues.put(Material.GLOWING_REDSTONE_ORE, 1);
        blockValues.put(Material.LAPIS_ORE, 1);
        blockValues.put(Material.GOLD_ORE, 3);
        blockValues.put(Material.DIAMOND_ORE, 5);
        blockValues.put(Material.EMERALD_ORE, 7);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can't be run from the console.");
            return false;
        }
        if (label.equals("abba")) {
            if (args.length == 0) {
                sender.sendMessage("/abba: Play ABBA rules caving\n" +
                        "  /abba create: Start a game\n" +
                        "  /abba join: Join a game\n" +
                        "  /abba start: Start the timer");
            } else if (args[0].equalsIgnoreCase("create")) {
                createGame((Player)sender);
            } else if (args[0].equalsIgnoreCase("join")) {
                addPlayer((Player)sender);
            } else if (args[0].equalsIgnoreCase("start")) {
                startGame();
            } else {
                sender.sendMessage("/abba: Play ABBA rules caving\n" +
                        "\t/abba create: Start a game\n" +
                        "\t/abba join: Join a game\n" +
                        "\t/abba start: Start the timer");
            }
            return true;
        } else {
            return false;
        }
    }

    public void createGame(Player player) {
        if (sb == null) sb = Bukkit.getScoreboardManager().getMainScoreboard();
        if (obj != null) {
            obj.unregister();
        }
        obj = sb.registerNewObjective("abba_score", "dummy");
        obj.setDisplayName("Score");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        playerList.clear();

        Bukkit.broadcastMessage(ChatColor.GOLD + player.getName() + " has started a game of ABBA rules caving!" +
                " Join the game with /abba join.");
        addPlayer(player);
    }

    public void addPlayer(Player player) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(player.getName());
        obj.getScore(op).setScore(1);
        obj.getScore(op).setScore(0);
        playerList.add(player.getName());
    }

    public void startGame() {

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player == null || !playerList.contains(player.getName())) {
            return;
        }
        Integer value = blockValues.get(event.getBlock().getType());
        if (value != null) {
            Score score = obj.getScore(Bukkit.getOfflinePlayer(player.getName()));
            score.setScore(score.getScore()+value);
        }
    }

}

