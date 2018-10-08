package io.github.axtuki1.onenightjinro.command;

import io.github.axtuki1.onenightjinro.GameStatus;
import io.github.axtuki1.onenightjinro.MConJinro;
import io.github.axtuki1.onenightjinro.Utility;
import io.github.axtuki1.onenightjinro.player.JinroPlayers;
import io.github.axtuki1.onenightjinro.player.Job;
import io.github.axtuki1.onenightjinro.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 参加者用のコマンド
 */

public class JinroCommand implements TabExecutor {

    private MConJinro plugin;

    public JinroCommand(MConJinro plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if( args.length <= 0 ){
            sendCmdHelp((Player)sender);
            return true;
        }
        if( args[0].equalsIgnoreCase("uranai") ) {
            Job.uranai.Action((Player) sender, args);
            return true;
        } else if( args[0].equalsIgnoreCase("change") ){
            Job.kaitou.Action((Player)sender, args);
            return true;
        } else if( args[0].equalsIgnoreCase("touhyou") ){
            new JinroTouhyouCmd().onCommand(sender, command, label, args);
            return true;
        } else if( args[0].equalsIgnoreCase("co") ){
            new JinroComingOutCmd().onCommand(sender, command, label, args);
            return true;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> out = new ArrayList<String>();
        if( args.length == 1 ){
            List<String> s = new ArrayList<String>(){
                {
                    add("touhyou");
                    add("co");
                }
            };
            PlayerData pd = JinroPlayers.getData( ((Player) sender) );
            if( JinroPlayers.equalsFirstJob((Player) sender,Job.uranai) ){
                s.add("uranai");
            }
            if( JinroPlayers.equalsFirstJob((Player)sender, Job.kaitou) ){
                s.add("change");
            }
            for (String name : s) {
                if (name.toLowerCase().startsWith(args[0].toLowerCase())) {
                    out.add(name);
                }
            }
        } else {
            if( args[0].equalsIgnoreCase("touhyou") ){
                out = new JinroTouhyouCmd().onTabComplete(sender, command, alias, args);
            } else if( args[0].equalsIgnoreCase("change") || args[0].equalsIgnoreCase("uranai") ){
                if( args.length == 2 ){
                    for ( PlayerData pd : JinroPlayers.getPlayers().values()) {
                        if (pd.getPlayer().getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                            out.add(pd.getPlayer().getName());
                        }
                    }
                }
            } else if( args[0].equalsIgnoreCase("co") ){
                out = new JinroComingOutCmd().onTabComplete(sender, command, alias, args);
            }
        }
        return out;
    }

    public void sendCmdHelp(Player p){
        p.sendMessage("");
    }

}
