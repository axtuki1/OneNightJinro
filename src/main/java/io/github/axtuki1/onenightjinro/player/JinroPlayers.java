package io.github.axtuki1.onenightjinro.player;

import io.github.axtuki1.onenightjinro.MConJinro;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.util.*;

public class JinroPlayers {

    static HashMap<UUID, PlayerData> players;
    static HashMap<UUID, PlayerData> specPlayers;
    static List<Job> notJob;
    static List<UUID> executionPlayers;

    public static void init(){
        players = new HashMap<UUID, PlayerData>();
        notJob = new ArrayList<Job>();
        specPlayers = new HashMap<UUID, PlayerData>();
        executionPlayers = new ArrayList<UUID>();
    }

    public static List<UUID> getExecutionPlayers() {
        return executionPlayers;
    }

    public static void setExecutionPlayers(List<UUID> executionPlayers) {
        JinroPlayers.executionPlayers = executionPlayers;
    }

    public static void addPlayer(Player p , Job job ) {
        players.put(p.getUniqueId(), new PlayerData(p, job));
    }

    public static void addPlayer( Player p , PlayerData playerData ) {
        players.put(p.getUniqueId(), playerData);
    }

    public static void removePlayer( Player p ) {
        players.remove(p);
    }

    public static void addSpecPlayer( Player p ) {
        specPlayers.put(p.getUniqueId(), new PlayerData(p, true));
    }

    public static void addSpecPlayer( Player p , PlayerData playerData ) {
        specPlayers.put(p.getUniqueId(), playerData);
    }

    public static void removeSpecPlayer( Player p ) {
        specPlayers.remove(p);
    }

    public static HashMap<UUID, PlayerData> getPlayers() {
        return players;
    }

    public static HashMap<UUID, PlayerData> getSpecPlayers() {
        return specPlayers;
    }

    public static List<UUID> getNonJoinPlayers() {
        Set<UUID> join = getPlayers().keySet();
        ArrayList<UUID> all = new ArrayList<UUID>();
        for( Player p : Bukkit.getOnlinePlayers() ){
            all.add(p.getUniqueId());
        }
        all.removeAll(join);
        return all;
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

    public static boolean equalsFirstJob(Player player, Job job){
        PlayerData pd = JinroPlayers.getData(player);
        if( pd == null ){
            return false;
        } else {
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

    public static ArrayList<Player> getPlayers( Job job ) {
        ArrayList<Player> p = new ArrayList<Player>();
        for( PlayerData pd : getPlayers().values() ){
            if( pd.getJob().equals(job) ){
                p.add( pd.getPlayer() );
            }
        }
        return p;
    }

    public static PlayerData getData(Player p){
        return players.get(p.getUniqueId());
    }

    public static PlayerData getData(UUID u){
        return players.get(u);
    }

    public static void setData(Player p, PlayerData pd){
        setData(p.getUniqueId(), pd);
    }

    public static void setData(UUID u, PlayerData pd){
        players.replace(u, pd);
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
