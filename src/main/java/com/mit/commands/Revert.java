package commands;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import utils.Database.Entry;
import utils.FileHandler.File;

public class Revert implements Command{
    private Path currentPath;
    private Repository repo;

    public Revert(Path path){
        currentPath = path;
        repo = new Repository(path);
        repo.initialise();
    }
    public void run(String[] dir){
        // Cheking if provided File path is correct or not
        if (dir.length >= 2 && !Files.exists(currentPath.resolve(dir[1]))) {
            System.out.println("The file "+dir[1]+" does not exist");
            return;
        }
        // Reads all data from the index file and returns a list of Entry objects,
        // where each Entry contains a file path and its corresponding file ID.
        List<Entry> entries = repo.INDEX.load();
        File fu = new File(currentPath);
        // for restoring individual file
        if (dir.length >= 2){  
            for (Entry entry : entries) {
                if (entry.getName().equals(dir[1])){
                    fu.writeData(repo.DATABASE.readObject(entry.getOid()), currentPath.resolve(entry.getPath()));
                }
            }   
        }
        // for restoring all files  from index
        else{
            for (Entry entry : entries) {
                fu.writeData(repo.DATABASE.readObject(entry.getOid()), currentPath.resolve(entry.getPath()));
            }
        }
    }
}
