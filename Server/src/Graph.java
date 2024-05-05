import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Graph {
    private HashMap<Integer, List<Integer>> graph;

    public void addNode(Integer id){
        graph.putIfAbsent(id, new ArrayList<>());
    }

    public void removeNode(Integer id){
        graph.values().stream().forEach(e -> e.remove(id));
        graph.remove(id);
    }

    public void addEdge(Integer id1, Integer id2){
        graph.get(id1).add(id2);
        graph.get(id2).add(id1);
    }

    public void removeEdge(Integer id1, Integer id2){
        List<Integer> e1 = graph.get(id1);
        List<Integer> e2 = graph.get(id2);

        if (e1 != null) {
            e1.remove(id2);
        }
        else if(e2 != null){
            e2.remove(id1);
        }
    }

    public int findShortestPath(Integer id1, Integer id2){
        // TODO: Pick A Good Algorithm, Stating Why.
        return 0;
    }
}
