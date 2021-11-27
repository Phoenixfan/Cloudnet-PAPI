package de.phibsy.cloudnet.PAPI;

import java.util.concurrent.ExecutionException;

import org.bukkit.OfflinePlayer;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class CloudnetExpansion extends PlaceholderExpansion {

    private CloudnetPAPI cloudnetPAPI;

    public CloudnetExpansion(CloudnetPAPI cloudnetPAPI) {
        this.cloudnetPAPI = cloudnetPAPI;
    }

    @Override
    public String getIdentifier() {
        return "cloudnet";
    }

    @Override
    public String getAuthor() {
        return "Phibsy";
    }

    @Override
    public String getVersion() {
        return "1.2";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if(params.length() > 8) {
            if(params.startsWith("taskcount_")) {
                //return "Many people";
                String name = params.substring(10);
                try {
                    return "" + cloudnetPAPI.getTaskWatcher().getTaskCount(name).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
        switch (params) {
            case "servername":
                return cloudnetPAPI.getServername();
            case "state":
                //return BukkitCloudNetHelper.getState();
            case "taskcount":
                //return "Many people";
                return "" + cloudnetPAPI.getTaskWatcher().getTaskCount();
        }
        return null;
    }
}