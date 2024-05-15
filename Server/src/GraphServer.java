import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Properties;

import utils.NumericalChecker;

public class GraphServer implements GraphRMO{
    private Graph graph;
    private NumericalChecker numericalChecker;
    private static ServerLogger logger = ServerLogger.getInstance();
    private static int port;
    private static String host;
    private static int rmiPort;


    public GraphServer(){
        super();
        readGraphFile("test.txt");
        this.numericalChecker = new NumericalChecker();
    }

    @Override
    public String processRequests(String batch, int id) throws RemoteException {
        // TODO Auto-generated method stub
        logger.logInfo("Received New Request From Node " + Integer.toString(id));
        logger.logInfo("Processing....");
        Request request = new Request(id);
        ArrayList<Query> queries = splitOperations(batch);
        String result = request.processQueries(queries);

        return result;
    }

    public ArrayList<Query> splitOperations(String batch){
        ArrayList<Query> queires = new ArrayList<>();
        String[] sepBatch = batch.split("\n");
        for(String query : sepBatch){
            String[] entries = query.split(" ");
            if (entries[0].toLowerCase() == "f" || entries.length != 3) {
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
            logger.logInfo("Reading Graph From PATH: " + path);
			reader = new BufferedReader(new FileReader("sample.txt"));
			String line = reader.readLine();

			while (line != null) {
				String[] pair = line.split(" ");
                if (!numericalChecker.isNumeric(pair[0]) && pair[0].equals("S")) {
                    break;
                }
                int n1 = Integer.parseInt(pair[0]);
                int n2 = Integer.parseInt(pair[1]);

                graph.addVertex(n1);
                graph.addVertex(n2);

                graph.addEdge(n1, n2);
				// read next line
				line = reader.readLine();
			}

			reader.close();
            logger.logInfo("Finished Reading Graph");
		} catch (IOException e) {
            logger.logError("Error Reading From File", e);
			e.printStackTrace();
		}
    }

    public static void setupProperties() throws IOException{
        InputStream inputStream = null;
        try {
            Properties prop = new Properties();
            String propFileName = "server.properties";

            inputStream = new FileInputStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                logger.logError("property file '" + propFileName + "' not found in the classpath", new FileNotFoundException());
            }

            rmiPort = Integer.parseInt(prop.getProperty("GSP.rmiregistry.port"));
            host = prop.getProperty("GSP.server");
            port = Integer.parseInt(prop.getProperty("GSP.server.port"));

        } catch (Exception e) {
            logger.logError("Error ", e);
        } finally {
            assert inputStream != null;
            inputStream.close();
        }
    }
    

    public static void main(String[] args) throws IOException{
        setupProperties();
        logger.logInfo("Server Configurations Finished Loading");
        try {
            logger.logInfo("Initializing Server.....");
            System.setProperty("java.rmi.server.hostname", "127.0.0.1");
            GraphRMO graphRMO = new GraphServer();
            GraphRMO stub = (GraphRMO) UnicastRemoteObject.exportObject(graphRMO, port);

            Registry registry = LocateRegistry.createRegistry(rmiPort);
            registry.rebind("GraphRMO", stub);
            logger.logInfo("Server has successfully started with port " + Integer.toString(port));
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
