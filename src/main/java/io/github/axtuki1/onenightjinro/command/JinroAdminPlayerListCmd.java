package io.github.axtuki1.onenightjinro.command;

import io.github.axtuki1.onenightjinro.GameStatus;
import io.github.axtuki1.onenightjinro.player.JinroPlayers;
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

public class JinroAdminPlayerListCmd implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<Player> unassignedPlayers = new ArrayList<>();
        for(Player p : Bukkit.getOnlinePlayers()){
            PlayerData pd = JinroPlayers.getData(p, true);
            if(GameStatus.getStatus().equals(GameStatus.Ready)){
                if( pd == null ){
                    unassignedPlayers.add(p);
                }
            }
        }
        sender.sendMessage( ChatColor.RED + "==================================" );
        if( unassignedPlayers.size() != 0 ){
            sender.sendMessage( ChatColor.GOLD + "[未割り当てプレイヤー]");
            StringBuilder unassigned_sb = new StringBuilder();
            String unassigned_output = "";
            for( Player p : unassignedPlayers ){
                unassigned_sb.append(p.getName()).append(", ");
            }
            if( (unassigned_sb.length() - 2) >= 0 ){
                unassigned_output = unassigned_sb.substring(0, unassigned_sb.length() - 2);
            } else {
                unassigned_output = unassigned_sb.toString();
            }
            sender.sendMessage( unassigned_output );
            sender.sendMessage( ChatColor.RED + "==================================");
        }
        sender.sendMessage( ChatColor.GOLD + "[参加プレイヤー]");
        for( PlayerData pd : JinroPlayers.getPlayers().values() ){
            boolean isFirst = false;
            String addText = "";
            if( !pd.getJob().equals( pd.getFirstJob() ) ){
                isFirst = true;
                addText = ChatColor.GRAY + "(First: " + pd.getFirstJob().getColor() + "[" + pd.getFirstJob().getJobName() + "]" + ChatColor.GRAY + ")";
            }
            sender.sendMessage( pd.getPlayer().getName() + ChatColor.GREEN + ": " + pd.getJob().getColor() + "[" + pd.getJob().getJobName() + "] " + addText );
        }
        sender.sendMessage( ChatColor.RED + "==================================");
        sender.sendMessage( ChatColor.AQUA + "サーバー人数: " + ChatColor.YELLOW + Bukkit.getOnlinePlayers().size() + "人 "
                + ChatColor.GREEN + "参加人数: " + ChatColor.YELLOW + JinroPlayers.getJoinedPlayers().size() + "人" );
        if( unassignedPlayers.size() != 0 ){
            sender.sendMessage( ChatColor.RED + "未割り当て人数(観戦 + GM): " + ChatColor.YELLOW + unassignedPlayers.size() + "人" );
        }
        if( JinroPlayers.getJoinedPlayers().size() <= 1 ){
            sender.sendMessage( ChatColor.RED + "おすすめはしない人数です...");
        } else {
            sender.sendMessage( ChatColor.GOLD + "/jinro_ad start でゲームを開始できます。");
        }
        sender.sendMessage( ChatColor.RED + "==================================");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
