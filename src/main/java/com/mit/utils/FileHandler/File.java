package utils.FileHandler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.*;

public class File {
    private Path name;
    public File(Path name){
        this.name = name;
    }
    public void updateName(String name){
        this.name = this.name.resolve(name);
    }
    public void createDirectories(String[] names){
        try {
            for (String folder : names) {
                Path SubDirectory = this.name.resolve(folder);
                if (!dirExist(SubDirectory)) {
                    Files.createDirectory(SubDirectory);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean dirExist(Path dirPath){
        return Files.exists(dirPath);
    }
    public void createFile(String filename, Path directory) {
        try {
            Path filePath = directory.resolve(filename);

            // Create the file
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            
        }
    }
    public void writeData(byte[] data,Path filePath){
        try {
            // Ensure the parent directory exists
            Path parentDir = filePath.getParent();
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }

            // Write the byte array to the file
            Files.write(filePath, data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        } catch (IOException e) {
            System.err.println("Error writing data to file: " + e.getMessage());
        }
    }
    public void renameFile(Path oldFilePath, Path newFilePath) {
        try {
            Files.move(oldFilePath, newFilePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error renaming file: " + e.getMessage());
        }
    }
    public List<String> readData(){
        try {
            return Files.readAllLines(this.name);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    public void writeData(List<String> data){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.name.toString()))) {
            for (String line : data) {
                writer.write(line);
                writer.newLine();
            }
            writer.close();
        }catch (Exception e){

        }
    }
}
