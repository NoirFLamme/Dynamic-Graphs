import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class stressTesting {
    static ArrayList<Long> responseTimePerClient = new ArrayList<>();
    public static void main(String args[]) {
        try {
            int numberOfClients = 15;
            Client[] clients = new Client[numberOfClients];
            for (int i = 0; i < clients.length; i++) {
                clients[i] = new Client();
                clients[i].start();
            }
            for (int i = 0; i < clients.length; i++) {
                clients[i].join();
                responseTimePerClient.add(clients[i].getTotalResponseTime());
            }

            responseTimeVSnumClients(responseTimePerClient);
        } catch (Exception e) {
            System.err.println("GraphService exception:");
            e.printStackTrace();
        }
    }

    private static void responseTimeVSnumClients(ArrayList<Long> responseTimes) {
        try {
            File analysisFile = new File("logs/client/stressTesting" + ".txt");
            if (!analysisFile.exists()) {
                analysisFile.createNewFile();
            }
            FileWriter analysisFileWriter = new FileWriter(analysisFile, false);

            analysisFileWriter.write("Number Of Clients vs Average Response Time\n");

            long totalResponseTime = 0;
            int clientNum = 0;

            for (int i = 0; i < responseTimes.size(); i++) {
                clientNum++;
                totalResponseTime += responseTimes.get(i);
                if (i >= 4) { // 5:15
                    long averageResponseTime = totalResponseTime / clientNum;
                    analysisFileWriter.write(clientNum + "\t" + averageResponseTime + "\n");
                }
            }
            analysisFileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
