package io.github.axtuki1.onenightjinro.command;

import io.github.axtuki1.onenightjinro.GameStatus;
import io.github.axtuki1.onenightjinro.MConJinro;
import io.github.axtuki1.onenightjinro.player.JinroPlayers;
import io.github.axtuki1.onenightjinro.player.PlayerData;
import io.github.axtuki1.onenightjinro.task.DiscussionTimerTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class JinroDiscussionSkipCmd implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if( !GameStatus.getStatus().equals( GameStatus.Playing ) || !GameStatus.Cycle.getCycle().equals(GameStatus.Cycle.Discussion) ){
            sender.sendMessage(MConJinro.getPrefix() + ChatColor.RED + "現在は使用できません。");
            return true;
        }
        DiscussionTimerTask task = null;
        if( MConJinro.getTask() instanceof DiscussionTimerTask ){
            task = (DiscussionTimerTask) MConJinro.getTask();
            if( task.getSeconds() <= 10 ){
                sender.sendMessage(MConJinro.getPrefix() + ChatColor.RED + "スキップ投票は締め切られました。");
            }
        } else {
            sender.sendMessage(MConJinro.getPrefix() + ChatColor.RED + "現在は使用できません。");
            return true;
        }
        PlayerData pd = JinroPlayers.getData( (Player)sender );
        if( pd.isDiscussionSkipVoted() ){
            sender.sendMessage(MConJinro.getPrefix() + ChatColor.AQUA + "あなたはスキップ投票が済んでいます。");
            return true;
        } else {
            pd.setDiscussionSkipVoted(true);
            JinroPlayers.setData((Player)sender, pd);
            boolean isSkip = true;
            int nonSkiped = 0;
            for( PlayerData pld : JinroPlayers.getJoinedPlayers().values() ){
                if( !pld.isDiscussionSkipVoted() ){
                    isSkip = false;
                    nonSkiped++;
                }
            }
            // スキップする
            if( isSkip ){
                Bukkit.broadcastMessage(MConJinro.getPrefix() + ChatColor.YELLOW + sender.getName() + "からのスキップ投票を受理しました。");
                Bukkit.broadcastMessage(MConJinro.getPrefix() + ChatColor.YELLOW + "全員の投票を確認しました。スキップします...");
                task.setSecondsRest(10);
            } else {
                Bukkit.broadcastMessage(MConJinro.getPrefix() + ChatColor.YELLOW + sender.getName() + "からのスキップ投票を受理しました。" + ChatColor.AQUA + "(残り"+nonSkiped+"人)");
            }
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
