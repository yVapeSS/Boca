package com.github.yVapeSS;

import com.github.yVapeSS.commands.BocaCommand;
import com.github.yVapeSS.listeners.BocaListeners;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class BocaPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new BocaListeners(), this);

        this.getCommand("boca").setExecutor(new BocaCommand());
    }
}
