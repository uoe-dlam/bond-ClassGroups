package au.edu.bond.classgroups.service;

import java.util.Collection;

/**
 * Created by shane on 3/09/2015.
 */
public class CacheCleaningService implements Cleanable {

    private Collection<Cleanable> services;

    public CacheCleaningService(Collection<Cleanable> services) {
        this.services = services;
    }

    public void clearCaches() {
        if(services != null) {
            for (Cleanable service : services) {
                service.clearCaches();
            }
        }
    }
}
