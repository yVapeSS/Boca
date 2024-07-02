package com.github.yVapeSS.listeners;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.ps.PS;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class BocaListeners implements Listener {

    @EventHandler
    public void onTNTDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof TNTPrimed) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block placedBlock = event.getBlockPlaced();
        Faction factionAtLocation = BoardColl.get().getFactionAt(PS.valueOf(player.getLocation()));

        if (placedBlock.getType() == Material.DISPENSER) {
            tratarColocacaoDispenser(event, player, placedBlock, factionAtLocation);
        } else if (placedBlock.getType() == Material.GLOWSTONE) {
            tratarColocacaoGlowstone(event, player, placedBlock);
        }
    }

    private void tratarColocacaoDispenser(BlockPlaceEvent event, Player player, Block placedBlock, Faction faction) {
        Chunk chunk = placedBlock.getChunk();
        int contadorDispenser = contarDispensersNoChunk(chunk);

        if (faction.getId().equalsIgnoreCase("none")) {
            if (contadorDispenser >= 6) {
                player.sendMessage("§cVocê não pode colocar mais de 6 dispensers nesta área.");
                event.setCancelled(true);
            }
        } else {
            if (contadorDispenser >= 96) {
                player.sendMessage("§cVocê não pode colocar mais de 96 dispensers nesta área.");
                event.setCancelled(true);
            }
        }
    }

    private void tratarColocacaoGlowstone(BlockPlaceEvent event, Player player, Block placedBlock) {
        Location localizacaoAcima = placedBlock.getLocation().add(0, 1, 0);
        Block blocoAcima = localizacaoAcima.getBlock();

        if (blocoAcima.getType().isSolid() || blocoAcima.getType() == Material.WATER || blocoAcima.getType() == Material.LAVA) {
            event.setCancelled(true);
            player.sendMessage("§cVocê não pode colocar este bloco abaixo de outros blocos.");
        } else {
            blocoAcima.setType(Material.REDSTONE_WIRE);
        }
    }

    private int contarDispensersNoChunk(Chunk chunk) {
        if (chunk == null || !chunk.isLoaded()) {
            return 0;
        }

        int contadorDispenser = 0;
        for (BlockState estadoBloco : chunk.getTileEntities()) {
            if (estadoBloco.getType() == Material.DISPENSER) {
                contadorDispenser++;
            }
        }
        return contadorDispenser;
    }
}
