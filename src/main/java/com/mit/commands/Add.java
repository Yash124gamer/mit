package commands;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Map;
import utils.Database.Blob;
import utils.Workspace.entry;

public class Add {
    private Path currentPath;
    private Repository repo;

    public Add(Path path){
        currentPath = path;
        new Repository(path);
    }
    
    /**
     * Add Files from the Working Area to the Staging Area.
     *
     * If the length of 'dir' is less than 2, a message is printed to provide a proper filename or file path.
     * If the user prompts "-a" or "-A", all files are added to staging area.
     * Otherwise, the specified file is added to the staging area.
     *
     * @param dir An array containing the command and optional file path
     */
    public void run(String[] dir) {
        // Checking if user has specified a directory path for initialising repository
        if (dir.length < 2) {
            System.out.println("Please provide a proper filename or file path to be added");
            return;
        // add all files in the working area if -a flag is used
        } else if (dir[1].equals("-a") || dir[1].equals("-A")){
            add_all();
        // for adding Individual file
        } else {
            incremental_add(Paths.get(dir[1]));
        }
    }
    private void add_all(){
        List<Path> filePaths = repo.WORKSPACE.listFiles();
         // Add the following file Paths to the Index file
         for (Path path : filePaths) {
            Path resolvedPath = currentPath.resolve(path);
            // Skip if the file does not exist or is a directory
            if (!Files.exists(resolvedPath) || Files.isDirectory(resolvedPath)) {
                continue;
            }
            try {
                String data = repo.WORKSPACE.readFile(path);
                BasicFileAttributes stat = Files.readAttributes(resolvedPath, BasicFileAttributes.class);
                // Creating blob, store it in the database, and add it to the index
                Blob blob = new Blob(data);
                repo.DATABASE.store(blob);
                repo.INDEX.add(path, blob.getOid(), stat);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        repo.INDEX.write_updates();
    }
    private void incremental_add(Path file){
        Path resolvedPath = currentPath.resolve(file);
        // Checking if the provided file path exist or not
        if (!Files.exists(resolvedPath)) {
            System.out.println("The " + file.toString() + " does not exist");
            return;
        }
        // reading the file data and making a blob
        Blob blob = new Blob(repo.WORKSPACE.readFile(file));
        repo.DATABASE.store(blob);
        repo.INDEX.entries = repo.INDEX.load_update();
        // Flags to decide whether the file is new or its content has changed
        boolean newFile = true;
        boolean contentChanged = false;
        // Iterate over index entries to check for an existing entry
        for (Map.Entry<String, entry> entry : repo.INDEX.entries.entrySet()) { 
            if (file.toString().equals(entry.getKey())) {
                newFile = false;
                if (!entry.getValue().fields.OID.equals(blob.getOid())) {
                    contentChanged = true;
                }
                break;
            }
        }
        // If it's a new file or its content has changed, update the index
        if (newFile || contentChanged) {
            if (!newFile) {
                repo.INDEX.entries.remove(file.toString());
            }
            try {
                repo.INDEX.add(file, blob.getOid(), Files.readAttributes(currentPath.resolve(file), BasicFileAttributes.class));
                repo.INDEX.write_updates();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }            
        return;
    }
}
