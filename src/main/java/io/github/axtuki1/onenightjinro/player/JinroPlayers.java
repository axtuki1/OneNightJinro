package io.github.axtuki1.onenightjinro.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * プレイヤーデータを管理するやつ
 */
public class JinroPlayers {

    static HashMap<UUID, PlayerData> players;
    static List<Job> notJob;
    static List<UUID> executionPlayers;

    public static void init(){
        players = new HashMap<UUID, PlayerData>();
        notJob = new ArrayList<Job>();
        executionPlayers = new ArrayList<UUID>();
    }

    public static List<UUID> getExecutionPlayers() {
        return executionPlayers;
    }

    public static void setExecutionPlayers(List<UUID> executionPlayers) {
        JinroPlayers.executionPlayers = executionPlayers;
    }

    public static void addPlayer(UUID uuid , Job job ) {
        players.put(uuid, new PlayerData(uuid, job));
    }

    public static void addPlayer(Player p , Job job ) {
        addPlayer(p.getUniqueId(), job);
    }

    public static void addPlayer( UUID uuid , PlayerData playerData ) {
        players.put(uuid, playerData);
    }

    public static void removePlayer( Player p ) {
        players.remove(p.getUniqueId());
    }

    /**
     * 登録済みのプレイヤーを返します。
     */
    public static HashMap<UUID, PlayerData> getPlayers() {
        return players;
    }

    public static HashMap<UUID, PlayerData> getJoinedPlayers() {
        HashMap<UUID, PlayerData> all = getPlayers();
        HashMap<UUID, PlayerData> joined = new HashMap<UUID, PlayerData>();
        for( UUID u : all.keySet() ){
            PlayerData p = all.get(u);
            if( p.getPlayingType().equals(PlayerData.PlayingType.Player) && p.getJob() != null ){
                joined.put( u, p );
            }
        }
        return joined;
    }

    public static HashMap<UUID, PlayerData> getNonJoinPlayers() {
        HashMap<UUID, PlayerData> all = getPlayers();
        HashMap<UUID, PlayerData> nonjoin = new HashMap<UUID, PlayerData>();
        for( Player p : Bukkit.getOnlinePlayers() ){
            PlayerData pd = JinroPlayers.getData(p, true);
            if( pd == null ){
                nonjoin.put(p.getUniqueId(), new PlayerData(p.getUniqueId()));
            } else if( pd.getJob() == null ) {
                nonjoin.put(p.getUniqueId(), pd);
            }
        }
        return nonjoin;
    }

    /**
     * 余っている役を追加します。
     * 重複可能です。
     * @param job 余っている役
     */
    public static void addNotJob(Job job){
        notJob.add(job);
    }

    /**
     * 余っている役から指定役を削除します。
     * 重複していても1つのみ削除します。
     * @param job 削除したい役
     */
    public static void removeNotJob(Job job){
        notJob.remove(job);
    }

    /**
     * 余っている役を返します。
     */
    public static List<Job> getNotJob() {
        return notJob;
    }

    public static boolean equalsJob(Player player, Job job){
        PlayerData pd = JinroPlayers.getData(player);
        if( pd == null ){
            return false;
        } else {
            if( !pd.getJob().equals(job) ){
                return false;
            }
        }
        return true;
    }

    /**
     * 指定したプレイヤーの初期役が指定した役であるか判定します。
     * @param player 対象プレイヤー
     * @param job 確認したい役
     * @return 指定役を持っているか
     */
    public static boolean equalsFirstJob(Player player, Job job){
        PlayerData pd = JinroPlayers.getData(player);
        if( pd == null ){
            return false;
        } else {
            if(pd.getFirstJob() == null){
                return false;
            }
            if( !pd.getFirstJob().equals(job) ){
                return false;
            }
        }
        return true;
    }

    /**
     * 指定した役に開始時所属していたプレイヤーを返します。
     * 怪盗などの影響を受けないプレイヤー一覧です。
     * @param job 役職
     * @return 指定した役に開始時所属していたプレイヤー
     */
    public static ArrayList<Player> getFirstJobPlayers( Job job ) {
        ArrayList<Player> p = new ArrayList<Player>();
        for( PlayerData pd : getPlayers().values() ){
            if( pd.getFirstJob().equals(job) ){
                p.add( pd.getPlayer() );
            }
        }
        return p;
    }

    /**
     * 指定した役に所属しているプレイヤーを返します。
     * 初期役ではなく現在の役。
     * @param job
     * @return
     */
    public static ArrayList<Player> getPlayers( Job job ) {
        ArrayList<Player> p = new ArrayList<Player>();
        for( PlayerData pd : getPlayers().values() ){
            if( pd.getJob().equals(job) ){
                p.add( pd.getPlayer() );
            }
        }
        return p;
    }

    public static ArrayList<Player> getSpecPlayers() {
        ArrayList<Player> p = new ArrayList<>();
        for( PlayerData pd : getPlayers().values() ){
            if( pd.getPlayingType().equals(PlayerData.PlayingType.Spectator) ){
                p.add( pd.getPlayer() );
            }
        }
        return p;
    }

    /**
     * 指定プレイヤーのPlayerDataを返します。
     * nullだった場合空のPlayerDataを返します。
     * @param p プレイヤー
     */
    public static PlayerData getData(Player p){
        PlayerData pd = players.get(p.getUniqueId());
        if( pd == null ) return new PlayerData(p.getUniqueId());
        return pd;
    }

    /**
     * 指定プレイヤーのPlayerDataを"そのまま"返します
     * @param p
     * @return
     */
    public static PlayerData getData(Player p, boolean isRaw){
        if(isRaw) return players.get(p.getUniqueId());
        return getData(p);
    }

    public static PlayerData getData(UUID u){
        PlayerData pd = players.get(u);
        if( pd == null ) return new PlayerData( u );
        return pd;
    }

    public static PlayerData getData(UUID u, boolean isRaw){
        if(isRaw) return players.get(u);
        return getData(u);
    }

    public static void setData(Player p, PlayerData pd){
        setData(p.getUniqueId(), pd);
    }

    public static void setData(UUID u, PlayerData pd){
        players.put(u, pd);
    }

    public static HashMap<Job, Integer> getJobPlayersNumber(){
        HashMap<Job, Integer> out = new HashMap<Job, Integer>();
        for( Job job : Job.values() ){
            out.put( job, getPlayers(job).size() );
        }
        return out;
    }

    public static int getJobPlayersNumber(Job job){
        return getPlayers(job).size();
    }

}
