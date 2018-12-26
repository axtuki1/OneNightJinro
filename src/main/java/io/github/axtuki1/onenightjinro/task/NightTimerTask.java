package io.github.axtuki1.onenightjinro.task;

import io.github.axtuki1.onenightjinro.GameStatus;
import io.github.axtuki1.onenightjinro.JinroConfig;
import io.github.axtuki1.onenightjinro.MConJinro;
import io.github.axtuki1.onenightjinro.Utility;
import io.github.axtuki1.onenightjinro.player.JinroPlayers;
import io.github.axtuki1.onenightjinro.player.Job;
import io.github.axtuki1.onenightjinro.player.PlayerData;
import io.github.axtuki1.onenightjinro.scoreboard.JinroScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class NightTimerTask extends BaseTimerTask {

    public NightTimerTask(MConJinro pl, int sec) {
        super(pl, sec);
    }

    @Override
    public void start() {
        super.start();
        GameStatus.Cycle.setCycle(GameStatus.Cycle.Night);
        Bukkit.broadcastMessage(ChatColor.RED + "===============[恐ろしい夜がやって来ました]===============");
        Bukkit.broadcastMessage(ChatColor.YELLOW + "各役職の行動を開始してください。");
        MConJinro.getRespawnLoc().getWorld().setTime(15000);
        for( Player p : Bukkit.getOnlinePlayers() ){
            PlayerData pd = JinroPlayers.getData(p);
            if( pd.getPlayingType().equals(PlayerData.PlayingType.Player) ){
                Job j = pd.getJob();
                pd.getPlayer().sendMessage( j.getColor() + "あなたは " + j.getJobName() + " です。");
                if( pd.getFirstJob().equals(Job.jinro) ){
                    pd.getPlayer().sendMessage( Job.jinro.getDescription(true) );
                } else {
                    pd.getPlayer().sendMessage( j.getDescription() );
                }
                p.setPlayerListName(p.getName() + " ");
            } else if( pd.getPlayingType().equals(PlayerData.PlayingType.Spectator) ){
                pd.getPlayer().sendMessage( ChatColor.RED + "=====" + ChatColor.WHITE +  " 観戦者として参加しています " + ChatColor.RED + "=====");
                pd.getPlayer().sendMessage( ChatColor.AQUA + "このセッション中のチャットは観戦者全体にのみ聞こえます。" );
                p.setPlayerListName(ChatColor.GRAY + "" + ChatColor.ITALIC + "[観戦] " + p.getName() + " ");
                p.setGameMode(GameMode.SPECTATOR);
            } else if( pd.getPlayingType().equals(PlayerData.PlayingType.GameMaster) ){
                pd.getPlayer().sendMessage( ChatColor.RED + "=====" + ChatColor.WHITE +  " GMとして参加しています " + ChatColor.RED + "=====");
                pd.getPlayer().sendMessage( ChatColor.YELLOW + "GMのチャットは状況関係なく全体に発信されます。" );
                pd.getPlayer().sendMessage( ChatColor.YELLOW + "指定役全体に送信する場合は/cコマンドを使用してください。" );
                p.setPlayerListName(ChatColor.YELLOW + "[GM] " + p.getName() + " ");
            }
        }
    }

    @Override
    public void beforeView() {
        JinroScoreboard.getScoreboard().resetScores(
                Utility.getColor(0, getSecondsMax()) + "残り時間: 0秒"
        );
        JinroScoreboard.getScoreboard().resetScores(
                Utility.getColor(getSeconds(), getSecondsMax()) + "残り時間: "+ getSeconds() +"秒"
        );
    }


    public void EndExec(){
        BaseTimerTask dt = new DiscussionTimerTask(
                getPlugin(),
                MConJinro.getMain().getConfig().getInt(
                        JinroConfig.DiscussionTime.getPath()
                )
        );
        dt.start();
        MConJinro.setTask(dt);
    }

    @Override
    public void exec() {

    }

    @Override
    public void onChat(AsyncPlayerChatEvent e) {
        Job job = JinroPlayers.getData(e.getPlayer()).getFirstJob();
        MConJinro.sendGameMaster(job.getColor() + "[" + job.getJobName2Moji() + "] <" + e.getPlayer().getName() + "> " + e.getMessage());
        e.getPlayer().sendMessage( job.getColor() + "[" + job.getJobName2Moji() + "] <" + e.getPlayer().getName() + "> " + e.getMessage() );
    }
}
