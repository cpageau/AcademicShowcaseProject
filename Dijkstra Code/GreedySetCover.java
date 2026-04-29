import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class GreedySetCover {

    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.err.println("Usage: java GreedySetCover graphfile output.pth");
            System.exit(1);
        }

        Scanner s = new Scanner(new File(args[0]));
        HighwayGraph g = new HighwayGraph(s);
        s.close();

        Set<String> uncovered = new HashSet<>();

        for (HighwayVertex v : g.vertices) {
            uncovered.add(v.label);
        }

        ArrayList<HighwayEdge> chosenEdges = new ArrayList<>();

        while (!uncovered.isEmpty()) {

            HighwayEdge bestEdge = null;
            int bestCount = 0;

            for (HighwayVertex v : g.vertices) {
                HighwayEdge e = v.head;

                while (e != null) {
                    int count = 0;

                    String sourceLabel = g.vertices[e.source].label;
                    String destLabel = g.vertices[e.dest].label;

                    if (uncovered.contains(sourceLabel)) {
                        count++;
                    }

                    if (uncovered.contains(destLabel)) {
                        count++;
                    }

                    if (count > bestCount) {
                        bestCount = count;
                        bestEdge = e;
                    }

                    e = e.next;
                }
            }

            if (bestEdge == null) {
                System.err.println("Could not cover remaining vertices: " + uncovered);
                System.exit(1);
            }

            chosenEdges.add(bestEdge);

            uncovered.remove(g.vertices[bestEdge.source].label);
            uncovered.remove(g.vertices[bestEdge.dest].label);
        }

        PrintWriter pw = new PrintWriter(args[1]);

        HighwayEdge first = chosenEdges.get(0);
        HighwayVertex start = g.vertices[first.source];

        pw.println("START " + start.label + " " + start.point);

        for (HighwayEdge edge : chosenEdges) {
            pw.print(edge.label + " ");

            if (edge.shapePoints != null) {
                for (int i = 0; i < edge.shapePoints.length; i++) {
                    pw.print(edge.shapePoints[i] + " ");
                }
            }

            HighwayVertex dest = g.vertices[edge.dest];
            pw.println(dest.label + " " + dest.point);
        }

        pw.close();

        System.out.println("Wrote greedy set cover to " + args[1]);
        System.out.println("Edges chosen: " + chosenEdges.size());
    }

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