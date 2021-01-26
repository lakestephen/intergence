package com.intergence.hgsrest.collection;

import com.intergence.hgsrest.runner.CollectorRunner;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by stephen on 15/09/2015.
 */
public abstract class SkeletalDiscoveryExecutor implements DiscoveryExecutor {

    private String collectorName;
    private int executeOrder = 1;

    @Override
    public int getExecuteOrder() {
        return executeOrder;
    }

    public void setExecuteOrder(int executeOrder) {
        this.executeOrder = executeOrder;
    }

    @Override
    public void setCollectorRunner(CollectorRunner collectorRunner) {
        collectorRunner.addDiscoveryExecutor(this);
    }

    @Override
    public String getCollectorName() {
        return checkNotNull(collectorName);
    }

    public void setCollectorName(String collectorName) {
        this.collectorName = collectorName;
    }

}
