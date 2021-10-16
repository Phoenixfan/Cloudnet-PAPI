package de.phibsy.cloudnet.PAPI;

import de.dytanic.cloudnet.wrapper.Wrapper;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class CloudnetPAPI extends JavaPlugin {

    private String servername = "";

    @Override
    public void onEnable() {
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            Bukkit.getLogger().severe("CloudnetPAPI: PlacerholderAPI required!");
            this.setEnabled(false);
        }
        this.servername = Wrapper.getInstance().getServiceId().getName();
        new CloudnetExpansion(this).register();
    }

    public String getServername() {
        return this.servername;
    }
}
