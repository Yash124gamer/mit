package commands;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import utils.Workspace.entry;

public class Status implements Command{
    private Path currentPath;
    private Repository repo;
    private final String RESET = "\u001B[0m";
    private final String RED = "\u001B[31m";
    private final String YELLOW = "\u001B[33m";
    private final String GREEN = "\033[32m";

    public Status(Path path){
        currentPath = path;
        repo = new Repository(path);
        repo.initialise();
    }
    public void run(String[] args){
        List<Path> working_list = repo.WORKSPACE.listFiles();
        Map<String,entry> index_list = repo.INDEX.load_update();
        // Iterating Over List of files from Working area and comparing them with files in staging area to see which files changed 
        for (Path file : working_list) {
            // Skip if a path is a directory
            if (Files.isDirectory(currentPath.resolve(file))){
                continue;
            }
            String fileName = file.toString();
            String fileHash = repo.WORKSPACE.get_fileHash(file);
            if (index_list.get(fileName) != null){
                if (!(index_list.get(fileName).fields.OID.equals(fileHash))){
                    System.out.println(YELLOW + String.format("%-30s "+ "%s", file.getFileName(), "Modified") + RESET);
                }
            }else{
                System.out.println(GREEN + String.format("%-30s "+ "%s", file.getFileName(), "Untracked") + RESET);
                // System.out.println(GREEN+file.getFileName()+" Untracked"+RESET);
            }
        }
        // Loop for finding delted files
        Set<Path> workingSet = new HashSet<>(working_list);
        for (String key : index_list.keySet()) {
            if (!workingSet.contains(Paths.get(key))) { 
                System.out.println(RED + String.format("%-30s "+ "%s", key, "Deleted") + RESET);
            }
        }
    }
}
