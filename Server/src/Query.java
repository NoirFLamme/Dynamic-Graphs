import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Query {
    char operation;
    int node1;
    int node2;
    Graph graph;
    long duration;
    int result;

    private static ServerLogger logger = ServerLogger.getInstance();
    public Query(char operation, int node1, int node2, Graph graph){
        this.operation = operation;
        this.node1 = node1;
        this.node2 = node2;
        this.graph = graph;
    }

    public void executeQuery(int id){
        long startTime = System.currentTimeMillis();
        switch (operation) {
            case 'Q':
                logger.logInfo("Node: " + Integer.toString(id) + " , Calculating Shortest Path Between " + Integer.toString(node1) + " and " + Integer.toString(node2));
                this.result = graph.shortestPath(node1, node2);
                logger.logInfo("Node: " + Integer.toString(id) + " , The Shortest Path Between " + Integer.toString(node1) + " and " + Integer.toString(node2) + " is " + Integer.toString(result));
                break;
            case 'A':
                logger.logInfo("Node: " + Integer.toString(id) + " , Creating an Edge between " + Integer.toString(node1) + " and " + Integer.toString(node2));
                graph.addEdge(node1, node2);
                break;
            case 'D':
                logger.logInfo("Node: " + Integer.toString(id) + " , Removing Edge between " + Integer.toString(node1) + " and " + Integer.toString(node2));
                graph.removeEdge(node1, node2);
                break;
            default:
                break;
        }
        long endTime = System.currentTimeMillis();
        this.duration = (endTime - startTime) / 1000;
    }

    public long getDuration(){
        return this.duration;
    }

    public int getResult(){
        return this.result;
    }
}
