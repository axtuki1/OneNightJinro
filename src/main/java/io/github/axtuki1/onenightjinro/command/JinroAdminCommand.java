package io.github.axtuki1.onenightjinro.command;

import io.github.axtuki1.onenightjinro.GameStatus;
import io.github.axtuki1.onenightjinro.JinroConfig;
import io.github.axtuki1.onenightjinro.MConJinro;
import io.github.axtuki1.onenightjinro.Utility;
import io.github.axtuki1.onenightjinro.player.JinroPlayers;
import io.github.axtuki1.onenightjinro.player.PlayerData;
import io.github.axtuki1.onenightjinro.player.RandomSelect;
import io.github.axtuki1.onenightjinro.player.ToolImport;
import io.github.axtuki1.onenightjinro.task.BaseTask;
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

import static org.bukkit.Bukkit.getServer;

/**
 * Admin(GameMaster)用のコマンド
 */

public class JinroAdminCommand implements TabExecutor {

    private MConJinro plugin;

    private RandomSelect rs;

    public JinroAdminCommand(MConJinro plugin){
        this.plugin = plugin;
        this.rs = new RandomSelect();
        getServer().getPluginManager().registerEvents(new RandomSelect(), plugin);
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

                if( JinroPlayers.getPlayersFromPlayingType(PlayerData.PlayingType.Player).size() == 0 ){
                    sender.sendMessage(MConJinro.getPrefix() + ChatColor.RED + "やる人誰も居ないですよ...?");
                    return true;
                }

                BaseTimerTask task = new NightTimerTask(
                        plugin,
                        plugin.getConfig().getInt(
                                JinroConfig.NightTime.getPath()
                        )
                );
                for( Player p : Bukkit.getOnlinePlayers()){
                    PlayerData pd = JinroPlayers.getData(p);
                    if( pd.getJob() == null ){
                        if( p.hasPermission("Jinro.GameMaster") ){
                            pd.setPlayingType(PlayerData.PlayingType.GameMaster);
                        } else {
                            pd.setPlayingType(PlayerData.PlayingType.Spectator);
                        }
                        JinroPlayers.setData(p , pd);
                    }
                }
                MConJinro.setTask(task);
                GameStatus.setStatus(GameStatus.Playing);
                task.start();
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
        } else if( args[0].equalsIgnoreCase("next") ){
            if( args.length >= 2){
                if( args[1].equalsIgnoreCase("force") ){
                    if( MConJinro.getTask() != null ){
                        MConJinro.getTask().end();
                    } else {
                        sender.sendMessage(MConJinro.getPrefix() + ChatColor.RED + "タスクが存在しません。");
                    }
                }
            }
            if( GameStatus.Cycle.getCycle().equals(GameStatus.Cycle.Vote) ){
                sender.sendMessage(MConJinro.getPrefix() + ChatColor.YELLOW + "投票時間です。強制的に次に移行する場合は、");
                sender.sendMessage(MConJinro.getPrefix() + ChatColor.AQUA + "/jinro_ad next force"+ ChatColor.YELLOW +" を実行してください。");
            } else {
                if( MConJinro.getTask() != null ){
                    MConJinro.getTask().end();
                } else {
                    sender.sendMessage(MConJinro.getPrefix() + ChatColor.RED + "タスクが存在しません。");
                }
            }
        } else if( args[0].equalsIgnoreCase("touhyou") ){
            new JinroAdminTouhyouCmd().onCommand(sender, command, label, args);
        } else if( args[0].equalsIgnoreCase("job") ){
            new JinroAdminJobCmd().onCommand(sender, command, label, args);
        } else if( args[0].equalsIgnoreCase("spec") ){
            new JinroAdminSpecCmd().onCommand(sender, command, label, args);
        } else if( args[0].equalsIgnoreCase("list") ){
            new JinroAdminPlayerListCmd().onCommand(sender, command, label, args);
        } else if( args[0].equalsIgnoreCase("toolimport") ){
            ToolImport.Import(Utility.CommandText(args, 1));
        } else if( args[0].equalsIgnoreCase("auto") ){
            rs.openInventory((Player)sender);
        } else if( args[0].equalsIgnoreCase("timer") ){
            // jinro_ad timer set 100
            if(args.length >= 3) {
                if( args[1].equalsIgnoreCase("set") ){
                    BaseTask task = MConJinro.getTask();
                    if( !(task instanceof BaseTimerTask) ){
                        sender.sendMessage(MConJinro.getPrefix() + ChatColor.RED + "実行中のタスクはタイマーが存在しません。");
                        return true;
                    }
                    ((BaseTimerTask)task).setSecondsRest( Integer.parseInt(args[2]) );
                } else if( args[1].equalsIgnoreCase("add") ){
                    BaseTask task = MConJinro.getTask();
                    if( !(task instanceof BaseTimerTask) ){
                        sender.sendMessage(MConJinro.getPrefix() + ChatColor.RED + "実行中のタスクはタイマーが存在しません。");
                        return true;
                    }
                    int t = ((BaseTimerTask)task).getSeconds();
                    ((BaseTimerTask)task).setSecondsRest( t + Integer.parseInt(args[2]) );
                }
            } else if( args[1].equalsIgnoreCase("pause") ) {
                MConJinro.getTask().pause();
                sender.sendMessage(MConJinro.getPrefix() + "タイマーを一時停止しました。");
            } else {
                Utility.sendCmdHelp(sender, "/jinro_ad timer set <秒数>", "タイマーの秒数を設定します。");
                Utility.sendCmdHelp(sender, "/jinro_ad timer add <秒数>", "タイマーの秒数を追加します。");
                Utility.sendCmdHelp(sender, "/jinro_ad timer pause", "タイマーを一時停止します。");
                return true;
            }
        }
        return true;
    }

    private void sendCmdHelp(Player sender) {
        Utility.sendCmdHelp(sender, "/jinro_ad start", "ゲームを開始します。");
        Utility.sendCmdHelp(sender, "/jinro_ad stop", "ゲームを強制終了します。");
        Utility.sendCmdHelp(sender, "/jinro_ad stop", "タイマーを");
        Utility.sendCmdHelp(sender, "/jinro_ad next", "次のタスクへ移動します。");
        Utility.sendCmdHelp(sender, "/jinro_ad list", "プレイヤーリストを表示します。");
        Utility.sendCmdHelp(sender, "/jinro_ad job <...>", "役職を設定します。");
        Utility.sendCmdHelp(sender, "/jinro_ad touhyou <...>", "投票関連のコマンドです。");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> out = new ArrayList<String>();
        if( args.length == 1 ){
            for (String name : new String[]{
                    "start", "stop", "next", "init", "touhyou", "job", "timer", "auto"
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
            } else if( args[0].equalsIgnoreCase("timer") ){
                for (String name : new String[]{
                        "set", "add", "pause"
                }) {
                    if (name.toLowerCase().startsWith(args[1].toLowerCase())) {
                        out.add(name);
                    }
                }
            } else if( args[0].equalsIgnoreCase("spec") ){
                out = new JinroAdminSpecCmd().onTabComplete(sender, command, alias, args);
            }
        }
        return out;
    }
}
