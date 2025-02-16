package utils.Workspace;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import utils.Hasher;

public class Workspace {
    public Path path;
    private final List<String> IGNORE = Arrays.asList(".", "..", ".mit", ".git");

    public Workspace(Path path){
        this.path = path;
    }
    // returns all the file paths in a directory
    public List<Path> listFiles() {

        try (Stream<Path> paths = Files.walk(path)) {
            return paths
                .filter(file -> !file.equals(path))
                .filter(file -> !isIgnored(file, path, IGNORE)) // Exclude ignored paths
                .map(this.path::relativize) // Make paths relative to the base directory
                .collect(Collectors.toList()); // Collect into a list
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
            return Collections.emptyList(); // Return an empty list in case of an error
        }
    }
    // Helper function to check if a file is ignored
    private boolean isIgnored(Path file, Path root, List<String> IGNORE) {
        Path relativePath = root.relativize(file); // Make path relative to root
        for (String ignored : IGNORE) {
            if (relativePath.toString().startsWith(ignored)) {
                return true; // Ignore files or directories starting with the ignored pattern
            }
        }
        return false;
    }
    // returns Data of a file after reading from it
    public String readFile(Path filePath){
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(this.path.resolve(filePath).toString()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n"); // Append the line and a newline character
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + filePath, e);
        }

        return content.toString();
    }
    public String get_fileHash(Path file) {
        byte[] fileContent = readFile(file).getBytes();
        byte[] header = ("blob " + fileContent.length + "\0").getBytes();
        ByteBuffer buffer = ByteBuffer.allocate(header.length + fileContent.length);
        buffer.put(header);
        buffer.put(fileContent);
        return Hasher.hash(buffer.array());
    }
}
