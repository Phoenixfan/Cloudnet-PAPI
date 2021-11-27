package de.phibsy.cloudnet.PAPI;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.ext.bridge.BridgeServiceProperty;
import de.dytanic.cloudnet.wrapper.Wrapper;

public class TaskWatcher {
    private String taskName;
    private volatile int taskCount = 0;
    private ConcurrentHashMap<String, Pair<Integer, Long>> concurrentHashMap = new ConcurrentHashMap<>();
    private final ExecutorService pool = Executors.newFixedThreadPool(2);

    public TaskWatcher() {
        this.taskName = Wrapper.getInstance().getServiceId().getTaskName();
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
        return serviceInfoSnapshot.getServiceId().getTaskName().equalsIgnoreCase(name);
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
                int i = CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServices()
                        .stream()
                        .filter(serviceInfoSnapshot -> this.isTask(serviceInfoSnapshot, name))
                        .mapToInt(v -> v.getProperty(BridgeServiceProperty.ONLINE_COUNT).orElse(0))
                        .sum();
                concurrentHashMap.put(name, Pair.of(i, System.currentTimeMillis()));
                future.complete(i);
            }
        });
        return future;
    }

    private void supplyTaskCount() {
        pool.submit(() -> {
            taskCount = CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServices()
                    .stream()
                    .filter(serviceInfoSnapshot -> this.isTask(serviceInfoSnapshot, taskName))
                    .mapToInt(v -> v.getProperty(BridgeServiceProperty.ONLINE_COUNT).orElse(0))
                    .sum();
        });
    }

}