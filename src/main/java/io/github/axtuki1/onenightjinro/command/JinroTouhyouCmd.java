package io.github.axtuki1.onenightjinro.command;

import io.github.axtuki1.onenightjinro.GameStatus;
import io.github.axtuki1.onenightjinro.MConJinro;
import io.github.axtuki1.onenightjinro.Utility;
import io.github.axtuki1.onenightjinro.player.JinroPlayers;
import io.github.axtuki1.onenightjinro.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JinroTouhyouCmd implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if( !GameStatus.getStatus().equals(GameStatus.Playing) && !GameStatus.Cycle.getCycle().equals(GameStatus.Cycle.Vote) ){
            sender.sendMessage(MConJinro.getPrefix() + ChatColor.RED + "まだ使用できません。");
            return true;
        }
        if( args.length <= 0 ){
            sendCmdHelp((Player)sender);
            return true;
        } else {
            Player p = Utility.getPlayer( args[1] );
            if(p == null){
                sender.sendMessage(MConJinro.getPrefix() + ChatColor.RED + "プレイヤーが見つかりませんでした。");
                return true;
            }
            if( ((Player) sender).getInventory().getItemInMainHand().getType() == Material.PAPER){
                Player ps = (Player) sender;
                PlayerData pds = JinroPlayers.getData(ps.getUniqueId());
                pds.setVoteTarget( p.getUniqueId() );
                JinroPlayers.setData(ps.getUniqueId(), pds);
                ps.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                ps.updateInventory();
                sender.sendMessage(MConJinro.getPrefix() + ChatColor.GREEN + p.getName() + " に投票しました。");
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> out = new ArrayList<String>();
        if( args.length == 2 ){
            for( PlayerData pd : JinroPlayers.getPlayers().values()){
                Player p = pd.getPlayer();
                if ( p.getName().toLowerCase().startsWith(args[1].toLowerCase()) ) {
                    out.add(p.getName());
                }
            }
        }
        return out;
    }

    private void sendCmdHelp(Player sender) {
        Utility.sendCmdHelp(sender, "/jinro_ad touhyou <player>", "投票します。");
    }
}
