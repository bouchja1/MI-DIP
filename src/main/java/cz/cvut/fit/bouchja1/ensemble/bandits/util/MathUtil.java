package cz.cvut.fit.bouchja1.ensemble.bandits.util;

import cz.cvut.fit.bouchja1.ensemble.bandits.Bandit;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author jan
 */
public class MathUtil {

    /**
     * Static utility methods for arrays (like java.util.Arrays, but more
     * useful).
     *
     */
    public static int argmax(List<Double> elems) {
        int bestIdx = -1;
        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < elems.size(); i++) {
            // ten z mapreduce zde dela double p = dist.nextMean(); ... my jen vytahneme ten navzorkovany inverse cumulative distribution
            // jinak s tim z MapReduce by to bylo ve tridach BayesianBandit a pak BetaBayesModel, BetaBinominalDistribution a BetaDistribution
            double elem = elems.get(i);
            if (elem > max) {
                max = elem;
                bestIdx = i;
            }
        }
        return bestIdx;
    }

    public static int secondArgMax(int banditIndexChoice, List<Double> roundInverseDistributions) {
        Iterator<Double> iter = roundInverseDistributions.iterator();
        int i = 0;
        while (iter.hasNext()) {
            iter.next();
            if (i == banditIndexChoice) {
                iter.remove();
                break;
            }
            i++;
        }
        return argmax(roundInverseDistributions);
    }

    public static int countBandits(Set<String> banditIds) {
        int count = 0;
        Iterator<String> bandits = banditIds.iterator();
        while (bandits.hasNext()) {
            String banditId = bandits.next();
            count++;
        }
        return count;
    }
}
