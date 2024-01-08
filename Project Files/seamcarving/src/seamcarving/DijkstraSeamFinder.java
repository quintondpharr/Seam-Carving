package seamcarving;

import graphs.Edge;
import graphs.Graph;
import graphs.shortestpaths.DijkstraShortestPathFinder;
import graphs.shortestpaths.ShortestPathFinder;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DijkstraSeamFinder implements SeamFinder {
    private final ShortestPathFinder<Graph<Point, Edge<Point>>, Point, Edge<Point>> pathFinder;

    public DijkstraSeamFinder() {
        this.pathFinder = createPathFinder();
    }

    protected <G extends Graph<V, Edge<V>>, V> ShortestPathFinder<G, V, Edge<V>> createPathFinder() {
        return new DijkstraShortestPathFinder<>();
    }

    @Override
    public List<Integer> findHorizontalSeam(double[][] energies) {
        // Convert the 2D energy array into a graph representation
        HorizontalGraph graph = new HorizontalGraph(energies);
        // Set the source and target points
        Point source = new Point(-1, -1);
        Point target = new Point(-2, -2);
        // Find the shortest path from source to target
        List<Edge<Point>> path = pathFinder.findShortestPath(graph, source, target).edges();
        // Convert the path into a seam representation
        return path.stream()
            .limit(path.size() - 1)
            .map(edge -> edge.to().y)
            .collect(Collectors.toList());
    }

    @Override
    public List<Integer> findVerticalSeam(double[][] energies) {
        // Convert the 2D energy array into a graph representation
        VerticalGraph graph = new VerticalGraph(energies);
        // Set the source and target points
        Point source = new Point(-1, -1);
        Point target = new Point(-2, -2);
        // Find the shortest path from source to target
        List<Edge<Point>> path = pathFinder.findShortestPath(graph, source, target).edges();
        // Convert the path into a seam representation
        return path.stream()
            .limit(path.size() - 1)
            .map(edge -> edge.to().x)
            .collect(Collectors.toList());
    }

    private class HorizontalGraph implements Graph<Point, Edge<Point>> {
        private double[][] energies;

        public HorizontalGraph(double[][] energies) {
            this.energies = energies;
        }

        public Collection<Edge<Point>> outgoingEdgesFrom(Point vertex) {
            List<Edge<Point>> edges = new ArrayList<>();
            if (vertex.equals(new Point(-1, -1))) { // if we are at the source/dummy vertex
                // Create edges from source to all vertices in the first column
                for (int y = 0; y < energies[0].length; y++) {
                    edges.add(new Edge<>(vertex, new Point(0, y), 0));
                }
            } else if (vertex.x < energies.length - 1) { // if we are not at the target vertex
                // Create edges to the next three possible vertices
                for (int dy = -1; dy <= 1; dy++) { // we can move up, down, or stay in the same row
                    int ny = vertex.y + dy; // the new y-coordinate
                    if (ny >= 0 && ny < energies[0].length) { // if the new y-coordinate is valid
                        edges.add(new Edge<>(vertex, new Point(vertex.x + 1, ny), energies[vertex.x][vertex.y]));
                    } // add an edge from the current vertex to the new vertex
                }
            } else if (vertex.x == energies.length - 1) { // if we are at the target vertex
                // Create an edge to the target
                edges.add(new Edge<>(vertex, new Point(-2, -2), energies[vertex.x][vertex.y]));
            } // add an edge from the current vertex to the target vertex
            return edges;
        }
    }

    private class VerticalGraph implements Graph<Point, Edge<Point>> { // same as HorizontalGraph, but for vertical
        private double[][] energies;

        public VerticalGraph(double[][] energies) {
            this.energies = energies;
        }

        public Collection<Edge<Point>> outgoingEdgesFrom(Point vertex) {
            List<Edge<Point>> edges = new ArrayList<>();
            if (vertex.equals(new Point(-1, -1))) {
                // Create edges from source to all vertices in the first row
                for (int x = 0; x < energies.length; x++) {
                    edges.add(new Edge<>(vertex, new Point(x, 0), 0));
                }
            } else if (vertex.y < energies[0].length - 1) {
                // Create edges to the next three possible vertices
                for (int dx = -1; dx <= 1; dx++) {
                    int nx = vertex.x + dx;
                    if (nx >= 0 && nx < energies.length) {
                        edges.add(new Edge<>(vertex, new Point(nx, vertex.y + 1), energies[vertex.x][vertex.y]));
                    }
                }
            } else if (vertex.y == energies[0].length - 1) {
                // Create an edge to the target
                edges.add(new Edge<>(vertex, new Point(-2, -2), energies[vertex.x][vertex.y]));
            }
            return edges;
        }
    }
}
