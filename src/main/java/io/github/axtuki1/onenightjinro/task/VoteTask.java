package io.github.axtuki1.onenightjinro.task;

import io.github.axtuki1.onenightjinro.GameStatus;
import io.github.axtuki1.onenightjinro.MConJinro;
import io.github.axtuki1.onenightjinro.player.JinroPlayers;
import io.github.axtuki1.onenightjinro.player.Job;
import io.github.axtuki1.onenightjinro.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class VoteTask extends BaseTask {

    VoteTask(MConJinro pl ){
        super(pl);
    }

    @Override
    public void start() {
        super.start();
        GameStatus.Cycle.setCycle(GameStatus.Cycle.Vote);
        MConJinro.getRespawnLoc().getWorld().setTime(12250);
        HashMap<UUID, PlayerData> pl = JinroPlayers.getPlayers();

        for(Player p : Bukkit.getOnlinePlayers()){
            PlayerData pd = JinroPlayers.getData( p );
            if(pd.getPlayingType().equals(PlayerData.PlayingType.Player)){
                p.getPlayer().getInventory().addItem(new ItemStack(Material.PAPER));
            }
        }

        Bukkit.broadcastMessage(ChatColor.RED + "===============[投票の時間になりました]===============");
        Bukkit.broadcastMessage(ChatColor.AQUA + "紙を手に持って投票してください");
        Bukkit.broadcastMessage(ChatColor.AQUA + "投票は「/jinro touhyou <Player>」で行えます");
        Bukkit.broadcastMessage(ChatColor.AQUA + "投票時間中のチャットはGMにしか聞こえません。");

        MConJinro.sendGameMaster(ChatColor.YELLOW + "===============[GMの操作]===============", false);
        MConJinro.sendGameMaster(ChatColor.AQUA + "/jinro_ad touhyou check で投票状況を確認できます。", false);
        MConJinro.sendGameMaster(ChatColor.AQUA + "/jinro_ad touhyou open で開票できます。", false);
    }

    /**
     * 表示の更新に使うメソッド。
     * execと違ってタイマーが可動している間は呼ばれる。
     */
    public void updateView() {

    }

    /**
     * メッセージ系に使用するメソッド。
     * Pause中に呼ばれることはない。
     */
    public void exec() {
        boolean isAllPlayerVoted = true;
        for( PlayerData pd : JinroPlayers.getPlayers().values() ){
            if( pd.getVoteTarget() == null ){
                isAllPlayerVoted = false;
            }
        }
        if( isAllPlayerVoted ){
            end();
        }
    }

    /**
     * タスク終了時に呼ばれる。
     */
    public void EndExec() {

    }

    @Override
    public void onChat(AsyncPlayerChatEvent e) {
        Job job = JinroPlayers.getData(e.getPlayer()).getFirstJob();
        MConJinro.sendGameMaster(job.getColor() + "[" + job.getJobName2Moji() + "] <" + e.getPlayer().getName() + "> " + e.getMessage());
        e.getPlayer().sendMessage( job.getColor() + "[" + job.getJobName2Moji() + "] <" + e.getPlayer().getName() + "> " + e.getMessage() );
    }

}
