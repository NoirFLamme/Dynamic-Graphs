import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Graph<Vertex> {

    public Map<Vertex, List<Vertex>> adjVerticesList= new HashMap<>();
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    // main method
    void addVertex(Vertex v) {
        readWriteLock.writeLock().lock();
        adjVerticesList.put( v, new LinkedList<>());
        readWriteLock.writeLock().unlock();
    }

    void removeVertex(Vertex v) {
        readWriteLock.writeLock().lock();
        adjVerticesList.remove(v);
        readWriteLock.writeLock().unlock();
    }

    void addEdge(Vertex s, Vertex d) {
        readWriteLock.writeLock().lock();
        if(!adjVerticesList.containsKey(s)){
            addVertex(s);
        }
        if(!adjVerticesList.containsKey(d)){
            addVertex(d);
        }
        adjVerticesList.get(s).add(d);
        readWriteLock.writeLock().unlock();
    }

    void removeEdge(Vertex s, Vertex d) {
        readWriteLock.writeLock().lock();
        List<Vertex> eV1 = adjVerticesList.get(s);
        if (eV1 != null)
            eV1.remove(d);
        readWriteLock.writeLock().unlock();
    }



    public String printGraph() {
        StringBuilder builder = new StringBuilder();
        //foreach loop that iterates over the keys
        for (Vertex v : adjVerticesList.keySet())
        {
            builder.append(v.toString() + ": ");
            //foreach loop for getting the vertices
            for (Vertex w : adjVerticesList.get(v))
            {
                builder.append(w.toString() + " ");
            }
            builder.append("\n");
        }
        return (builder.toString());
    }


    // Dijkstra's algorithm to find shortest path
    public int shortestPath(Vertex s, Vertex d) {
        readWriteLock.readLock().lock();
        Map<Vertex, Integer> distances = new HashMap<>();
        PriorityQueue<Vertex> pq = new PriorityQueue<>((v1, v2) -> distances.get(v1) - distances.get(v2));

        Vertex start = s;
        Vertex end = d;

        distances.put(start, 0);
        pq.add(start);

        while (!pq.isEmpty()) {
            Vertex current = pq.poll();
            if (current.equals(end)){
                readWriteLock.readLock().unlock();
                return distances.get(end);
            }
            List<Vertex> neighbors = adjVerticesList.get(current);
            if (neighbors != null) {
                for (Vertex neighbor : neighbors) {
                    int distanceToNeighbor = distances.get(current) + 1; // Assuming each edge has weight 1
                    if (!distances.containsKey(neighbor) || distanceToNeighbor < distances.get(neighbor)) {
                        distances.put(neighbor, distanceToNeighbor);
                        pq.add(neighbor);
                    }
                }
            }
        }
        readWriteLock.readLock().unlock();
        return -1; // No path found
    }


}
