
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Random;
// import rmi.registery.GraphRMO;

public class Client extends Thread {
	private int currentRequestCount = 0;

	public void run() {
		try {
			System.err.println("ClientID: " + Thread.currentThread().getId());
			GraphRMO graphRMO = this.getGraphRMO();
			ArrayList<RequestClient> requests = this.generateRequestsBatch();
			Random randomGenerator = new Random();
			for (RequestClient request : requests) {
				long startTime = System.currentTimeMillis();
				String response = graphRMO.processRequests(request.getOperations(), (int) Thread.currentThread().getId()); // "A 1 3\nA 4 5\nQ 1 5\nQ 5 1\nF"
				long endTime = System.currentTimeMillis();
				long responseTime = endTime - startTime;
				request.setReponse(response);
				request.setResponseTime(responseTime);
				this.logInformation(request);
				int sleepTime = randomGenerator.nextInt(100);
				Thread.sleep(sleepTime);
			}
		} catch (NotBoundException | InterruptedException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private GraphRMO getGraphRMO() throws RemoteException, NotBoundException {
		String name = "GraphRMO";
		Registry registry = LocateRegistry.getRegistry("127.0.0.1", 1099);
		GraphRMO GraphRMO = (GraphRMO) registry.lookup(name);
		return GraphRMO;
	}

	private ArrayList<RequestClient> generateRequestsBatch() {
		ArrayList<RequestClient> requests = new ArrayList<RequestClient>();
		RequestGenerator requestGenerator = new RequestGenerator(0.4, 5, 15);
		int numOfRequests = 100;// randomGenerator.nextInt(10)+1;
		for (int i = 0; i < numOfRequests; i++) {
			RequestClient request = requestGenerator.getReqeust();
			requests.add(request);
		}

		return requests;
	}

	private void logInformation(RequestClient request) throws IOException {
		File logFile = new File("logs/client/log" + this.getId() + ".txt");
		if(!logFile.exists()) {
			logFile.createNewFile();
		}
		FileWriter logFileWriter = new FileWriter(logFile, true);
		logFileWriter.write("Request : \n");
		logFileWriter.write(request.getOperations());
		logFileWriter.write("\n Response : \n");
		logFileWriter.write(request.getReponse());
		logFileWriter.write("response time : " + request.getResponseTime() + "\n");
		logFileWriter.write("-------------------------------\n");
		logFileWriter.close();
	}

}
