package de.phibsy.cloudnet.PAPI;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.ext.bridge.BridgeServiceProperty;
import de.dytanic.cloudnet.wrapper.Wrapper;

import java.util.ArrayList;
import java.util.List;

public class TaskWatcher {
    private List<ServiceInfoSnapshot> services = new ArrayList<>();
    private String taskName;

    public TaskWatcher() {
        this.taskName = Wrapper.getInstance().getServiceId().getTaskName();
        CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServices().stream()
                .filter(this::isTask)
                .forEach(this::putService);
    }

    private void putService(ServiceInfoSnapshot serviceInfoSnapshot) {
        this.services.add(serviceInfoSnapshot);
    }

    private boolean isTask(ServiceInfoSnapshot serviceInfoSnapshot) {
        return serviceInfoSnapshot.getServiceId().getTaskName().equals(taskName);
    }

    private boolean isOnlinePresent(ServiceInfoSnapshot serviceInfoSnapshot) {
        return serviceInfoSnapshot.getProperty(BridgeServiceProperty.ONLINE_COUNT).isPresent();
    }

    private int getCount(ServiceInfoSnapshot serviceInfoSnapshot) {
        if(this.isOnlinePresent(serviceInfoSnapshot)) {
            return serviceInfoSnapshot.getProperty(BridgeServiceProperty.ONLINE_COUNT).get();
        } else {
            return 0;
        }
    }

    public int getTaskCount() {
        return services.stream()
                .mapToInt(this::getCount)
                .sum();
    }
}
