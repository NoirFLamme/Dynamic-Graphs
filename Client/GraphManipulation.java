import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GraphManipulation {
	public static void main(String args[]) {
		createClientLog();
		try {
			Client clientThread = new Client();
			clientThread.start();
		} catch (Exception e) {
			System.err.println("GraphService exception:");
			e.printStackTrace();
		}
	}

	private static void createClientLog(){
		String directoryPath = "logs/client";

        // Create a Path object representing the directory
        Path dirPath = Paths.get(directoryPath);
        
        try {
            // Check if the directory already exists
            if (Files.exists(dirPath)) {
                // If it exists, delete it and create a new one
                Files.deleteIfExists(dirPath);
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
