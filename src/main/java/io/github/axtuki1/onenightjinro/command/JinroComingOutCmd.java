package io.github.axtuki1.onenightjinro.command;

import io.github.axtuki1.onenightjinro.GameStatus;
import io.github.axtuki1.onenightjinro.MConJinro;
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

public class JinroComingOutCmd implements TabExecutor{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if( args.length <= 1 ){
            sender.sendMessage(Job.getJobHelp());
            return true;
        }
        if( !GameStatus.getStatus().equals(GameStatus.Playing) ){
            sender.sendMessage(MConJinro.getPrefix() + ChatColor.RED + "現在は使用できません。");
            return true;
        }
        if( args[1].equalsIgnoreCase("siro") || args[1].equalsIgnoreCase("kuro") ){
            Player p = (Player)sender;
            Job.Marker j = null;
            if( args[1].equalsIgnoreCase("siro") ){
                j = Job.Marker.white;
            } else if( args[1].equalsIgnoreCase("kuro") ){
                j = Job.Marker.black;
            }
            PlayerData data = JinroPlayers.getData( p );
            data.setMarker(j);
            JinroPlayers.setData(p, data);
            p.setPlayerListName( "[" + j.getString() + "] " + p.getName() + " " );
            p.sendMessage(MConJinro.getPrefix() + j.getName() + "マーカーを付けました");
        } else {
            try {
                Player p = (Player)sender;
                Job j = Job.valueOf(args[1]);
                PlayerData data = JinroPlayers.getData( p );
                data.setComingOut(j);
                JinroPlayers.setData(p, data);
                p.setPlayerListName( j.getColor() + "[" + j.getJobName2Moji() + "] " + p.getName() + " " );
                Bukkit.broadcastMessage(j.getColor() + p.getName() + "が" + j.getJobName() + "COしました。");
            } catch (IllegalArgumentException e){
                sender.sendMessage(MConJinro.getPrefix() + ChatColor.RED + args[1] + "に該当する役職はありません。");
                sender.sendMessage( Job.getJobHelp() );
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> out = new ArrayList<String>();
        if( args.length <= 2 ){
            for( Job j : Job.values() ){
                if ( j.getJobID().toLowerCase().startsWith(args[1].toLowerCase()) ) {
                    out.add(j.getJobID());
                }
            }
            for (String name : new String[]{
                    "siro", "kuro"
            }) {
                if (name.toLowerCase().startsWith(args[1].toLowerCase())) {
                    out.add(name);
                }
            }
        }
        return out;
    }
}
