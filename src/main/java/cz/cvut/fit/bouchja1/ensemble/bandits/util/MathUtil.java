package cz.cvut.fit.bouchja1.ensemble.bandits.util;

import cz.cvut.fit.bouchja1.ensemble.bandits.Bandit;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

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

    public static <K, V extends Comparable<V>> Map<K, V> sortMapByValues(final Map<K, V> map) {
        Comparator<K> valueComparator = new Comparator<K>() {
            public int compare(K k1, K k2) {
                int compare = map.get(k2).compareTo(map.get(k1));
                if (compare == 0) {
                    return 1;
                } else {
                    return compare;
                }
            }
        };
        Map<K, V> sortedByValues = new TreeMap<K, V>(valueComparator);
        sortedByValues.putAll(map);
        return sortedByValues;
    }

    public static int[] sortedArgmax(List<Double> roundInverseDistributions) {
        int[] sortedBandits = new int[roundInverseDistributions.size()];
        Map<Integer, Double> distributionMap = new HashMap<>();
        for (int i = 0; i < roundInverseDistributions.size(); i++) {
            distributionMap.put(i, roundInverseDistributions.get(i));
        }

        ValueComparator bvc = new ValueComparator(distributionMap);
        TreeMap<Integer, Double> sortedMap = new TreeMap<>(bvc);
        sortedMap.putAll(distributionMap);

        int counter = 0;
        for (Map.Entry<Integer, Double> entry : sortedMap.entrySet()) {
            sortedBandits[counter] = entry.getKey();
            counter++;
        }
        return sortedBandits;
    }
}

class ValueComparator implements Comparator<Integer> {

    Map<Integer, Double> base;

    public ValueComparator(Map<Integer, Double> base) {
        this.base = base;
    }

    @Override
    public int compare(Integer a, Integer b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}
