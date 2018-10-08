package io.github.axtuki1.onenightjinro.task;

import io.github.axtuki1.onenightjinro.GameStatus;
import io.github.axtuki1.onenightjinro.MConJinro;
import io.github.axtuki1.onenightjinro.Utility;
import io.github.axtuki1.onenightjinro.scoreboard.JinroScoreboard;
import org.bukkit.scheduler.BukkitTask;

public class BaseTimerTask extends BaseTask {

    // ExpTimerを見て書いてたけどあまりうまく行かなかった....
    // しょーがないからデクリメントする方法でとりあえず。
    // 現在時刻から算出する方法もやってはみたい

    private static final long OFFSET = 1150;
    private static final long DEC_OFFSET = 0;

    private MConJinro plugin;

    private BukkitTask task;

    private int secondsRest;
    private int secondsMax;

    private long tickBase;

    private boolean isPaused = false;

    BaseTimerTask( MConJinro pl, int sec ){
        super(pl);
        this.plugin = pl;
        secondsRest = sec + (int)DEC_OFFSET;
        secondsMax = sec;
        tickBase = System.currentTimeMillis() + secondsRest * 1000 + OFFSET;
        MConJinro.setTask(this);
    }

    public void run() {
        beforeView();
        updateTime();
        updateView();
        if( !isPaused ){
            if ( secondsRest <= 0 ) {
                EndExec();
                stop();
                return;
            }
            exec();
        }
    }

    /**
     * 表示の更新に使うメソッド。
     * execと違ってタイマーが可動している間は呼ばれる。
     * 秒数の更新前に呼ばれる。
     */
    public void beforeView() {
        JinroScoreboard.getScoreboard().resetScores(
                Utility.getColor(getSeconds(), getSecondsMax()) + "残り時間: "+ getSeconds() +"秒"
        );
    }

    /**
     * 表示の更新に使うメソッド。
     * execと違ってタイマーが可動している間は呼ばれる。
     * 秒数の更新後に呼ばれる。
     */
    public void updateView() {
        JinroScoreboard.getInfoObj().getScore(
                Utility.getColor(getSeconds(), getSecondsMax()) + "残り時間: " + getSeconds() + "秒"
        ).setScore(0);
    }

    /**
     * メッセージ系に使用するメソッド。
     * Pause中に呼ばれることはない。
     * これはTimerEndExecが呼ばれるときは呼ばれない。
     */
    public void exec() { }

    /**
     * タイマーが終了した(0になった)ときに呼ばれるメソッド。
     */
    public void EndExec(){

    }

    private void updateTime() {
        if( isPaused ) {
            return;
        }
        long currentSec = -1;
        if ( !isPaused ) {
//            long current = System.currentTimeMillis();
//            currentSec = tickBase - current;
//            secondsRest = (int)(currentSec/1000);
            secondsRest--;
        }
//            String msg;
//            float percent = (float)secondsRest / (float)secondsMax;
//            msg = String.format(
//                    "rest %dsec (%.2f%%), millisec %d",
//                    secondsRest, percent, currentSec);
//            plugin.getLogger().info(msg);
    }

    public boolean isPaused() {
        return isPaused;
    }

    public int getSeconds() {
        return secondsRest;
    }

    public int getSecondsMax() {
        return secondsMax;
    }

    /**
     * 現在のステータスを返す
     * @return ステータス
     */
    public Status getStatus() {

        if ( isPaused ) {
            return Status.PAUSED;
        }
        if  ( secondsRest > 0 ) {
            return Status.RUNNING;
        }
        return Status.READY;
    }

    /**
     * タイマーを一時停止する
     */
    public void pause(){
        isPaused = true;
        GameStatus.setStatus(GameStatus.Pause);
    }

    /**
     * 一時停止にあるタイマーを再開する
     */
    public void resume() {
        if ( isPaused ) {
            isPaused = false;
            long current = System.currentTimeMillis();
            tickBase = current + secondsRest * 1000 + OFFSET;
            GameStatus.setStatus(GameStatus.Playing);
        }
    }

    /**
     * タイマーを開始する。
     */
    public void start(){
        if( task == null ){
            task = runTaskTimer(plugin, 20L, 20L);
        }
        JinroScoreboard.getScoreboard().resetScores(
                Utility.getColor(0, getSecondsMax()) + "残り時間: 0秒"
        );
        JinroScoreboard.getInfoObj().getScore(
                Utility.getColor(getSeconds(), getSecondsMax()) + "残り時間: " + getSeconds() + "秒"
        ).setScore(0);
    }

    /**
     * タイマーを強制終了する。
     * このメソッドで終了した場合、再開はできない。
     */
    public void stop(){
        if( task != null ){
            cancel();
            task = null;
        }
    }

    /**
     * 終了時処理を実行し、タイマーを終了する。
     * このメソッドで終了した場合、再開はできない。
     */
    public void end(){
        beforeView();
        secondsRest = 1;
        updateTime();
        updateView();
        if( !isPaused ){
            if ( secondsRest <= 0 ) {
                EndExec();
                stop();
                return;
            }
            exec();
        }
    }
}
