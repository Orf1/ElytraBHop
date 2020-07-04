package dev.orf1.plugins;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;


public final class Main extends JavaPlugin implements  Listener {

    List<Player> list = new ArrayList<Player>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        int pluginId = 8081; // <-- Replace with the id of your plugin!
        Metrics metrics = new Metrics(this, pluginId);
        Bukkit.getPluginManager().registerEvents(this,this);
        getCommand("bhop").setExecutor(new BhopCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    @EventHandler
    public void onElytra(EntityToggleGlideEvent e){
        if (e.getEntity() instanceof Player){
            Player player = (Player) e.getEntity();
            if (list.contains(player)) {
                if (player.isGliding()) {
                    e.setCancelled(true);
                }
            }
        }

    }
    @EventHandler
    public void onWallHit(EntityDamageEvent e){
        if (e.getCause() == EntityDamageEvent.DamageCause.FLY_INTO_WALL){
            if (e.getEntity() instanceof Player){
                Player player = (Player) e.getEntity();
                if (list.contains(player)) {
                    if (player.isGliding()) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    private class BhopCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args.length == 1) {
                    if (player.hasPermission("elytrabhop.others")) {
                        Player target = Bukkit.getPlayer(args[0]);
                        if (target != null) {
                            if (list.contains(target)) {
                                list.remove(target);
                                target.setGliding(false);
                                player.sendMessage("Disabled Elytra BHop for " + target.getName());
                            } else {
                                list.add(target);
                                target.setGliding(true);
                                player.sendMessage("Enabled Elytra BHop for " + target.getName());
                            }
                        }
                    }
                }else if (args.length == 0){
                    if (player.hasPermission("elytrabhop.self")) {
                        if (list.contains(player)) {
                            list.remove(player);
                            player.setGliding(false);
                            player.sendMessage("Disabled Elytra BHop!");
                        } else {
                            list.add(player);
                            player.setGliding(true);
                            player.sendMessage("Enabled Elytra BHop!");
                        }

                    }
                }

            }else {
                sender.sendMessage("This command can only be used in-game!");
            }
            return false;
        }
    }
}
