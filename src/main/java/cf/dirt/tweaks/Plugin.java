package cf.dirt.tweaks;

import cf.dirt.tweaks.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.PrintWriter;
import java.io.StringWriter;

@SuppressWarnings("unused")
public final class Plugin extends JavaPlugin {

    @Override
    public void onEnable() {
        PluginManager manager = Bukkit.getPluginManager();

        try {
            manager.registerEvents(new ExplosionListener(), this);
            manager.registerEvents(new ParrotListener(), this);
            manager.registerEvents(new ArrowListener(), this);
            manager.registerEvents(new ThrowableListener(), this);
            manager.registerEvents(new CropsListener(), this);
            manager.registerEvents(new EnderDragonListener(), this);
            manager.registerEvents(new RespawnListener(this,
                    Bukkit.getWorld("world")
            ), this);
        }
        catch (NullPointerException exception) {
            StringWriter writer = new StringWriter();
            exception.printStackTrace(new PrintWriter(writer));
            getLogger().severe(
                    String.format(
                            "Failed to load configuration: \n%s",
                            writer.toString()
                    )
            );
            manager.disablePlugin(this);
        }
    }
}
