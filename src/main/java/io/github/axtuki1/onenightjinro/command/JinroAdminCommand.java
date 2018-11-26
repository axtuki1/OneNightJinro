package io.github.axtuki1.onenightjinro.command;

import io.github.axtuki1.onenightjinro.GameStatus;
import io.github.axtuki1.onenightjinro.JinroConfig;
import io.github.axtuki1.onenightjinro.MConJinro;
import io.github.axtuki1.onenightjinro.Utility;
import io.github.axtuki1.onenightjinro.player.JinroPlayers;
import io.github.axtuki1.onenightjinro.player.PlayerData;
import io.github.axtuki1.onenightjinro.player.RandomSelect;
import io.github.axtuki1.onenightjinro.player.ToolImport;
import io.github.axtuki1.onenightjinro.task.BaseTimerTask;
import io.github.axtuki1.onenightjinro.task.NightTimerTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import javax.tools.Tool;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Admin(GameMaster)用のコマンド
 */

public class JinroAdminCommand implements TabExecutor {

    private MConJinro plugin;

    public JinroAdminCommand(MConJinro plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if( args.length <= 0 ){
            sendCmdHelp((Player)sender);
            return true;
        }
        if( args[0].equalsIgnoreCase("init") ){
            MConJinro.init();
            Bukkit.broadcastMessage(MConJinro.getPrefix() + "初期化しました。");
        }
        if( args[0].equalsIgnoreCase("reload") ){
            MConJinro.getMain().reloadConfig();
            sender.sendMessage(MConJinro.getPrefix() + "Configの再読込を行いました。");
        }
        if( args[0].equalsIgnoreCase("start") ){
            if( GameStatus.getStatus().equals(GameStatus.Ready) ){
                BaseTimerTask task = new NightTimerTask(
                        plugin,
                        plugin.getConfig().getInt(
                                JinroConfig.NightTime.getPath()
                        )
                );
                task.start();
                MConJinro.setTask(task);
                GameStatus.setStatus(GameStatus.Playing);
                for( UUID u : JinroPlayers.getNonJoinPlayers().keySet()){
                    Player p = Bukkit.getPlayer(u);
                    PlayerData pd = new PlayerData(p.getUniqueId());
                    if( p.hasPermission("Jinro.GameMaster") ){
                        pd.setMode(PlayerData.Type.GameMaster);
                    } else {
                        pd.setMode(PlayerData.Type.Spectator);
                    }
                    JinroPlayers.setData(p , pd);
                }
            } else {
                sender.sendMessage(MConJinro.getPrefix() + ChatColor.RED + "ゲームは既に開始されています。");
            }
            return true;
        } else if( args[0].equalsIgnoreCase("stop") ){
            if( MConJinro.getTask() != null ){
                MConJinro.getTask().stop();
                Bukkit.broadcastMessage(MConJinro.getPrefix() + ChatColor.YELLOW + "ゲームを強制終了しました。");
            } else {
                Bukkit.broadcastMessage(MConJinro.getPrefix() + ChatColor.RED + "ゲームが実行中ではありません。");
            }
        } else if( args[0].equalsIgnoreCase("pause") ){
            MConJinro.getTask().pause();
            sender.sendMessage(MConJinro.getPrefix() + "タイマーを一時停止しました。");
        } else if( args[0].equalsIgnoreCase("next") ){
            if( GameStatus.Cycle.getCycle().equals(GameStatus.Cycle.Vote) ){
                sender.sendMessage(MConJinro.getPrefix() + ChatColor.YELLOW + "投票時間です。強制的に次に移行する場合は、");
                sender.sendMessage(MConJinro.getPrefix() + ChatColor.AQUA + "/jinro_ad next force"+ ChatColor.YELLOW +" を実行してください。");
            } else {
                if( MConJinro.getTask() != null ){
                    MConJinro.getTask().end();
                } else {
                    Bukkit.broadcastMessage(MConJinro.getPrefix() + ChatColor.RED + "タスクが存在しません。");
                }
            }
        } else if( args[0].equalsIgnoreCase("touhyou") ){
            new JinroAdminTouhyouCmd().onCommand(sender, command, label, args);
        } else if( args[0].equalsIgnoreCase("job") ){
            new JinroAdminJobCmd().onCommand(sender, command, label, args);
        } else if( args[0].equalsIgnoreCase("list") ){
            new JinroAdminPlayerListCmd().onCommand(sender, command, label, args);
        } else if( args[0].equalsIgnoreCase("toolimport") ){
            ToolImport.Import(Utility.CommandText(args, 1));
        } else if( args[0].equalsIgnoreCase("auto") ){
            new RandomSelect().openInventory((Player)sender);
        }
        return true;
    }

    private void sendCmdHelp(Player sender) {
        Utility.sendCmdHelp(sender, "/jinro_ad start", "ゲームを開始します。");
        Utility.sendCmdHelp(sender, "/jinro_ad stop", "ゲームを開始します。");
        Utility.sendCmdHelp(sender, "/jinro_ad next", "ゲームを開始します。");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> out = new ArrayList<String>();
        if( args.length == 1 ){
            for (String name : new String[]{
                    "start", "stop", "next", "init", "touhyou", "job"
            }) {
                if (name.toLowerCase().startsWith(args[0].toLowerCase())) {
                    out.add(name);
                }
            }
        }
        if( args.length >= 2 ){
            if( args[0].equalsIgnoreCase("touhyou") ){
                out = new JinroAdminTouhyouCmd().onTabComplete(sender, command, alias, args);
            } else if( args[0].equalsIgnoreCase("job") ){
                out = new JinroAdminJobCmd().onTabComplete(sender, command, alias, args);
            }
        }
        return out;
    }
}
