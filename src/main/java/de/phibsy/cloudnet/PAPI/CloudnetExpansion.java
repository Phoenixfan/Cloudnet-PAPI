package de.phibsy.cloudnet.PAPI;

import java.util.concurrent.ExecutionException;

import org.bukkit.OfflinePlayer;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.jetbrains.annotations.NotNull;

public class CloudnetExpansion extends PlaceholderExpansion {

    private final CloudnetPAPI cloudnetPAPI;

    public CloudnetExpansion(CloudnetPAPI cloudnetPAPI) {
        this.cloudnetPAPI = cloudnetPAPI;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "cloudnet";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Phibsy";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.2";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (params.length() > 8) {
            if (params.startsWith("taskcount_")) {
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
                return cloudnetPAPI.getServerName();
            case "taskcount":
                return "" + cloudnetPAPI.getTaskWatcher().getTaskCount();
        }
        return null;
    }
}