package com.github.yVapeSS.commands;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

public class BocaCommand implements CommandExecutor {

    private final Map<UUID, Long> cooldown = new WeakHashMap<>();
    private final Map<UUID, Integer> command = new WeakHashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cSomente jogadores podem executar este comando.");
            return false;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage("§cUso correto: /boca <raio>");
            return false;
        }

        int radius;
        try {
            radius = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage("§cO valor do raio deve ser um número.");
            return false;
        }

        MPlayer mPlayer = MPlayer.get(player);
        Faction faction = BoardColl.get().getFactionAt(PS.valueOf(player.getLocation()));

        if (command.getOrDefault(player.getUniqueId(), 0) >= 2) {
            cooldown.put(player.getUniqueId(), System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5L));
            command.remove(player.getUniqueId());
        }

        if (cooldown.containsKey(player.getUniqueId()) && cooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
            player.sendMessage("§cPor favor, aguarde 5 minutos para usar este comando novamente.");
            return true;
        }

        if (!faction.getId().equalsIgnoreCase("safezone") && !faction.getId().equalsIgnoreCase("warzone")) {
            if (!mPlayer.getFaction().getName().equalsIgnoreCase(faction.getName())) {
                player.sendMessage("§cVocê não está na sua facção.");
                return true;
            } else if (!faction.isNone() && !mPlayer.hasFaction()) {
                player.sendMessage("§cVocê não está na sua facção.");
                return true;
            } else {
                Location location = player.getLocation();
                int locationY = location.getBlockY() - radius;

                for (int y = location.getBlockY() - 1; y >= locationY; --y) {
                    Block block = player.getWorld().getBlockAt(location.getBlockX(), y, location.getBlockZ());
                    if (block.getType() != Material.BEDROCK && block.getType() != Material.ENDER_STONE && block.getType() != Material.AIR) {
                        block.breakNaturally();
                    }
                }

                player.getWorld().getBlockAt(player.getLocation().getBlockX(), locationY + 1, player.getLocation().getBlockZ()).setType(Material.TRIPWIRE);
                player.sendMessage("§aVocê quebrou §2" + radius + " §ablocos abaixo de você.");
                command.put(player.getUniqueId(), command.getOrDefault(player.getUniqueId(), 0) + 1);
                return true;
            }
        } else {
            player.sendMessage("§cVocê não pode usar este comando em safezone ou warzone.");
            return true;
        }
    }
}
