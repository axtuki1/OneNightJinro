package io.github.axtuki1.onenightjinro.task;

import io.github.axtuki1.onenightjinro.ActionBar;
import io.github.axtuki1.onenightjinro.GameStatus;
import io.github.axtuki1.onenightjinro.MConJinro;
import io.github.axtuki1.onenightjinro.player.JinroPlayers;
import io.github.axtuki1.onenightjinro.player.Job;
import io.github.axtuki1.onenightjinro.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class ExecutionTask extends BaseTask {

    public ExecutionTask(MConJinro pl){
        super(pl);
    }

    @Override
    public void start() {
        super.start();
        GameStatus.Cycle.setCycle(GameStatus.Cycle.Execution);
    }


    /**
     * 表示の更新に使うメソッド。
     * execと違ってタイマーが可動している間は呼ばれる。
     */
    public void updateView() {
        StringBuilder out = new StringBuilder();
        for( UUID u : JinroPlayers.getExecutionPlayers()){
            out.append(Bukkit.getPlayer(u).getName()).append(", ");
        }
        String ou = out.toString();
        if( (out.length() - 2) > 0){
            ou = out.substring(0, out.length() - 2);
        }
        if( ou.equalsIgnoreCase("") ){
            ou = ChatColor.AQUA + "処刑なし";
        }
        for( Player p : Bukkit.getOnlinePlayers() ){
            ActionBar.sendActionbar(p, ChatColor.GREEN + "投票結果: " + ChatColor.RED + ou);
        }
    }

    /**
     * メッセージ系に使用するメソッド。
     * Pause中に呼ばれることはない。
     */
    public void exec() {

    }

    public void EndExec(){
        // 平和村判定
        boolean isPeaceVillage = false;
        if( JinroPlayers.getJobPlayersNumber(Job.jinro) == 0 ){
            isPeaceVillage = true;
        }

        if( isPeaceVillage ){
            // 平和村
            if( JinroPlayers.getExecutionPlayers().size() != 0 ) {
                // 処刑あった
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendTitle(ChatColor.DARK_AQUA + "==== 村人は敗北しました ====", ChatColor.WHITE + "平和村での処刑発生の為",10, 70, 10);
                }
                Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "====[村人は敗北しました]====");
            } else {
                // なかった
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendTitle(ChatColor.AQUA + "==== 村人が勝利しました ====", ChatColor.WHITE + "平和村で処刑が発生しなかった為",10, 70, 10);
                }
                Bukkit.broadcastMessage(ChatColor.AQUA + "====[村人が勝利しました]====");
            }
        } else {
            // 通常村
            // 人狼ちぇっく
            boolean isDeathJinro = false;
            for( UUID u : JinroPlayers.getExecutionPlayers()){
                PlayerData pd = JinroPlayers.getData(u);
                if( pd.getJob().equals(Job.jinro) ){
                    isDeathJinro = true;
                }
            }
            if( isDeathJinro ){
                // 人狼が死んでた => 村の勝ち
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendTitle(ChatColor.AQUA + "==== 村人が勝利しました ====", ChatColor.WHITE + "人狼の処刑成功",10, 70, 10);
                }
                Bukkit.broadcastMessage(ChatColor.AQUA + "====[村人が勝利しました]====");
            } else {
                // 人狼が死んでなかった => 狼の勝ち
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendTitle(ChatColor.RED + "==== 人狼が勝利しました ====", ChatColor.WHITE + "人狼の処刑失敗",10, 70, 10);
                }
                Bukkit.broadcastMessage(ChatColor.RED + "====[人狼が勝利しました]====");
            }
        }
        Bukkit.broadcastMessage(ChatColor.RED + "==================================");
        Bukkit.broadcastMessage(ChatColor.GOLD + "[各プレイヤーの役職]");
        StringBuilder sb = new StringBuilder();
        for( Job j : JinroPlayers.getNotJob() ){
            sb.append(j.getColor()).append("[").append(j.getJobName()).append("]");
        }
        if(sb.length() == 0){
            Bukkit.broadcastMessage(ChatColor.WHITE + "余り" + ChatColor.GREEN + ": " + ChatColor.DARK_AQUA + "[なし]");
        } else {
            Bukkit.broadcastMessage(ChatColor.WHITE + "余り" + ChatColor.GREEN + ": " + sb.toString());
        }
        for( PlayerData pd : JinroPlayers.getPlayers().values() ){
            if( pd.getPlayingType().equals(PlayerData.PlayingType.Player) ){
                String ex = "";
                String ch = "";
                if( JinroPlayers.getExecutionPlayers().contains(pd.getUUID()) ){
                    ex = ChatColor.RED.toString() + "" + ChatColor.ITALIC.toString() + ChatColor.BOLD.toString() + "(処刑)";
                }
                if( pd.getJob() != null ){
                    if( !pd.getJob().equals(pd.getFirstJob()) ){
                        Job fb = pd.getFirstJob();
                        ch = ChatColor.GRAY + "<= " + fb.getColor() + "[" + fb.getJobName() + "] ";
                    }
                    Bukkit.broadcastMessage(ChatColor.WHITE + pd.getPlayer().getName() + ChatColor.GREEN + ": " + pd.getJob().getColor() + "[" + pd.getJob().getJobName() + "] " + ch + ex);
                }
            }
        }
        Bukkit.broadcastMessage(ChatColor.RED + "==================================");
        Bukkit.broadcastMessage(ChatColor.GOLD + "[各プレイヤーの行動(簡略)]");
        for( Job j : Job.values() ){
            if( !j.isActionable() || JinroPlayers.getJobPlayersNumber(j) == 0){
                continue;
            }
            Bukkit.broadcastMessage(ChatColor.GOLD.toString() + j.getColor() + "[" + j.getJobName() + "]");
            boolean isNonAction = true;
            for( Player p : JinroPlayers.getPlayers(j) ){
                PlayerData pd = JinroPlayers.getData(p);
                if( pd.getAcitonTarget() != null ){
                    Bukkit.broadcastMessage( p.getName() + " -> " + Bukkit.getPlayer(pd.getAcitonTarget()).getName() );
                    isNonAction = false;
                }
            }
            if( isNonAction ){
                Bukkit.broadcastMessage( ChatColor.GRAY + "N/A - " + ChatColor.WHITE + "誰も行動していないようです．" );
            }
        }
        Bukkit.broadcastMessage(ChatColor.RED + "==================================");
        GameStatus.setStatus(GameStatus.End);
        GameStatus.Cycle.setCycle(GameStatus.Cycle.Ready);
    }

    @Override
    public void onChat(AsyncPlayerChatEvent e) {
        PlayerData pd = JinroPlayers.getData(e.getPlayer());
        Job coming = pd.getComingOut();
        Job.Marker ma = pd.getMarker();
        if ( !MConJinro.getMain().getConfig().getBoolean("ShowComingOut")) {
            Bukkit.broadcastMessage(ChatColor.WHITE + "<" + e.getPlayer().getName() + "> " + e.getMessage());
        } else {
            if( coming != null ){
                Bukkit.broadcastMessage(coming.getColor() + "[" +coming.getJobName2Moji() + "]" + ChatColor.WHITE + " <" + e.getPlayer().getName() + "> " + e.getMessage());
            } else if( ma != null ){
                Bukkit.broadcastMessage("[" + ma.getString() + "]" + ChatColor.WHITE + " <" + e.getPlayer().getName() + "> " + e.getMessage());
            } else {
                Bukkit.broadcastMessage(ChatColor.WHITE + "<" + e.getPlayer().getName() + "> " + e.getMessage());
            }
        }
    }
}
