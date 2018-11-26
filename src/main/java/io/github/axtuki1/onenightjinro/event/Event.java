package io.github.axtuki1.onenightjinro.event;

import io.github.axtuki1.onenightjinro.GameStatus;
import io.github.axtuki1.onenightjinro.MConJinro;
import io.github.axtuki1.onenightjinro.Utility;
import io.github.axtuki1.onenightjinro.player.JinroPlayers;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.util.Vector;

import java.text.Normalizer;
import java.util.UUID;

public class Event implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e){
        String str = e.getMessage();
        // マイクラ上で表示がおかしいスペースを半角スペースに変更
        str = Utility.myReplaceAll("ㅤ", " ", str);
        str = Utility.myReplaceAll("　", " ", str);
        e.setMessage(str);
        e.setCancelled(true);

        // GM
        if(e.getPlayer().hasPermission("Jinro.GameMaster") ){
            if (Normalizer.normalize(e.getMessage(), Normalizer.Form.NFKC).toLowerCase().contains("@")) {
                String f = Utility.myReplaceAll("@", "", e.getMessage());
                f = Utility.myReplaceAll("＠", "", f);
                e.setMessage(ChatColor.BOLD + f);
            }
            Bukkit.broadcastMessage(ChatColor.YELLOW + "[GM] <" + e.getPlayer().getName() + "> " + e.getMessage());
            return;
        }

        // 観戦
        if(JinroPlayers.getSpecPlayers().contains(e.getPlayer())){
            for (Player p : JinroPlayers.getSpecPlayers()) {
                p.sendMessage(ChatColor.WHITE + "[観戦] <" + e.getPlayer().getName() + "> " + e.getMessage());
                return;
            }
        }

        try{
            // プレイヤー
            if(MConJinro.getTask() != null && GameStatus.getStatus().equals(GameStatus.Playing)){
                MConJinro.getTask().onChat(e);
                return;
            }
        } catch (Exception e1 ){
            e.setCancelled(true);
            e.getPlayer().sendMessage(MConJinro.getPrefix() + ChatColor.RED + "チャット処理時に例外が発生しました....");
            Bukkit.broadcast("[例外:" + e1 + "] <" + e.getPlayer().getName() + "> " + e.getMessage(), "axtuki1.Jinro.GameMaster");
            Bukkit.broadcast("[例外:" + e1 + "] " + e1.getLocalizedMessage(), "axtuki1.Jinro.GameMaster");
            e1.printStackTrace();
        }

        Bukkit.broadcastMessage(ChatColor.WHITE + "<" + e.getPlayer().getName() + "> " + e.getMessage());

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent e){
        if( JinroPlayers.getExecutionPlayers().contains(e.getEntity().getUniqueId()) && GameStatus.getStatus().equals(GameStatus.Playing) ){
            e.setDeathMessage(ChatColor.RED + e.getEntity().getName() + "は処刑されました。");
            e.getEntity().getPlayer().setGlowing(false);
            e.getEntity().getPlayer().setPlayerListName(ChatColor.BLACK + "[霊界] " + e.getEntity().getName() + " ");
            e.getEntity().setVelocity(new Vector());
            e.getEntity().spigot().respawn();
        } else {
            e.setDeathMessage(null);
            e.getEntity().getPlayer().setGlowing(false);
            e.getEntity().setVelocity(new Vector());
            e.getEntity().spigot().respawn();
            e.setKeepInventory(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        if (JinroPlayers.getExecutionPlayers().contains(e.getPlayer().getUniqueId()) && GameStatus.getStatus().equals(GameStatus.Playing)) {
            e.setRespawnLocation(MConJinro.getReikaiLoc());
            return;
        }
        e.setRespawnLocation(MConJinro.getRespawnLoc());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDamageByEntity(EntityDamageByEntityEvent e) {
        if( e.getDamager().hasPermission("Jinro.GameMaster") ) {
            return;
        }

        if (e.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow)e.getDamager();
            arrow.setCritical(true);
            e.setDamage(5);
            LivingEntity p = (LivingEntity) e.getEntity();
            p.setNoDamageTicks(0);
            p.setVelocity(new Vector());
            return;
        }

        // 被ダメージ者が受刑者の場合
        if( JinroPlayers.getExecutionPlayers().contains(e.getEntity().getUniqueId()) ){
            if(e.getDamager().getType() == EntityType.FALLING_BLOCK || e.getDamager().getType() == EntityType.CREEPER){
                e.setDamage(1000);
                return;
            }
        }
    }
}
