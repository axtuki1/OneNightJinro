package io.github.axtuki1.onenightjinro.player;

import com.sun.org.apache.xerces.internal.xs.StringList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class RandomSelect implements Listener {

    Inventory inv;

    public RandomSelect(){
        inv = Bukkit.createInventory(null,9 * 6, "役数の設定");
    }

    public void openInventory( Player p ) {
        int baseX = 0, baseY = 0;
        for( Job j : Job.values() ){
            // Plus
            inv.setItem( baseX + baseY, new ItemStack(Material.STAINED_GLASS_PANE,1, (short) 5));
            // Now
            ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE,1, (short) 15);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(j.getColor() + j.getJobName());
            meta.setLore( Arrays.asList(j.getDescription()) );
            item.setItemMeta(meta);
            inv.setItem(baseX + ((baseY + 1) * 9), item );
            // Minus
            inv.setItem( baseX + ((baseY + 1) * 18), new ItemStack(Material.STAINED_GLASS_PANE,1, (short) 14) );
//            Bukkit.broadcastMessage("Job: " + j.getJobID() + "===============================");
//            Bukkit.broadcastMessage( "Plus: " + (baseX + baseY) );
//            Bukkit.broadcastMessage( "Now: " + (baseX + ((baseY + 1) * 9)) );
//            Bukkit.broadcastMessage( "Minus: " + (baseX + ((baseY + 1) * 18)) );
            if(baseX >= 8){
                baseY++;
                baseX = 0;
            } else {
                baseX++;
            }
        }
        p.openInventory(inv);
    }

    public void setJobSet(HashMap<Job, Integer> jobSet){

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJobGuiClick(InventoryClickEvent event) {
        if( event.getInventory().getTitle().equals(inv.getTitle()) ){
            if( event.getClickedInventory().getTitle().equals(inv.getTitle()) ){
                ItemStack cu = event.getCurrentItem();
                if(cu.getType().equals(Material.STAINED_GLASS_PANE)){
                    switch (cu.getData().getData()) {
                        case 5:
                            Bukkit.broadcastMessage("+");
                            break;
                        case 14:
                            Bukkit.broadcastMessage("-");
                            break;
                    }
                }
            }
            event.setCancelled(true);
            ((Player)event.getWhoClicked()).updateInventory();
        }
    }

}
