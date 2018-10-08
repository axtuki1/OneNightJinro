package io.github.axtuki1.onenightjinro.player;

import io.github.axtuki1.onenightjinro.GameStatus;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerData {

    private Job firstJob;
    private final UUID player;
    private boolean isAction, isSpec;
    private UUID acitonTarget, voteTarget;
    private Job job;
    private Job comingOut;
    private Job.Marker marker;

    public PlayerData(Player player, Job job){
        this.player = player.getUniqueId();
        isAction = false;
        acitonTarget = null;
        this.firstJob = job;
        this.job = job;
        this.comingOut = null;
        isSpec = false;
        marker = null;
    }

    public PlayerData(Player player, boolean isSpectator) {
        this.player = player.getUniqueId();
        isAction = false;
        acitonTarget = null;
        this.firstJob = null;
        this.job = null;
        this.comingOut = null;
        isSpec = isSpectator;
        marker = null;
    }

    /**
     * このPlayerDataに振られているプレイヤーのUUIDを返す。
     * ログアウト等でPlayerが利用できなくなる事を防ぐ為。
     */
    public UUID getUUID() {
        return player;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(player);
    }

    public boolean isSpec() {
        return isSpec;
    }

    public void setSpec(boolean spec) {
        isSpec = spec;
    }

    /**
     * 現在割り当てられている役職を返す。
     */
    public Job getJob() {
        return job;
    }

    /**
     * 役職を変更する。
     */
    public void setJob(Job job) {
        this.job = job;
        if( this.firstJob == null || GameStatus.getStatus().equals(GameStatus.Ready)){
            this.firstJob = this.job;
        }
    }

    /**
     * 初期に割り当てられた役職を返す。
     * これは怪盗などのすり替え等に影響しない。
     */
    public Job getFirstJob() {
        return firstJob;
    }



    public Job getComingOut() {
        return comingOut;
    }

    public void setComingOut(Job comingOut) {
        this.comingOut = comingOut;
        this.marker = null;
    }

    public void setMarker(Job.Marker marker) {
        this.marker = marker;
        this.comingOut = null;
    }

    public Job.Marker getMarker() {
        return marker;
    }

    /**
     * 行動の状態を返す。
     */
    public boolean isAction() {
        return isAction;
    }

    /**
     * 行動の状態を変更する。
     * @param action
     */
    public void setAction(boolean action) {
        isAction = action;
    }

    /**
     * 行動を起こした時ターゲットとなるプレイヤーのUUIDを返す。
     * いない場合はnullを返す。
     * @return
     */
    public UUID getAcitonTarget() {
        return acitonTarget;
    }

    /**
     * 行動を起こした時ターゲットとなるプレイヤーのUUIDを変更する。
     * @param acitonTarget
     */
    public void setAcitonTarget(UUID acitonTarget) {
        this.acitonTarget = acitonTarget;
    }

    /**
     * 投票のターゲットとなるプレイヤーのUUIDを返す。
     * いない場合はnullを返す。
     * @return
     */
    public UUID getVoteTarget() {
        return voteTarget;
    }

    /**
     * 行動を起こした時ターゲットとなるプレイヤーのUUIDを変更する。
     * @param voteTarget
     */
    public void setVoteTarget(UUID voteTarget) {
        this.voteTarget = voteTarget;
    }
}