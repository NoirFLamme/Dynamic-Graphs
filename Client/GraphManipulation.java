import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

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
