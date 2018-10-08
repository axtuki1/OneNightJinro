package io.github.axtuki1.onenightjinro.command;

import io.github.axtuki1.onenightjinro.GameStatus;
import io.github.axtuki1.onenightjinro.MConJinro;
import io.github.axtuki1.onenightjinro.Utility;
import io.github.axtuki1.onenightjinro.player.JinroPlayers;
import io.github.axtuki1.onenightjinro.player.PlayerData;
import io.github.axtuki1.onenightjinro.task.BaseTask;
import io.github.axtuki1.onenightjinro.task.ExecutionTask;
import javafx.print.Collation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.Map.Entry;

public class JinroAdminTouhyouCmd implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if( args.length <= 1 ){
            sendCmdHelp((Player)sender);
            return true;
        }
        if(!GameStatus.Cycle.getCycle().equals(GameStatus.Cycle.Vote)){
            sender.sendMessage(MConJinro.getPrefix() + ChatColor.RED + "投票の時間ではありません。");
            return true;
        }
        if( args[1].equalsIgnoreCase("check") ){
            HashMap<Player, Player> tl = TouhyouList();
            sender.sendMessage(ChatColor.RED + "===========[投票結果]===========");
            for( Player p : tl.keySet() ){
                sender.sendMessage(ChatColor.GREEN + p.getName() + " -> " + tl.get(p).getName());
            }
            List<Player> pl = TouhyouCheck();
            if( pl.size() != 0){
                StringBuilder sb = new StringBuilder();
                for( Player p : pl ){
                    sb.append(p.getName()).append(", ");
                }
                String out = sb.toString();
                if( (out.length() - 2) > 0 ){
                    out = out.substring(0, out.length() - 2);
                }
                Bukkit.broadcastMessage(MConJinro.getPrefix() + out);
                sender.sendMessage(MConJinro.getPrefix() + "が投票していません。");
            }
        } else if(args[1].equalsIgnoreCase("open")) {
            int rep = TouhyouOpen();
            if( rep != 0 ){
                switch ( rep ){
                    case 1:
                        sender.sendMessage(MConJinro.getPrefix() + "まだ投票していないプレイヤーが居ます。");
                        break;
                }
            }
        }
        return true;
    }

    public int TouhyouOpen() {
        if( TouhyouCheck().size() != 0 ){
            return 1;
        }
        Bukkit.broadcastMessage(ChatColor.RED + "===========[投票結果]===========");
        HashMap<Player, Player> tl = TouhyouList();
        HashMap<Player, Integer> num = new HashMap<Player, Integer>();
        for( Player p : tl.keySet() ){
            Player target = tl.get(p);
            Bukkit.broadcastMessage(ChatColor.GREEN + p.getName() + " -> " + target.getName());
            num.put(
                    target,
                    ( num.get(target) == null ? 1 : num.get(target) + 1 )
            );
        }
        Bukkit.broadcastMessage(ChatColor.RED + "===============================");
        Bukkit.broadcastMessage(ChatColor.YELLOW + "投票の結果、");
        int maxValueInMap = Collections.max( num.values() );
        if( maxValueInMap == 1 ){
            Bukkit.broadcastMessage(ChatColor.YELLOW + "同数票だった為、処刑が発生しません。");
        } else {
            StringBuilder max = new StringBuilder();
            List<UUID> exp = new ArrayList<UUID>();
            for (Entry<Player, Integer> entry : num.entrySet()) {
                if (entry.getValue() == maxValueInMap) {
                    max.append(entry.getKey().getName()).append(", ");
                    exp.add(entry.getKey().getUniqueId());
                }
            }
            String out = max.toString();
            if( out.length() >= 2 ){
                out = out.substring(0, out.length() - 2);
            }
            JinroPlayers.setExecutionPlayers(exp);
            Bukkit.broadcastMessage(ChatColor.YELLOW + out + " が処刑されます。");
        }
        Bukkit.broadcastMessage(ChatColor.AQUA + "チャットが全体に聞こえるようになりました。");
        Bukkit.broadcastMessage(ChatColor.RED + "===============================");
        BaseTask task = new ExecutionTask(MConJinro.getMain());
        task.start();
        MConJinro.setTask(task);
        return 0;
    }

    public List<Player> TouhyouCheck() {
        ArrayList<Player> p = new ArrayList<Player>();
        for(PlayerData pd : JinroPlayers.getPlayers().values()){
            if( pd.getVoteTarget() == null ){
                p.add( pd.getPlayer() );
            }
        }
        return p;
    }

    public HashMap<Player, Player> TouhyouList(){
        HashMap<Player, Player> p = new HashMap<Player, Player>();
        for(PlayerData pd : JinroPlayers.getPlayers().values()){
            if( pd.getVoteTarget() != null ){
                p.put( pd.getPlayer(), Bukkit.getPlayer(pd.getVoteTarget()) );
            }
        }
        return p;
    }

    private void sendCmdHelp(Player sender) {
        Utility.sendCmdHelp(sender, "/jinro_ad touhyou check", "投票状況を確認します。");
        Utility.sendCmdHelp(sender, "/jinro_ad touhyou open", "開票します。");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> out = new ArrayList<String>();
        if( args.length == 2 ){
            for (String name : new String[]{
                    "open", "check"
            }) {
                if (name.toLowerCase().startsWith(args[1].toLowerCase())) {
                    out.add(name);
                }
            }
        }
        return out;
    }
}
