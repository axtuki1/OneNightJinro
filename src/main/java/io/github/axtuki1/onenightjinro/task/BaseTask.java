package io.github.axtuki1.onenightjinro.task;

import io.github.axtuki1.onenightjinro.GameStatus;
import io.github.axtuki1.onenightjinro.MConJinro;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class BaseTask extends BukkitRunnable {

    private MConJinro plugin;

    private BukkitTask task;

    private boolean isPaused = false;

    public enum Status {
        READY,
        RUNNING,
        PAUSED,;
    }

    BaseTask(MConJinro pl ){
        this.plugin = pl;
    }

    public void run() {
        updateView();
        if( !isPaused ){
            exec();
        }
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
    public void exec() { }

    /**
     * タスク終了時に呼ばれる。
     */
    public void EndExec() {
    }

    public boolean isPaused() {
        return isPaused;
    }

    /**
     * 現在のステータスを返す
     * @return ステータス
     */
    public Status getStatus() {

        if ( isPaused ) {
            return Status.PAUSED;
        }
        if  ( task != null ) {
            return Status.RUNNING;
        }
        return Status.READY;
    }

    /**
     * タスクを一時停止する
     */
    public void pause(){
        isPaused = true;
        GameStatus.setStatus(GameStatus.Pause);
    }

    /**
     * 一時停止にあるタスクを再開する
     */
    public void resume() {
        if ( isPaused ) {
            isPaused = false;
            GameStatus.setStatus(GameStatus.Playing);
        }
    }

    /**
     * タスクを開始する。
     */
    public void start(){
        if( task == null ){
            task = runTaskTimer(plugin, 20, 20);
        }
    }

    /**
     * タスクを開始する。
     */
    public void start(int periodTick){
        if( task == null ){
            task = runTaskTimer(plugin, 20, periodTick);
        }
    }

    /**
     * タスクを開始する。
     */
    public void start(int delay, int periodTick){
        if( task == null ){
            task = runTaskTimer(plugin, delay, periodTick);
        }
    }

    /**
     * タスクを強制終了する。
     * このメソッドで終了した場合、再開はできない。
     */
    public void stop(){
        if( task != null ){
            cancel();
            task = null;
        }
    }

    /**
     * タスクを終了する。
     * このメソッドで終了した場合、再開はできない。
     */
    public void end() {
        stop();
        EndExec();
    }

    /**
     * チャットイベント時に呼ばれます。
     */
    public void onChat(AsyncPlayerChatEvent e){

    }

    public MConJinro getPlugin() {
        return plugin;
    }
}
