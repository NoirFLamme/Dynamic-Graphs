import java.rmi.RemoteException;
import java.util.ArrayList;

public class GraphServer implements GraphRMO{
    Graph graph;
    public GraphServer(){
        super();
        this.graph = readGraphFile("test");
    }

    @Override
    public String processRequests(String batch) throws RemoteException {
        // TODO Auto-generated method stub
        Request request = new Request();
        ArrayList<Query> queries = splitOperations(batch);
        String result = request.processQueries(queries);

        return result;
    }

    public ArrayList<Query> splitOperations(String batch){
        ArrayList<Query> queires = new ArrayList<>();
        String[] sepBatch = batch.split("\n");
        for(String query : sepBatch){
            String[] entries = query.split(" ");
            if (entries[0].toLowerCase() == "f") {
                break;
            }
            queires.add(new Query(entries[0].charAt(0), Integer.parseInt(entries[1]), Integer.parseInt(entries[2]), graph));
        }

        return queires;
    }

    public Graph readGraphFile(String path){
        // TODO: Construct Graph Function
        return null;
    }
    
}
