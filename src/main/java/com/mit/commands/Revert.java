package commands;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import utils.Database.Database;
import utils.Database.Entry;
import utils.FileHandler.File;
import utils.Workspace.Index;

public class Revert {
    private Path currentPath;

    public Revert(Path path){
        currentPath = path;
    }
    public void run(String[] dir){
        // Cheking if provided File path is correct or not
        if (dir.length >= 2 && !Files.exists(currentPath.resolve(dir[1]))) {
            System.out.println("The file "+dir[1]+" does not exist");
            return;
        }
        Index index = new Index(currentPath.resolve(".mit"));
        // Reads all data from the index file and returns a list of Entry objects,
        // where each Entry contains a file path and its corresponding file ID.
        List<Entry> entries = index.load();
        File fu = new File(currentPath);
        Database db = new Database(currentPath.resolve(".mit/objects"));
        // for restoring individual file
        if (dir.length >= 2){  
            for (Entry entry : entries) {
                if (entry.getName().equals(dir[1])){
                    fu.writeData(db.readObject(entry.getOid()), currentPath.resolve(entry.getPath()));
                }
            }   
        }
        // for restoring all files  from index
        else{
            for (Entry entry : entries) {
                fu.writeData(db.readObject(entry.getOid()), currentPath.resolve(entry.getPath()));
            }
        }
    }
}
