package io.github.axtuki1.onenightjinro.player;

import com.sun.org.apache.xerces.internal.xs.StringList;
import io.github.axtuki1.onenightjinro.GameStatus;
import io.github.axtuki1.onenightjinro.MConJinro;
import io.github.axtuki1.onenightjinro.Utility;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class RandomSelect implements Listener {

    Inventory inv;
    LinkedHashMap<Job, Integer> jobSet;
    int currentPage;
    boolean giveingLock, isGameMaster, isKeepSetting;

    enum Config {
        SenderIsGameMaster,
        KeepSetting
    }

    public RandomSelect(){
        if( inv == null || !isKeepSetting ){
            init();
            RandomConfig.init();
            HashMap<RandomSelect.Config, Boolean> rc = new HashMap<RandomSelect.Config, Boolean>();
            rc.put(Config.KeepSetting, isKeepSetting);
            rc.put(Config.SenderIsGameMaster, isGameMaster);
            RandomConfig.setRandomConfig(rc);
        }
    }

    public void init(){
        currentPage = 1;
        giveingLock = false;
        isGameMaster = true;
        isKeepSetting = true;
        jobSet = new LinkedHashMap<Job, Integer>();
        for( Job j : Job.getDefaultSort() ){
            jobSet.put(j,0);
        }
        inv = Bukkit.createInventory(null,9 * 4, "役数の設定");
        inv = generateInventory();
    }

    public void openInventory( Player p ) {
        if( inv == null || !isKeepSetting ){
            init();
        }
        jobSet = RandomConfig.getRandomJobSet();
        isKeepSetting = RandomConfig.getRandomConfig().get(Config.KeepSetting);
        isGameMaster = RandomConfig.getRandomConfig().get(Config.SenderIsGameMaster);
        inv = generateInventory();
        p.openInventory(inv);
    }

    public Inventory generateInventory(){
        int baseX = 0, baseY = 0;

        int indexStart = (currentPage - 1) * 9;

        List<Job> jobs;

        if( jobSet.size() <= 9 ){
            jobs = new ArrayList<>( jobSet.keySet() );
        } else {
            jobs = new ArrayList<>(new ArrayList<>( jobSet.keySet() ).subList( indexStart, (indexStart + 9) ));
        }

        ItemStack item;
        ItemMeta meta;

        for( Job j : jobs ) {

            int currentCount = jobSet.get(j);

            // Now
            if (currentCount <= 0) {
                item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
            } else {
                item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 3);
                item.setAmount(currentCount);
            }
            meta = item.getItemMeta();
            meta.setDisplayName(j.getColor() + j.getJobName());
            ArrayList<String> lore = new ArrayList<String>(Arrays.asList(j.getDescription()));
            if( currentCount <= 0 ){
                lore.add(0, ChatColor.WHITE.toString() + ChatColor.BOLD + "この役職には割り振られません");
            } else {
                lore.add(0, ChatColor.WHITE.toString() + ChatColor.BOLD + currentCount + "人が割り振られます");
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(baseX + ((baseY + 1) * 9), item);

            // Plus
            item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);
            meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.YELLOW + j.getJobName() + "の人数を増やす");
            item.setItemMeta(meta);
            inv.setItem(baseX + baseY, item);

            // Minus
            item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
            meta = item.getItemMeta();
            if (currentCount <= 0) {
                meta.setDisplayName(ChatColor.RED + "これ以上減らせません！");
            } else {
                meta.setDisplayName(ChatColor.YELLOW + j.getJobName() + "の人数を減らす");
            }
            item.setItemMeta(meta);
            inv.setItem(baseX + ((baseY + 1) * 18), item);

//            Bukkit.broadcastMessage("Job: " + j.getJobID() + "===============================");
//            Bukkit.broadcastMessage( "Plus: " + (baseX + baseY) );
//            Bukkit.broadcastMessage( "Now: " + (baseX + ((baseY + 1) * 9)) );
//            Bukkit.broadcastMessage( "Minus: " + (baseX + ((baseY + 1) * 18)) );
            if (baseX >= 8) {
                baseY++;
                baseX = 0;
            } else {
                baseX++;
            }
        }

        item = new ItemStack(Material.NETHER_STAR, 1);
        meta = item.getItemMeta();
        boolean isAllZero = true;
        for( int i : jobSet.values() ){
            if( i != 0 ){
                isAllZero = false;
            }
        }
        if( isAllZero ){
            meta.setDisplayName(ChatColor.RED + "配役してないですよね...");
        } else {
            meta.setDisplayName(ChatColor.GREEN + "役を配る");
        }
        item.setItemMeta(meta);
        inv.setItem(35, item);

        if( isGameMaster ){
            item = new ItemStack(Material.BOOK, 1);
            meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.YELLOW + "あなたの参加状態: GM");
            meta.setLore(new ArrayList<String>(){
                {
                    add(ChatColor.WHITE + "クリックで『参加者』になります");
                }
            });
        } else {
            item = new ItemStack(Material.PAPER, 1);
            meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.YELLOW + "あなたの参加状態: 参加者");
            meta.setLore(new ArrayList<String>(){
                {
                    add(ChatColor.WHITE + "クリックで『GM』になります");
                }
            });
        }
        item.setItemMeta(meta);
        inv.setItem(34, item);

        if( isKeepSetting ){
            item = new ItemStack(Material.WOOL, 1, (short)5);
            meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + "設定の保存: ON");
        } else {
            item = new ItemStack(Material.WOOL, 1, (short) 14);
            meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.YELLOW + "設定の保存: OFF");
        }
        item.setItemMeta(meta);
        inv.setItem(33, item);

        item = new ItemStack(Material.EYE_OF_ENDER, 1);
        meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "オールクリア");
        item.setItemMeta(meta);
        inv.setItem(32, item);

        return inv;
    }

    public Inventory getInv() {
        return inv;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJobGuiClick(InventoryClickEvent event) {
//        Bukkit.broadcastMessage(event.getCurrentItem().getType().toString());
        if( event.getInventory().getTitle().equals(inv.getTitle()) && event.getClickedInventory() != null ){
            if( event.getClickedInventory().getTitle().equals(inv.getTitle()) ){
                ItemStack cu = event.getCurrentItem();
                int slot = event.getSlot();
                if(cu.getType().equals(Material.STAINED_GLASS_PANE)){
                    Job selectedJob = null;
                    Integer nowJobCount = 0;
                    switch (cu.getData().getData()) {
                        case 5:
                            String selectedJobName = event.getClickedInventory().getItem(slot + 9).getItemMeta().getDisplayName().substring(2);
                            for( Job j : Job.values() ){
                                if( selectedJobName.equalsIgnoreCase(j.getJobName()) ){
                                    selectedJob = j;
                                    break;
                                }
                            }
                            nowJobCount = jobSet.get(selectedJob);
                            jobSet.put(selectedJob, (jobSet.get(selectedJob) + 1));
                            inv = generateInventory();
                            event.getWhoClicked().openInventory(inv);
                            break;
                        case 14:
                            selectedJobName = event.getClickedInventory().getItem(slot - 9).getItemMeta().getDisplayName().substring(2);
                            for( Job j : Job.values() ){
                                if( selectedJobName.equalsIgnoreCase(j.getJobName()) ){
                                    selectedJob = j;
                                    break;
                                }
                            }
                            nowJobCount = jobSet.get(selectedJob);
                            if(nowJobCount > 0){
                                jobSet.put(selectedJob, (nowJobCount - 1));
                            } else {
                                Player p = ((Player)event.getWhoClicked());
                                p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1F, 0);
                            }
                            inv = generateInventory();
                            event.getWhoClicked().openInventory(inv);
                            break;
                    }
                } else if( isItemEquals( cu, Material.BOOK, ChatColor.YELLOW.toString() + "あなたの参加状態: GM") ){
                    isGameMaster = false;
                    inv = generateInventory();
                    event.getWhoClicked().openInventory(inv);
                } else if( isItemEquals( cu, Material.PAPER, ChatColor.YELLOW.toString() + "あなたの参加状態: 参加者") ){
                    isGameMaster = true;
                    inv = generateInventory();
                    event.getWhoClicked().openInventory(inv);
                } else if( isItemEquals( cu, Material.NETHER_STAR, ChatColor.GREEN.toString() + "役を配る") ){
                    giveJob( ((Player)event.getWhoClicked()) );
                } else if( isItemEquals( cu, Material.WOOL, ChatColor.GREEN.toString() + "設定の保存: ON") ){
                    isKeepSetting = false;
                    inv = generateInventory();
                    event.getWhoClicked().openInventory(inv);
                } else if( isItemEquals( cu, Material.WOOL, ChatColor.YELLOW.toString() + "設定の保存: OFF") ){
                    isKeepSetting = true;
                    inv = generateInventory();
                    event.getWhoClicked().openInventory(inv);
                } else if( isItemEquals( cu, Material.EYE_OF_ENDER, ChatColor.YELLOW.toString() + "オールクリア") ){
                    resetJobSet();
                    inv = generateInventory();
                    event.getWhoClicked().openInventory(inv);
                }
            }
            event.setCancelled(true);
            ((Player)event.getWhoClicked()).updateInventory();
            RandomConfig.setRandomJobSet(jobSet);
            HashMap<RandomSelect.Config, Boolean> rc = new HashMap<RandomSelect.Config, Boolean>();
            rc.put(Config.KeepSetting, isKeepSetting);
            rc.put(Config.SenderIsGameMaster, isGameMaster);
            RandomConfig.setRandomConfig(rc);
        }
    }

    public void resetJobSet(){
        jobSet = new LinkedHashMap<Job, Integer>();
        for( Job j : Job.getDefaultSort() ){
            jobSet.put(j,0);
        }
    }

    public void giveJob( Player sender ){
        if( giveingLock ){
            sender.sendMessage("処理中です！");
            return;
        } else {
            giveingLock = true;
        }

        List<Job> selectedJobs = new ArrayList<Job>();
        for( Job j : jobSet.keySet() ){
            int jc = jobSet.get(j);
            for (int i = 0; i < jc; i++) {
                selectedJobs.add(j);
            }
        }

        for( Job j : selectedJobs ){
//            Bukkit.broadcastMessage(j.toString());
        }

        List<PlayerData> selectPlayers = new ArrayList<PlayerData>();
        selectPlayers.addAll(JinroPlayers.getNonJoinPlayers().values());
        selectPlayers.addAll(JinroPlayers.getPlayers().values());
        for( PlayerData pd : selectPlayers ){
            if( pd.getPlayingType().equals(PlayerData.PlayingType.Spectator)
            || pd.getPlayingType().equals(PlayerData.PlayingType.GameMaster)
                    || ( sender.getUniqueId().equals(pd.getUUID()) && isGameMaster )){
                continue;
            }
            int index = Utility.generateRandom(selectedJobs.size());

//            Bukkit.broadcastMessage(index + " / " + selectedJobs.size());

            Job currentJob = selectedJobs.get(index);
            selectedJobs.remove(index);
            pd.setJob(currentJob);
            JinroPlayers.setData(pd.getUUID(), pd);
            Player p = pd.getPlayer();
            p.sendMessage(ChatColor.RED + "=== " + ChatColor.WHITE + "あなたは " + currentJob.getColor() + "[" + currentJob.getJobName() + "]" + ChatColor.WHITE + " です。" + ChatColor.RED + " ===");

//            Bukkit.broadcastMessage(pd.getPlayer().getName() + ": " + pd.getFirstJob());
        }

        int amariCount = 2;
        for (int i = 0; i < amariCount; i++) {
            if( selectedJobs.size() == 0 ){
                break;
            }
            int index = Utility.generateRandom(selectedJobs.size());
            Job currentJob = selectedJobs.get(index);
            selectedJobs.remove(index);
            JinroPlayers.addNotJob(currentJob);
        }

        if( isGameMaster ){

            // JinroAdminPlayerListCmd.java と同じ
            // かきなおしたい(せつじつ
            List<Player> unassignedPlayers = new ArrayList<>();
            for(Player p : Bukkit.getOnlinePlayers()){
                PlayerData pd = JinroPlayers.getData(p, true);
                if(GameStatus.getStatus().equals(GameStatus.Ready)){
                    if( pd == null ){
                        unassignedPlayers.add(p);
                    }
                }
            }
            List<Player> spectatorPlayers = new ArrayList<>();
            for(PlayerData pd : JinroPlayers.getPlayers().values()){
                if( pd.getPlayingType().equals(PlayerData.PlayingType.Spectator) ){
                    spectatorPlayers.add(pd.getPlayer());
                }
            }
            sender.sendMessage( ChatColor.RED + "======================================");
            if( unassignedPlayers.size() != 0 ){
                sender.sendMessage( ChatColor.GOLD + "[未割り当てプレイヤー]");
                StringBuilder unassigned_sb = new StringBuilder();
                String unassigned_output = "";
                for( Player p : unassignedPlayers ){
                    unassigned_sb.append(p.getName()).append(", ");
                }
                if( (unassigned_sb.length() - 2) >= 0 ){
                    unassigned_output = unassigned_sb.substring(0, unassigned_sb.length() - 2);
                } else {
                    unassigned_output = unassigned_sb.toString();
                }
                sender.sendMessage( unassigned_output );
                sender.sendMessage( ChatColor.RED + "======================================");
            }
            if( spectatorPlayers.size() != 0 ){
                sender.sendMessage( ChatColor.GOLD + "[観戦プレイヤー]");
                StringBuilder spectator_sb = new StringBuilder();
                String spectator_output = "";
                for( Player p : spectatorPlayers ){
                    spectator_sb.append(p.getName()).append(", ");
                }
                if( (spectator_sb.length() - 2) >= 0 ){
                    spectator_output = spectator_sb.substring(0, spectator_sb.length() - 2);
                } else {
                    spectator_output = spectator_sb.toString();
                }
                sender.sendMessage( spectator_output );
                sender.sendMessage( ChatColor.RED + "======================================");
            }
            HashMap<UUID, PlayerData> pls = JinroPlayers.getPlayers();
            if(pls.size() != 0){
                sender.sendMessage( ChatColor.GOLD + "[参加プレイヤー]");
                for( PlayerData pd : pls.values() ){
                    String addText = "";
                    if(pd.getJob() != null){
                        if( !pd.getJob().equals( pd.getFirstJob() ) ){
                            addText = ChatColor.GRAY + "(First: " + pd.getFirstJob().getColor() + "[" + pd.getFirstJob().getJobName() + "]" + ChatColor.GRAY + ")";
                        }
                        sender.sendMessage( pd.getPlayer().getName() + ChatColor.GREEN + ": " + pd.getJob().getColor() + "[" + pd.getJob().getJobName() + "] " + addText );
                    }
                }
                sender.sendMessage( ChatColor.RED + "======================================");
            }
            List<Job> nj = JinroPlayers.getNotJob();
            if( nj.size() != 0 ){
                sender.sendMessage( ChatColor.GOLD + "[余りの役職]");
                StringBuilder nonjob_sb = new StringBuilder();
                String nonjob_output = "";
                for( Job j : nj ){
                    nonjob_sb.append(j.getColor()).append("[").append(j.getJobName()).append("]").append(ChatColor.WHITE).append(", ");
                }
                if( (nonjob_sb.length() - 2) >= 0 ){
                    nonjob_output = nonjob_sb.substring(0, nonjob_sb.length() - 2);
                } else {
                    nonjob_output = nonjob_sb.toString();
                }
                sender.sendMessage( nonjob_output );
                sender.sendMessage( ChatColor.RED + "======================================");
            }

        }

        if( selectedJobs.size() != 0 ){
            StringBuilder sb = new StringBuilder();
            String out = "";
            for( Job j : selectedJobs ){
                sb.append(j.getJobName()).append(", ");
            }
            if( (sb.length() - 2) >= 0 ){
                out = sb.substring(0, sb.length() - 2);
            } else {
                out = sb.toString();
            }
            sender.sendMessage(MConJinro.getPrefix() + ChatColor.YELLOW + out);
            sender.sendMessage(MConJinro.getPrefix() + ChatColor.GREEN + "上記の役職が余ってしまいました。");
            sender.sendMessage(MConJinro.getPrefix() + ChatColor.GREEN + "人数の調整をしてみてください。");
        } else {
            sender.sendMessage(MConJinro.getPrefix() + ChatColor.GREEN + "役職が余らず配布できました (*´ω｀*)");
        }
        sender.closeInventory();
        if( !isKeepSetting ){
            init();
        }
        inv = generateInventory();
        giveingLock = false;
    }

    public boolean isItemEquals(ItemStack is, Material material, String displayname){
        if(is.getType().equals(material)
                && is.getItemMeta().getDisplayName().equals(displayname)) {
            return true;
        }
        return false;
    }

}
