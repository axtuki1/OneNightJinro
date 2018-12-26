package io.github.axtuki1.onenightjinro;

import io.github.axtuki1.onenightjinro.command.JinroAdminCommand;
import io.github.axtuki1.onenightjinro.command.JinroToJobALLChatCommand;
import io.github.axtuki1.onenightjinro.event.Event;
import io.github.axtuki1.onenightjinro.event.GuiEvent;
import io.github.axtuki1.onenightjinro.player.JinroPlayers;
import io.github.axtuki1.onenightjinro.player.RandomConfig;
import io.github.axtuki1.onenightjinro.player.RandomSelect;
import io.github.axtuki1.onenightjinro.scoreboard.JinroScoreboard;
import io.github.axtuki1.onenightjinro.task.BaseTask;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.axtuki1.onenightjinro.command.JinroCommand;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

/**
 * ワンナイト人狼のシステム面担当プラグイン。
 * 他の方の書き方を参考にしてるので似てるとか言われてもそうだなぁとしか
 * @author axtuki1
 */

public final class MConJinro extends JavaPlugin {

    private static MConJinro main;
    private static BaseTask timerTask = null;
    private static JinroScoreboard js;
    private HashMap<String, TabExecutor> commands;
    private static boolean isDEBUG = false;

    private static World world;

    @Override
    public void onEnable() {
        main = this;

        // ワールド読み込み
        new BukkitRunnable(){
            @Override
            public void run() {
                try {
                    world = Bukkit.getWorld(getConfig().getString("spawnpoint.world"));
                    if (world == null) {
                        world = Bukkit.getWorlds().get(0);
                    }
                } catch (IllegalArgumentException e) {
                    world = Bukkit.getWorlds().get(0);
                }

                if(getConfig().get("spawnpoint.x") == null ||
                        getConfig().get("spawnpoint.y") == null ||
                        getConfig().get("spawnpoint.z") == null) {
                    setRespawnLoc( world.getSpawnLocation() );
                    getConfig().set("spawnpoint.world", world.getName() );
                    getConfig().set("spawnpoint.x", world.getSpawnLocation().getX() );
                    getConfig().set("spawnpoint.y", world.getSpawnLocation().getY() );
                    getConfig().set("spawnpoint.z", world.getSpawnLocation().getZ() );
                    saveConfig();
                } else if( getConfig().get("spawnpoint.yaw") == null ||
                        getConfig().get("spawnpoint.pitch") == null ) {
                    World w;
                    try {
                        w = Bukkit.getWorld(getConfig().getString("spawnpoint.world"));
                        if (w == null) {
                            w = Bukkit.getWorlds().get(0);
                        }
                    } catch (IllegalArgumentException e) {
                        w = Bukkit.getWorlds().get(0);
                    }
                    setRespawnLoc(new Location(w, getConfig().getDouble("spawnpoint.x"),
                            getConfig().getDouble("spawnpoint.y"),
                            getConfig().getDouble("spawnpoint.z")
                    ));
                } else {
                    World w;
                    try {
                        w = Bukkit.getWorld(getConfig().getString("spawnpoint.world"));
                        if (w == null) {
                            w = Bukkit.getWorlds().get(0);
                        }
                    } catch (IllegalArgumentException e) {
                        w = Bukkit.getWorlds().get(0);
                    }
                    setRespawnLoc(new Location(w, getConfig().getDouble("spawnpoint.x"),
                            getConfig().getDouble("spawnpoint.y"),
                            getConfig().getDouble("spawnpoint.z"),
                            Float.parseFloat(getConfig().getString("spawnpoint.yaw")),
                            Float.parseFloat(getConfig().getString("spawnpoint.pitch"))
                    ));
                }
                init();
                JinroPlayers.init();
            }
        }.runTaskAsynchronously(this);

        commands = new HashMap<String, TabExecutor>();
        commands.put("jinro", new JinroCommand(this));
        commands.put("jinro_ad", new JinroAdminCommand(this));
        commands.put("c", new JinroToJobALLChatCommand(this));

        saveDefaultConfig();
        reloadConfig();

        isDEBUG = getConfig().getBoolean("Debug");

        getServer().getPluginManager().registerEvents(new Event(), this);
    }

    @Override
    public void onDisable() {

    }

    public static MConJinro getMain() {
        return main;
    }

    public static boolean isDEBUG() {
        return isDEBUG;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return commands.get(command.getName()).onCommand(sender,command,label,args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return commands.get(command.getName()).onTabComplete(sender, command, alias, args);
    }

    public static void setTask(BaseTask task) { timerTask = task;
    }

    public static BaseTask getTask() {
        return timerTask;
    }

    public static JinroScoreboard getScoreboard() {
        return js;
    }

    public static void setScoreboard(JinroScoreboard js) {
        MConJinro.js = js;
    }

    public static String getPrefix() {
        return ChatColor.GREEN + "[" + ChatColor.AQUA + "OneNightJinro" + ChatColor.GREEN + "] " + ChatColor.WHITE;
    }

    public static void sendGameMaster(String s, boolean isLogging){
        for( Player p : Bukkit.getOnlinePlayers() ){
            if( JinroPlayers.isGameMaster(p) ){
                p.sendMessage( s );
            }
        }
        if( isLogging ){
            sendConsole(s);
        }
    }

    public static void sendConsole(String s){
        getMain().getServer().getConsoleSender().sendMessage(s);
    }

    public static void sendGameMaster(String s){
        sendGameMaster(s, true);
    }

    public static void init(){
        MConJinro.getMain().reloadConfig();
        Scoreboard s = JinroScoreboard.getScoreboard();
        Objective obj = s.getObjective( MConJinro.getMain().getConfig().getString(JinroConfig.InfoObjectiveName.getPath()) );
        if( obj != null ){
            obj.unregister();
        }
        obj = s.registerNewObjective(MConJinro.getMain().getConfig().getString(JinroConfig.InfoObjectiveName.getPath()),"dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName("情報");
        GameStatus.setStatus(GameStatus.Ready);
        GameStatus.Cycle.setCycle(GameStatus.Cycle.Ready);
        MConJinro.getRespawnLoc().getWorld().setTime(0);
        JinroPlayers.init();
        for( Player p : Bukkit.getOnlinePlayers() ){
            p.setPlayerListName(p.getName() + " ");
            if( p.hasPermission("Jinro.GameMaster") ){
                p.setPlayerListName(ChatColor.YELLOW + "[GM] " + p.getName() + " ");
            }
            if (p.getGameMode().equals(GameMode.SPECTATOR)) {
                p.setGameMode(GameMode.ADVENTURE);
                TeleportToRespawn(p);
            }
        }
        if( MConJinro.getTask() != null ) {
            MConJinro.getTask().stop();
            MConJinro.setTask(null);
        }
    }

    private static Location ReikaiLoc = null;
    private static Location RespawnLoc = null;

    public static void TeleportToReikai(Player p){
        if( getReikaiLoc() == null ){
            p.teleport(p.getWorld().getSpawnLocation());
        }
        p.teleport(getReikaiLoc());
    }

    public static void TeleportToRespawn(Player p){
        if( getRespawnLoc() == null ){
            p.teleport(p.getWorld().getSpawnLocation());
        }
        p.teleport(getRespawnLoc());
    }

    public static void setReikaiLoc(Location loc){
         MConJinro.getMain().getConfig().set("reikai.world", loc.getWorld().getName());
         MConJinro.getMain().getConfig().set("reikai.x", loc.getX());
         MConJinro.getMain().getConfig().set("reikai.y", loc.getY());
         MConJinro.getMain().getConfig().set("reikai.z", loc.getZ());
         MConJinro.getMain().getConfig().set("reikai.yaw", loc.getYaw());
         MConJinro.getMain().getConfig().set("reikai.pitch", loc.getPitch());
        try {
            MConJinro.getMain().getConfig().save( MConJinro.getMain().getDataFolder() + File.separator + "config.yml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        ReikaiLoc = loc;
    }

    public static void setRespawnLoc(Location loc) {
        world = loc.getWorld();
        MConJinro.getMain().getConfig().set("spawnpoint.world", loc.getWorld().getName());
        MConJinro.getMain().getConfig().set("spawnpoint.x", loc.getX());
        MConJinro.getMain().getConfig().set("spawnpoint.y", loc.getY());
        MConJinro.getMain().getConfig().set("spawnpoint.z", loc.getZ());
        MConJinro.getMain().getConfig().set("spawnpoint.yaw", loc.getYaw());
        MConJinro.getMain().getConfig().set("spawnpoint.pitch", loc.getPitch());
        try {
            MConJinro.getMain().getConfig().save(MConJinro.getMain().getDataFolder() + File.separator + "config.yml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        RespawnLoc = loc;
    }

    public static Location getReikaiLoc(){
        return ReikaiLoc;
    }

    public static Location getRespawnLoc(){
        return RespawnLoc;
    }
}
