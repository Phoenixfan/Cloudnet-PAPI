package de.phibsy.cloudnet.PAPI;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.ext.bridge.BridgeServiceProperty;
import de.dytanic.cloudnet.wrapper.Wrapper;

public class TaskWatcher {
    private String taskName;

    public TaskWatcher() {
        this.taskName = Wrapper.getInstance().getServiceId().getTaskName();
    }

    private boolean isTask(ServiceInfoSnapshot serviceInfoSnapshot, String name) {
        return serviceInfoSnapshot.getServiceId().getTaskName().equalsIgnoreCase(name);
    }

    private int getCount(ServiceInfoSnapshot serviceInfoSnapshot) {
        if(serviceInfoSnapshot.getProperty(BridgeServiceProperty.ONLINE_COUNT).isPresent()) {
            return serviceInfoSnapshot.getProperty(BridgeServiceProperty.ONLINE_COUNT).get();
        } else {
            return 0;
        }
    }

    public int getTaskCount() {
        return CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServices()
                .stream()
                .filter(serviceInfoSnapshot -> this.isTask(serviceInfoSnapshot, taskName))
                .mapToInt(this::getCount)
                .sum();
    }

    public int getTaskCount(String name) {
        return CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServices()
                .stream()
                .filter(serviceInfoSnapshot -> this.isTask(serviceInfoSnapshot, name))
                .mapToInt(this::getCount)
                .sum();
    }
}
