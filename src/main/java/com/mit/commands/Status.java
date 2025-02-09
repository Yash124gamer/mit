package commands;

import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import utils.Hasher;
import utils.Workspace.entry;

public class Status {
    private Path currentPath;
    private Repository repo;
    private final String RESET = "\u001B[0m";
    private final String RED = "\u001B[31m";
    private final String YELLOW = "\u001B[33m";

    public Status(Path path){
        currentPath = path;
        repo = new Repository(path);
        repo.initialise();
    }
    public void run(){
        List<Path> working_list = repo.WORKSPACE.listFiles();
        Map<String,entry> index_list = repo.INDEX.load_update();
        // Iterating Over List of files from Working area and comparing them with files in staging area to see which files changed 
        for (Path file : working_list) {
            // Skip if a path is a directory
            if (Files.isDirectory(currentPath.resolve(file))){
                continue;
            }
            String fileName = file.toString();
            String fileHash = get_fileHash(file);
            if (index_list.get(fileName) != null){
                if (!(index_list.get(fileName).fields.OID.equals(fileHash))){
                    System.out.println(YELLOW+file.getFileName()+" Modified"+RESET);
                }
            }else{
                System.out.println(YELLOW+file.getFileName()+" Untracked"+RESET);
            }
        }
        // Loop for finding delted files
        Set<Path> workingSet = new HashSet<>(working_list);
        for (String key : index_list.keySet()) {
            if (!workingSet.contains(Paths.get(key))) { 
                System.out.println(RED + key + " Deleted" + RESET);
            }
        }
    }
    // funtion to calculate hash of the content of a given file Path
    private String get_fileHash(Path file){
        byte[] fileContent = repo.WORKSPACE.readFile(file).getBytes();
        byte[] header = ("blob "+fileContent.length+"\0").getBytes();
        ByteBuffer buffer = ByteBuffer.allocate(header.length + fileContent.length);
        buffer.put(header);
        buffer.put(fileContent);
        return Hasher.hash(buffer.array());
    }
}
