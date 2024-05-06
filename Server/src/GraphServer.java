import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class GraphServer implements GraphRMO{
    Graph graph;
    ServerLogger logger;
    public GraphServer(){
        super();
        readGraphFile("test.txt");
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

    public void readGraphFile(String path){
        // TODO: Construct Graph Function

        BufferedReader reader;
        graph = new Graph();
		try {
			reader = new BufferedReader(new FileReader("sample.txt"));
			String line = reader.readLine();

			while (line != null) {
				String[] pair = line.split(" ");
                if (pair[0] == "S") {
                    break;
                }
                int n1 = Integer.parseInt(pair[0]);
                int n2 = Integer.parseInt(pair[1]);

                graph.addNode(n1);
                graph.addNode(n2);

                graph.addEdge(n1, n2);
				// read next line
				line = reader.readLine();
			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
}
