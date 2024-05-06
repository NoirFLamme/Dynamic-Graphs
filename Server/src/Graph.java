import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Graph {
    private HashMap<Integer, List<Integer>> graph;
    ReadWriteLock lock = new ReentrantReadWriteLock();

    public void addNode(Integer id){
        graph.putIfAbsent(id, new ArrayList<>());
    }

    public void removeNode(Integer id){
        graph.values().stream().forEach(e -> e.remove(id));
        graph.remove(id);
    }

    public void addEdge(Integer id1, Integer id2){
        lock.writeLock().lock();
        graph.get(id1).add(id2);
        graph.get(id2).add(id1);
        lock.writeLock().unlock();
    }

    public void removeEdge(Integer id1, Integer id2){
        lock.writeLock().lock();
        List<Integer> e1 = graph.get(id1);
        List<Integer> e2 = graph.get(id2);

        if (e1 != null) {
            e1.remove(id2);
        }
        else if(e2 != null){
            e2.remove(id1);
        }
        lock.writeLock().unlock();
    }

    public int shortestPath(Integer s, Integer d) {
        lock.readLock().lock();

        Map<Integer, Integer> distances = new HashMap<>();
        PriorityQueue<Integer> pq = new PriorityQueue<>((v1, v2) -> distances.get(v1) - distances.get(v2));

        int start = s;
        int end = d;

        distances.put(start, 0);
        pq.add(start);

        while (!pq.isEmpty()) {
            int current = pq.poll();
            if (current == end){
                lock.readLock().unlock();
                return distances.get(end);
            }
            List<Integer> neighbors = graph.get(current);
            if (neighbors != null) {
                for (int neighbor : neighbors) {
                    int distanceToNeighbor = distances.get(current) + 1; // Assuming each edge has weight 1
                    if (!distances.containsKey(neighbor) || distanceToNeighbor < distances.get(neighbor)) {
                        distances.put(neighbor, distanceToNeighbor);
                        pq.add(neighbor);
                    }
                }
            }
        }
        lock.readLock().unlock();
        return -1; // No path found
    }

    public String printGraph() {
        StringBuilder builder = new StringBuilder();
        //foreach loop that iterates over the keys
        for (int v : graph.keySet())
        {
            builder.append(Integer.toString(v) + ": ");
            //foreach loop for getting the vertices
            for (int w : graph.get(v))
            {
                builder.append(Integer.toString(w) + " ");
            }
            builder.append("\n");
        }
        return (builder.toString());
    }
    
}
