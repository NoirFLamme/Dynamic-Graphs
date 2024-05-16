import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

public class GraphManipulation {
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

    /********************/
    private static void responseTimeVSnumClients(ArrayList<Long> responseTimes) {
        try {
            File analysisFile = new File("logs/client/numberOfClients_analysis" + ".txt");
            if (!analysisFile.exists()) {
                analysisFile.createNewFile();
            }
            FileWriter analysisFileWriter = new FileWriter(analysisFile, true);

            analysisFileWriter.write("Number Of Clients vs Average Response Time\n");

            long totalResponseTime = 0;
            int clientNum = 0;

            for (long responseTime : responseTimes) {
                clientNum++;
                totalResponseTime += responseTime;
                long averageResponseTime = totalResponseTime / clientNum;
                analysisFileWriter.write(clientNum + "\t" + averageResponseTime + "\n");
            }
            analysisFileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	private static void createClientLog(){
		String directoryPath = "logs\\client";

        // Create a Path object representing the directory
        Path dirPath = Paths.get(directoryPath);
        
        try {
            // Check if the directory already exists
            if (Files.exists(dirPath)) {
                // If it exists, delete it and create a new one
                Files.walkFileTree(dirPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    System.out.println("Deleted file: " + file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    System.out.println("Deleted directory: " + dir);
                    return FileVisitResult.CONTINUE;
                }
            });
                Files.createDirectory(dirPath);
                System.out.println("Directory overwritten successfully.");
            } else {
                // If it doesn't exist, simply create it
                Files.createDirectory(dirPath);
                System.out.println("Directory created successfully.");
            }
        } catch (IOException e) {
            // Handle exception if any
            System.err.println("Error: " + e.getMessage());
        }
	}

}
