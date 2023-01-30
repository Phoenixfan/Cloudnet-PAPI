package de.phibsy.cloudnet.PAPI;

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.wrapper.configuration.WrapperConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class CloudnetPAPI extends JavaPlugin {

    private String serverName;
    private TaskWatcher taskWatcher;

    private final WrapperConfiguration wrapperConfiguration = InjectionLayer.ext().instance(WrapperConfiguration.class);

    @Override
    public void onEnable() {
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            Bukkit.getLogger().severe("CloudnetPAPI: PlacerholderAPI required!");
            this.setEnabled(false);
        }
        this.taskWatcher = new TaskWatcher();
        this.serverName = wrapperConfiguration.serviceConfiguration().serviceId().name();
        new CloudnetExpansion(this).register();
    }

    public String getServerName() {
        return this.serverName;
    }

    public TaskWatcher getTaskWatcher() {
        return this.taskWatcher;
    }
}
