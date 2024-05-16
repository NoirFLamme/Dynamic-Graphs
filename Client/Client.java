import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;


public class Client extends Thread {

	// Total response time for each client
	long totalResponseTime = 0;

	public void run() {
		try {
			System.err.println("ClientID: " + Thread.currentThread().getId());
			GraphRMO graphRMO = this.getGraphRMO();
			ArrayList<RequestClient> requests = this.generateRequestsBatch();
			Random randomGenerator = new Random();

			// Data collection for analysis
			ArrayList<Long> responseTimes = new ArrayList<>();
			ArrayList<Integer> addDeletePercentages = new ArrayList<>();

			// Start from the first request
			for (RequestClient request : requests) {
				long startTime = System.currentTimeMillis();
				String response = graphRMO.processRequests(request.getOperations(), (int) Thread.currentThread().getId());
				long endTime = System.currentTimeMillis();
				long responseTime = endTime - startTime;

				request.setReponse(response);
				request.setResponseTime(responseTime);
				this.logInformation(request);

				// Add response time to the list
				responseTimes.add(responseTime);

				// Calculate and add add/delete operation percentage
				int addDeletePercentage = calculateAddDeletePercentage(request.getOperations());
				addDeletePercentages.add(addDeletePercentage);

				// Add response time to the total response time per client
				totalResponseTime += responseTime;

				int sleepTime = randomGenerator.nextInt(100);
				Thread.sleep(sleepTime);
			}

			// Perform analysis
			responseTimeVSfreq(responseTimes);
			responseTimeVSperc(responseTimes, addDeletePercentages);

		} catch (NotBoundException | InterruptedException | IOException e) {
			e.printStackTrace();
		}
	}

	public long getTotalResponseTime(){
		return totalResponseTime;
	}

	private GraphRMO getGraphRMO() throws RemoteException, NotBoundException {
		String name = "GraphRMO";
		Registry registry = LocateRegistry.getRegistry("127.0.0.1", 1099);
		GraphRMO graphRMO = (GraphRMO) registry.lookup(name);
		return graphRMO;
	}

	private ArrayList<RequestClient> generateRequestsBatch() {
		ArrayList<RequestClient> requests = new ArrayList<>();
		RequestGenerator requestGenerator = new RequestGenerator(0.4, 5, 15);
		int numOfRequests = 100;
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

	private void responseTimeVSfreq(ArrayList<Long> responseTimes) {
		try {
			File analysisFile = new File("logs/client/frequency_analysis" + this.getId() + ".txt");
			if (!analysisFile.exists()) {
				analysisFile.createNewFile();
			}
			FileWriter analysisFileWriter = new FileWriter(analysisFile, false);

			analysisFileWriter.write("Frequency of Requests vs Average Response Time\n");
			analysisFileWriter.write("Frequency\tAverage Response Time (ms)\n");

			int frequency = 0;
			long totalResponseTime = 0;

			for (long responseTime : responseTimes) {
				totalResponseTime += responseTime;
				long averageResponseTime = totalResponseTime / (frequency + 1L);
				analysisFileWriter.write(frequency + "\t" + averageResponseTime + "\n");
				frequency++;
			}

			analysisFileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void responseTimeVSperc(ArrayList<Long> responseTimes, ArrayList<Integer> addDeletePercentages) {
		try {
			File analysisFile = new File("logs/client/percentage_analysis" + this.getId() + ".txt");
			if (!analysisFile.exists()) {
				analysisFile.createNewFile();
			}
			FileWriter analysisFileWriter = new FileWriter(analysisFile, false);

			analysisFileWriter.write("Percentage of Add/Delete Operations vs Average Response Time\n");
			analysisFileWriter.write("Add/Delete Percentage\tAverage Response Time (ms)\n");

			long totalResponseTime = 0;

			for (int i = 0; i < responseTimes.size(); i++) {
				int percentage = addDeletePercentages.get(i);
				long responseTime = responseTimes.get(i);

				totalResponseTime += responseTime;
				long averageResponseTime = totalResponseTime / (i + 1L);

				analysisFileWriter.write(percentage + "\t" + averageResponseTime + "\n");
			}

			analysisFileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private int calculateAddDeletePercentage(String operations) {
		String[] ops = operations.split("\n");
		int totalOps = ops.length - 1;
		int addDeleteOps = 0;

		for (String op : ops) {
			if (op.startsWith("A") || op.startsWith("D")) {
				addDeleteOps++;
			}
		}

		return (int) ((addDeleteOps / (double) totalOps) * 100);
	}

}
