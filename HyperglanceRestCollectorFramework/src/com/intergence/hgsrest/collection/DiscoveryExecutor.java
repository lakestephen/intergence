package com.intergence.hgsrest.collection;

import com.intergence.hgsrest.model.update.ModelUpdate;
import com.intergence.hgsrest.runner.CollectorRunner;

import java.util.Comparator;

/**
 * Created by stephen on 27/01/2015.
 */
public interface DiscoveryExecutor {

    Comparator<DiscoveryExecutor> BY_EXECUTION_ORDER = new Comparator<DiscoveryExecutor>() {
        @Override
        public int compare(DiscoveryExecutor o1, DiscoveryExecutor o2) {
            return o1.getExecuteOrder() - o2.getExecuteOrder();
        }
    };


    void setCollectorRunner(CollectorRunner collectorRunner);
    int getExecuteOrder();

    String getCollectorName();

    void execute(ModelUpdate modelUpdate);
}
