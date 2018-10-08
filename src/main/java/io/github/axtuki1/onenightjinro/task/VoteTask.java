package io.github.axtuki1.onenightjinro.task;

import io.github.axtuki1.onenightjinro.GameStatus;
import io.github.axtuki1.onenightjinro.MConJinro;
import io.github.axtuki1.onenightjinro.player.JinroPlayers;
import io.github.axtuki1.onenightjinro.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
        if(pl.size() != 0) {
            for (PlayerData p : pl.values()) {
                p.getPlayer().getInventory().addItem(new ItemStack(Material.PAPER));
            }
        }
        Bukkit.broadcastMessage(ChatColor.RED + "===============[投票の時間になりました]===============");
        Bukkit.broadcastMessage(ChatColor.AQUA + "紙を手に持って投票してください");
        Bukkit.broadcastMessage(ChatColor.AQUA + "投票は「/jinro touhyou <Player>」で行えます");
        Bukkit.broadcastMessage(ChatColor.AQUA + "投票時間中のチャットはGMにしか聞こえません。");
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

}
