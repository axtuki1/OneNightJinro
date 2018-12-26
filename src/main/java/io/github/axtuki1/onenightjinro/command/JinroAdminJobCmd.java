package io.github.axtuki1.onenightjinro.command;

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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;


/**
 * 役職を割り振るコマンド
 */
public class JinroAdminJobCmd implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if( args.length <= 1 ){
            sendCmdHelp((Player)sender);
            return true;
        }
        if( args[1].equalsIgnoreCase("set") ){
            if( args.length <= 3 ){
                sender.sendMessage( Job.getJobHelp() );
                return true;
            }
            if( args[3].equalsIgnoreCase("amari##") ){
                try {
                    Job job = Job.valueOf(args[2]);
                    JinroPlayers.addNotJob(job);
                    sender.sendMessage( MConJinro.getPrefix() + ChatColor.GREEN + "余りに役職[ "+ job.getJobName() +" ]を追加しました。" );
                } catch (IllegalArgumentException e){
                    sender.sendMessage(MConJinro.getPrefix() + ChatColor.RED + args[2] + "に該当する役職はありません。");
                    sender.sendMessage( Job.getJobHelp() );
                }
            } else {
                Player p = Bukkit.getPlayerExact( args[3] );
                if( p == null ){
                    sender.sendMessage( MConJinro.getPrefix() + ChatColor.RED + "指定プレイヤーはオフラインです。" );
                    return true;
                }
                try {
                    Job job = Job.valueOf(args[2]);
                    JinroPlayers.addPlayer(p, job);
                    sender.sendMessage( MConJinro.getPrefix() + ChatColor.GREEN + p.getName() + " を役職[ "+ job.getJobName() +" ]に設定しました。" );
                    p.sendMessage(ChatColor.RED + "=== " + ChatColor.WHITE + "あなたは " + job.getColor() + "[" + job.getJobName() + "]" + ChatColor.WHITE + " です。" + ChatColor.RED + " ===");
                } catch (IllegalArgumentException e){
                    sender.sendMessage(MConJinro.getPrefix() + ChatColor.RED + args[2] + "に該当する役職はありません。");
                    sender.sendMessage( Job.getJobHelp() );
                }
            }

        } else if(args[1].equalsIgnoreCase("del")) {
            if( args.length <= 2 ){
                sender.sendMessage( MConJinro.getPrefix() + ChatColor.RED + "プレイヤーを指定してください。" );
                return true;
            }
            Player p = Bukkit.getPlayerExact( args[2] );
            if( p == null ){
                sender.sendMessage( MConJinro.getPrefix() + ChatColor.RED + "指定プレイヤーはオフラインです。" );
                return true;
            }
            JinroPlayers.removePlayer(p);
            sender.sendMessage( MConJinro.getPrefix() + ChatColor.GREEN + p.getName() + " の役職を取り消しました。" );
            p.sendMessage(ChatColor.RED + "===== " + ChatColor.AQUA + "あなたの役職は取り消されました" + ChatColor.RED + " =====");

        } else if(args[1].equalsIgnoreCase("")){

        }
        return true;
    }

    private void sendCmdHelp(Player sender) {
        Utility.sendCmdHelp(sender, "/jinro_ad job set <Job> <Player>", "役職を設定します。");
        Utility.sendCmdHelp(sender, "/jinro_ad job del <Player>", "役職の設定を取り消します。");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> out = new ArrayList<String>();
        if( args.length == 2 ){
            for (String name : new String[]{
                    "set", "del"
            }) {
                if (name.toLowerCase().startsWith(args[1].toLowerCase())) {
                    out.add(name);
                }
            }
        }
        if( args[1].equalsIgnoreCase("set") ){
            if( args.length <= 3 ){
                for( Job j : Job.values() ){
                    if ( j.getJobID().toLowerCase().startsWith(args[2].toLowerCase()) ) {
                        out.add(j.getJobID());
                    }
                }
            } else if( args.length <= 4 ) {
                for( Player p : Bukkit.getOnlinePlayers() ){
                    if ( p.getName().toLowerCase().startsWith(args[3].toLowerCase()) ) {
                        out.add(p.getName());
                    }
                }
            }
        } else if( args[1].equalsIgnoreCase("del") ){
            if( args.length <= 3 ) {
                for( Player p : Bukkit.getOnlinePlayers() ){
                    if ( p.getName().toLowerCase().startsWith(args[2].toLowerCase()) ) {
                        out.add(p.getName());
                    }
                }
            }
        }
        return out;
    }
}
