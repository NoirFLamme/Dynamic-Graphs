public class Query {
    char operation;
    int node1;
    int node2;
    Graph graph;
    long duration;
    int result;

    public Query(char operation, int node1, int node2, Graph graph){
        this.operation = operation;
        this.node1 = node1;
        this.node2 = node2;
        this.graph = graph;
    }

    public void executeQuery(){
        long startTime = System.currentTimeMillis();
        switch (operation) {
            case 'Q':
                this.result = graph.findShortestPath(node1, node2);
                break;
            case 'A':
                graph.addEdge(node1, node2);
                break;
            case 'D':
                graph.removeEdge(node1, node2);
                break;
            default:
                break;
        }
        long endTime = System.currentTimeMillis();
        this.duration= (endTime - startTime) / 1000;
    }

    public long getDuration(){
        return this.duration;
    }

    public int getResult(){
        return this.result;
    }
}
