package cf.dirt.tweaks;

import cf.dirt.tweaks.listeners.ParrotListener;
import cf.dirt.tweaks.listeners.ProjectileListener;
import cf.dirt.tweaks.listeners.ExplosionListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public final class Plugin extends JavaPlugin {

    @Override
    public void onEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new ExplosionListener(), this);
        manager.registerEvents(new ParrotListener(), this);
        manager.registerEvents(new ProjectileListener(), this);
    }
}
