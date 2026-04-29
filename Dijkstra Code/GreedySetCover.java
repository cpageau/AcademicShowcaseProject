import java.util.*;

public class GreedySetCover {

    public static <T> List<Set<T>> findCover(Set<T> universe, List<Set<T>> sets) {
        Set<T> uncovered = new HashSet<>(universe);
        List<Set<T>> cover = new ArrayList<>();

        while (!uncovered.isEmpty()) {
            Set<T> bestSet = null;
            int bestCount = 0;

            for (Set<T> set : sets) {
                int count = 0;

                for (T item : set) {
                    if (uncovered.contains(item)) {
                        count++;
                    }
                }

                if (count > bestCount) {
                    bestCount = count;
                    bestSet = set;
                }
            }

            if (bestSet == null) {
                throw new IllegalArgumentException("Some items cannot be covered: " + uncovered);
            }

            cover.add(bestSet);
            uncovered.removeAll(bestSet);
        }

        return cover;
    }
}