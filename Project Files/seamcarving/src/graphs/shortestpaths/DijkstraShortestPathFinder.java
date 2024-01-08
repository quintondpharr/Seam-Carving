package graphs.shortestpaths;

import priorityqueues.ExtrinsicMinPQ;
import priorityqueues.NaiveMinPQ;
import graphs.BaseEdge;
import graphs.Graph;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Computes shortest paths using Dijkstra's algorithm.
 * @see SPTShortestPathFinder for more documentation.
 */
public class DijkstraShortestPathFinder<G extends Graph<V, E>, V, E extends BaseEdge<V, E>>
    extends SPTShortestPathFinder<G, V, E> {

    protected <T> ExtrinsicMinPQ<T> createMinPQ() {
        return new NaiveMinPQ<>();
        /*
        If you have confidence in your heap implementation, you can disable the line above
        and enable the one below.
         */
        // return new ArrayHeapMinPQ<>();

        /*
        Otherwise, do not change this method.
        We override this during grading to test your code using our correct implementation so that
        you don't lose extra points if your implementation is buggy.
         */
    }

    @Override
    protected Map<V, E> constructShortestPathsTree(G graph, V start, V end) {

        if (start.equals(end)) {
            return new HashMap<>();
        }

        Set<V> known = new HashSet<>();
        Map<V, E> edgeTo = new HashMap<>();
        Map<V, Double> distTo = new HashMap<>();
        ExtrinsicMinPQ<V> pq = createMinPQ();

        known.add(start); // add start to known
        distTo.put(start, 0.0); // set distTo(start) = 0
        pq.add(start, 0.0); // set priority of start to 0

        while (!pq.isEmpty()) {
            V u = pq.removeMin(); // let u be the closest unknown vertex
            known.add(u); // add u to known
            if (u.equals(end)) { // if u is the end vertex, we're done
                break;
            }
            for (E v : graph.outgoingEdgesFrom(u)) { // for each edge (u,v) to unknown v with weight w:
                V w = v.to(); // let w be the vertex v
                if (!known.contains(w)) { // if w is unknown:
                    double newDist = distTo.get(u) + v.weight(); // set distTo(w) = distTo(u) + w
                    if (!distTo.containsKey(w)) { // if w is not in the priority queue:
                        distTo.put(w, newDist); // add w to the priority queue
                        edgeTo.put(w, v); // set edgeTo(w) = (u,v)
                        pq.add(w, newDist); // set priority of w to distTo(w)
                    } else if (newDist < distTo.get(w)) { // otherwise, if newDist < distTo(w):
                        distTo.put(w, newDist); // update distTo(w) = newDist
                        edgeTo.put(w, v); // set edgeTo(w) = (u,v)
                        pq.changePriority(w, newDist); // update priority of w to distTo(w)
                    }
                }
            }
        }

        return edgeTo;
    }

    @Override
    protected ShortestPath<V, E> extractShortestPath(Map<V, E> spt, V start, V end) {

        if (start.equals(end)) {
            return new ShortestPath.SingleVertex<>(start);
        }

        if (!spt.containsKey(end)) { // if end is not in the shortest paths tree, there is no path
            return new ShortestPath.Failure<>();
        }

        List<E> edges = new ArrayList<>(); // create a list of edges
        V curr = end; // let curr be the end vertex

        while (!curr.equals(start)) { // while curr is not the start vertex:
            E edge = spt.get(curr); // let edge be the edge from edgeTo(curr)
            edges.add(edge); // add edge to the list of edges
            curr = edge.from(); // let curr be the vertex edge.from()
        }

        Collections.reverse(edges); // backtrack from end to start to get the path
        return new ShortestPath.Success<>(edges); // return the path
    }

}
