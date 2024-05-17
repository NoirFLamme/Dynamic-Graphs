import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Random;


public class Client extends Thread {

	// Total response time for each client
	long totalResponseTime = 0;

	public void run() {
		try {
			System.err.println("ClientID: " + Thread.currentThread().getId());
			GraphRMO graphRMO = this.getGraphRMO();
			ArrayList<RequestClient> requests = this.generateRequestsBatch(5, 100, 0.4);
			Random randomGenerator = new Random();

			// Start from the first request
			for (RequestClient request : requests) {
				long startTime = System.currentTimeMillis();
				String response = graphRMO.processRequests(request.getOperations(), (int) Thread.currentThread().getId());
				long endTime = System.currentTimeMillis();
				long responseTime = endTime - startTime;

				request.setReponse(response);
				request.setResponseTime(responseTime);
				this.logInformation(request);

				// Add response time to the total response time per client
				totalResponseTime += responseTime;

				int sleepTime = randomGenerator.nextInt(100);
				Thread.sleep(sleepTime);
			}

		} catch (NotBoundException | InterruptedException | IOException e) {
			e.printStackTrace();
		}
	}

	public long getTotalResponseTime() {
		return totalResponseTime;
	}

	private GraphRMO getGraphRMO() throws RemoteException, NotBoundException {
		String name = "GraphRMO";
		Registry registry = LocateRegistry.getRegistry("127.0.0.1", 1099);
		GraphRMO graphRMO = (GraphRMO) registry.lookup(name);
		return graphRMO;
	}

	public ArrayList<RequestClient> generateRequestsBatch(int numOfQueries, int numOfRequests, double writingPercentage) {
		ArrayList<RequestClient> requests = new ArrayList<>();
		RequestGenerator requestGenerator = new RequestGenerator(writingPercentage, numOfQueries, 15);
		for (int i = 0; i < numOfRequests; i++) {
			RequestClient request = requestGenerator.getReqeust();
			requests.add(request);
		}
		return requests;
	}

	private void logInformation(RequestClient request) throws IOException {
		File logFile = new File("logs/client/log" + this.getId() + ".txt");
		if (!logFile.exists()) {
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