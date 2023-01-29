package de.phibsy.cloudnet.PAPI;

import dev.derklaro.aerogel.Inject;
import eu.cloudnetservice.driver.provider.CloudServiceProvider;
import eu.cloudnetservice.driver.service.ServiceInfoSnapshot;
import eu.cloudnetservice.modules.bridge.BridgeServiceProperties;
import eu.cloudnetservice.wrapper.configuration.WrapperConfiguration;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class TaskWatcher {
    @Inject
    private CloudServiceProvider cloudServiceProvider;
    @Inject
    private WrapperConfiguration wrapperConfiguration;
    private final String taskName;
    private volatile int taskCount = 0;
    private final ConcurrentHashMap<String, Pair<Integer, Long>> concurrentHashMap = new ConcurrentHashMap<>();
    private final ExecutorService pool = Executors.newFixedThreadPool(2);

    public TaskWatcher() {
        this.taskName = wrapperConfiguration.serviceConfiguration().serviceId().taskName();
        Timer timer = new Timer();
        timer.schedule(
        new TimerTask() {
            @Override
            public void run() {
                supplyTaskCount();
            }
        }, 0, 20000);
    }

    private boolean isTask(ServiceInfoSnapshot serviceInfoSnapshot, String name) {
        return serviceInfoSnapshot.serviceId().taskName().equalsIgnoreCase(name);
    }

    public int getTaskCount() {
        return taskCount;
    }

    public Future<Integer> getTaskCount(String name) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        pool.submit(() -> {
            Pair<Integer, Long> pair = concurrentHashMap.get(name);
            if (pair != null && System.currentTimeMillis() - pair.getLeft() < 20000) {
                future.complete(pair.getRight());
            } else {
                int i = cloudServiceProvider.services()
                        .stream()
                        .filter(serviceInfoSnapshot -> this.isTask(serviceInfoSnapshot, name))
                        .mapToInt(v -> v.property(BridgeServiceProperties.ONLINE_COUNT))
                        .sum();
                concurrentHashMap.put(name, Pair.of(i, System.currentTimeMillis()));
                future.complete(i);
            }
        });
        return future;
    }

    private void supplyTaskCount() {
        pool.submit(() -> {
            taskCount = cloudServiceProvider.services()
                    .stream()
                    .filter(serviceInfoSnapshot -> this.isTask(serviceInfoSnapshot, taskName))
                    .mapToInt(v -> v.property(BridgeServiceProperties.ONLINE_COUNT))
                    .sum();
        });
    }

}