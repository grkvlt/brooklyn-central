package brooklyn.policy.loadbalancing;

import java.util.Set;

/**
 * Provides conveniences for searching for hot/cold containers in a provided pool model.
 * Ported from Monterey v3, with irrelevant bits removed.
 */
public class PolicyUtilForPool<C, E> {

    private final BalanceablePoolModel<C, E> model;

    public PolicyUtilForPool (BalanceablePoolModel<C, E> model) {
        this.model = model;
    }

    public C findColdestContainer(Set<C> excludedContainers) {
        return findColdestContainer(excludedContainers, null);
    }

    /**
     * Identifies the container with the maximum spare capacity (highThreshold - currentWorkrate),
     * returns null if none of the model's nodes has spare capacity.
     */
    public C findColdestContainer(Set<C> excludedContainers, LocationConstraint locationConstraint) {
        double maxSpareCapacity = 0;
        C coldest = null;

        for (C c : model.getPoolContents()) {
            if (excludedContainers.contains(c)
                    || (locationConstraint != null && !locationConstraint.isPermitted(model.getLocation(c))))
                continue;

            double highThreshold = model.getHighThreshold(c);
            double totalWorkrate = model.getTotalWorkrate(c);
            double spareCapacity = highThreshold - totalWorkrate;

            if (highThreshold == -1 || totalWorkrate == -1) {
                continue; // container presumably has been removed
            }
            if (spareCapacity > maxSpareCapacity) {
                maxSpareCapacity = spareCapacity;
                coldest = c;
            }
        }
        return coldest;
    }

    /**
     * Identifies the container with the maximum overshoot (currentWorkrate - highThreshold),
     * returns null if none of the model's  nodes has an overshoot.
     */
    public C findHottestContainer(Set<C> excludedContainers) {
        double maxOvershoot = 0;
        C hottest = null;

        for (C c : model.getPoolContents()) {
            if (excludedContainers.contains(c))
                continue;

            double totalWorkrate = model.getTotalWorkrate(c);
            double highThreshold = model.getHighThreshold(c);
            double overshoot = totalWorkrate - highThreshold;

            if (highThreshold == -1 || totalWorkrate == -1) {
                continue; // container presumably has been removed
            }
            if (overshoot > maxOvershoot) {
                maxOvershoot = overshoot;
                hottest = c;
            }
        }
        return hottest;
    }

}