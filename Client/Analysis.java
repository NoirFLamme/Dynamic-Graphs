import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Random;

public class Analysis extends Thread {

    public void run() {
        try {
            System.out.println("----------Analysis----------");
            Client client = new Client();
            System.err.println("ClientID: " + Thread.currentThread().getId());
            GraphRMO graphRMO = this.getGraphRMO();

            // Ensure the analysis files are reset at the start
            resetAnalysisFiles();

            // Try different numbers of batch size "number of queries" over 10 requests
            for (int i = 1; i <= 15; i++) {
                ArrayList<RequestClient> requests = client.generateRequestsBatch(i, 10, 0.4);
                Random randomGenerator = new Random();

                // Data collection for analysis
                ArrayList<Long> responseTimes = new ArrayList<>();

                // Start from the first request
                for (RequestClient request : requests) {
                    long startTime = System.currentTimeMillis();
                    String response = graphRMO.processRequests(request.getOperations(), (int) Thread.currentThread().getId());
                    long endTime = System.currentTimeMillis();
                    long responseTime = endTime - startTime;

                    request.setReponse(response);
                    request.setResponseTime(responseTime);
                    this.logInformation(request, i);

                    // Add response time to the list
                    responseTimes.add(responseTime);

                    int sleepTime = randomGenerator.nextInt(100);
                    Thread.sleep(sleepTime);
                }

                // Perform analysis
                responseTimeVSfreq(responseTimes, i);
            }

            for (double j = 0.2; j <= 1.0; j += 0.2) {
                ArrayList<RequestClient> requests = client.generateRequestsBatch(5, 10, j);
                Random randomGenerator = new Random();

                // Data collection for analysis
                ArrayList<Long> responseTimes = new ArrayList<>();

                // Start from the first request
                for (RequestClient request : requests) {
                    long startTime = System.currentTimeMillis();
                    String response = graphRMO.processRequests(request.getOperations(), (int) Thread.currentThread().getId());
                    long endTime = System.currentTimeMillis();
                    long responseTime = endTime - startTime;

                    request.setReponse(response);
                    request.setResponseTime(responseTime);
                    this.logInformation_perc(request, j);

                    // Add response time to the list
                    responseTimes.add(responseTime);

                    int sleepTime = randomGenerator.nextInt(100);
                    Thread.sleep(sleepTime);
                }

                // Perform analysis
                responseTimeVSperc(responseTimes, j);
            }

        } catch (NotBoundException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private void resetAnalysisFiles() throws IOException {
        // Overwrite the analysis files by writing empty content to them
        FileWriter frequencyFileWriter = new FileWriter("logs/client/frequency_analysis.txt", false);
        frequencyFileWriter.write("");
        frequencyFileWriter.close();

        FileWriter percentageFileWriter = new FileWriter("logs/client/percentage_analysis.txt", false);
        percentageFileWriter.write("");
        percentageFileWriter.close();

        for(int i = 1; i <= 15; i++){
            FileWriter file = new FileWriter("logs/client/FreqAnalysis_" + i + ".txt", false);
            file.write("");
            file.close();
        }

        for(double i = 0.2; i <= 1.0; i += 0.2){
            String formattedNumber = String.format("%.1f", i);
            FileWriter file = new FileWriter("logs/client/PercentageAnalysis_" + formattedNumber + ".txt", false);
            file.write("");
            file.close();
        }
    }

    private GraphRMO getGraphRMO() throws RemoteException, NotBoundException {
        String name = "GraphRMO";
        Registry registry = LocateRegistry.getRegistry("127.0.0.1", 1099);
        GraphRMO graphRMO = (GraphRMO) registry.lookup(name);
        return graphRMO;
    }

    private void logInformation(RequestClient request, int batchSize) throws IOException {
        File logFile = new File("logs/client/FreqAnalysis_" + batchSize + ".txt");
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

    private void logInformation_perc(RequestClient request, double percentage) throws IOException {
        String formattedNumber = String.format("%.1f", percentage);
        File logFile = new File("logs/client/PercentageAnalysis_" + formattedNumber + ".txt");
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

    private void responseTimeVSfreq(ArrayList<Long> responseTimes, int batchSize) {
        try {
            File analysisFile = new File("logs/client/frequency_analysis" + ".txt");
            if (!analysisFile.exists()) {
                analysisFile.createNewFile();
            }
            FileWriter analysisFileWriter = new FileWriter(analysisFile, true);

            if (analysisFile.length() == 0) {
                analysisFileWriter.write("Frequency of Requests vs Average Response Time\n");
                analysisFileWriter.write("Batch size\tAverage Response Time (ms)\n");
            }

            long totalResponseTime = 0;

            for (long responseTime : responseTimes) {
                totalResponseTime += responseTime;
            }
            long averageResponseTime = totalResponseTime / responseTimes.size();
            analysisFileWriter.write(batchSize + "\t" + averageResponseTime + "\n");
            analysisFileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void responseTimeVSperc(ArrayList<Long> responseTimes, double percentage) {
        try {
            File analysisFile = new File("logs/client/percentage_analysis" + ".txt");
            if (!analysisFile.exists()) {
                analysisFile.createNewFile();
            }
            FileWriter analysisFileWriter = new FileWriter(analysisFile, true);

            if (analysisFile.length() == 0) {
                analysisFileWriter.write("Percentage of Add/Delete Operations vs Average Response Time\n");
                analysisFileWriter.write("Add/Delete Percentage\tAverage Response Time (ms)\n");
            }

            long totalResponseTime = 0;

            for (long responseTime : responseTimes) {
                totalResponseTime += responseTime;
            }
            long averageResponseTime = totalResponseTime / responseTimes.size();

            double perc = percentage*100;
            String formattedNumber = String.format("%.1f", perc);

            analysisFileWriter.write(formattedNumber + "%\t\t" + averageResponseTime + "\n");
            analysisFileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        try {
            Analysis analysis = new Analysis();
            analysis.start();
        } catch (Exception e) {
            System.err.println("GraphService exception:");
            e.printStackTrace();
        }
    }
}
