package io.github.axtuki1.onenightjinro.command;

import io.github.axtuki1.onenightjinro.MConJinro;
import io.github.axtuki1.onenightjinro.Utility;
import io.github.axtuki1.onenightjinro.player.JinroPlayers;
import io.github.axtuki1.onenightjinro.player.Job;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class JinroToJobALLChatCommand implements TabExecutor {

    private MConJinro plugin;

    public JinroToJobALLChatCommand(MConJinro plugin){
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if( args.length == 0 ){
            Utility.sendCmdHelp(sender, "/c <役職> <メッセージ...>", "指定役職全体に向けてメッセージを表示できます。", true);
            sender.sendMessage(Job.getJobHelp());
            sender.sendMessage(MConJinro.getPrefix() + ChatColor.AQUA + "観戦: kansen");
            return true;
        } else {
            sendMsgToYakuALL(Utility.CommandText(args, 1), args[0], sender);
            return true;
        }
    }

    public void sendMsgToYakuALL(String msg, String job, CommandSender sender) {
        if(msg.equalsIgnoreCase("")){
            sender.sendMessage(MConJinro.getPrefix() + ChatColor.RED + "メッセージがありません。");
            return;
        } else {
            if( job.equalsIgnoreCase("kansen") || job.equalsIgnoreCase("spec") ){
                sender.sendMessage(ChatColor.YELLOW + "[GM -> 観戦] <"+sender.getName()+"> "+msg);
                for(Player p : JinroPlayers.getSpecPlayers()) {
                    p.sendMessage(ChatColor.YELLOW + "[GM -> 観戦] <"+sender.getName()+"> "+msg);
                }
                if( !(sender instanceof ConsoleCommandSender) ){
                    MConJinro.sendConsole(ChatColor.YELLOW + "[GM -> 観戦] <"+sender.getName()+"> "+msg);
                }
                return;
            } else {
                try {
                    Job j = Job.valueOf(job);
                    sender.sendMessage(ChatColor.YELLOW + "[GM -> " + j.getJobName() + "] <" + sender.getName() + "> " + msg);
                    for (Player p : JinroPlayers.getFirstJobPlayers(j)) {
                        p.sendMessage(ChatColor.YELLOW + "[GM -> " + j.getJobName() + "] <" + sender.getName() + "> " + msg);
                    }
                    if( !(sender instanceof ConsoleCommandSender) ){
                        MConJinro.sendConsole(ChatColor.YELLOW + "[GM -> " + j.getJobName() + "] <" + sender.getName() + "> " + msg);
                    }
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(ChatColor.RED + "[GM -> 宛先不明] <"+sender.getName()+"> "+msg);
                }
                return;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> out = new ArrayList<String>();
        if( args.length == 1 ){
            for( Job j : Job.values() ){
                if ( j.getJobID().toLowerCase().startsWith(args[0].toLowerCase()) ) {
                    out.add(j.getJobID());
                }
            }
            for( String s : new String[]{
                    "spec"
            } ){
                if ( s.toLowerCase().startsWith(args[0].toLowerCase()) ) {
                    out.add(s);
                }
            }
        }
        return out;
    }
}
