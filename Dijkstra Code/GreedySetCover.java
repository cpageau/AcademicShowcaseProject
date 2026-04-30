import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class GreedySetCover {

    public static void main(String[] args) throws IOException {

        if (args.length != 5) {
            System.err.println("Usage: java GreedySetCover graphfile startVertex endVertex output.pth output.tmg");
            System.exit(1);
        }

        Scanner s = new Scanner(new File(args[0]));
        HighwayGraph g = new HighwayGraph(s);
        s.close();

        HighwayVertex start = getVertexByName(g, args[1]);
        HighwayVertex end = getVertexByName(g, args[2]);

        if (start == null) {
            System.err.println("No vertex found with label " + args[1]);
            System.exit(1);
        }

        if (end == null) {
            System.err.println("No vertex found with label " + args[2]);
            System.exit(1);
        }

        ArrayList<HighwayEdge> chosenEdges = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();

        int current = getVertexIndex(g, start.label);
        int endIndex = getVertexIndex(g, end.label);

        visited.add(current);

        while (current != endIndex) {

            HighwayEdge bestEdge = null;
            int bestNext = -1;
            double bestLength = Double.POSITIVE_INFINITY;

            HighwayEdge e = g.vertices[current].head;

            while (e != null) {

                int next;

                if (e.source == current) {
                    next = e.dest;
                } else {
                    next = e.source;
                }

                if (!visited.contains(next) && e.length < bestLength) {
                    bestLength = e.length;
                    bestEdge = e;
                    bestNext = next;
                }

                e = e.next;
            }

            if (bestEdge == null) {
                System.err.println("Could not reach " + end.label + " from " + start.label);
                System.exit(1);
            }

            chosenEdges.add(bestEdge);
            current = bestNext;
            visited.add(current);
        }

        writePthFile(g, chosenEdges, start, args[3]);
        writeTmgFile(g, chosenEdges, args[4]);

        System.out.println("Start vertex: " + start.label);
        System.out.println("End vertex: " + end.label);
        System.out.println("Wrote greedy path to " + args[3]);
        System.out.println("Wrote greedy path graph to " + args[4]);
        System.out.println("Edges chosen: " + chosenEdges.size());
    }

    public static HighwayVertex getVertexByName(HighwayGraph g, String name) {
        for (HighwayVertex v : g.vertices) {
            if (v.label.equals(name)) {
                return v;
            }
        }
        return null;
    }

    public static int getVertexIndex(HighwayGraph g, String name) {
        for (int i = 0; i < g.vertices.length; i++) {
            if (g.vertices[i].label.equals(name)) {
                return i;
            }
        }
        return -1;
    }

    public static void writePthFile(HighwayGraph g, ArrayList<HighwayEdge> chosenEdges, HighwayVertex start, String filename) throws IOException {

        PrintWriter pw = new PrintWriter(filename);

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
    }

    public static void writeTmgFile(HighwayGraph g, ArrayList<HighwayEdge> chosenEdges, String filename) throws IOException {

        PrintWriter pw = new PrintWriter(filename);

        pw.println("TMG 2.0 traveled");
        pw.println(g.vertices.length + " " + chosenEdges.size() + " 0");

        for (HighwayVertex v : g.vertices) {
            pw.println(v.label + " " + v.point);
        }

        for (HighwayEdge edge : chosenEdges) {
            pw.print(edge.source + " " + edge.dest + " " + edge.label);

            if (edge.shapePoints != null) {
                for (int i = 0; i < edge.shapePoints.length; i++) {
                    pw.print(" " + edge.shapePoints[i]);
                }
            }

            pw.println();
        }

        pw.close();
    }
}