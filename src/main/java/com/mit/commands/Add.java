package commands;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import utils.Database.Blob;
import utils.Database.Database;
import utils.Workspace.Index;
import utils.Workspace.Workspace;

public class Add {
    private Path currentPath;

    public Add(Path path){
        currentPath = path;
    }
    
    public void run(String[] dir) {
        // Checking if user has specified a directory path for initialising repository
        List<Path> filePaths = new ArrayList<>();
        if (dir.length < 2) {
            System.out.println("Please provide a proper filename or file path to be added");
            return;
        // add all files in the working area if -a flag is used
        } else if (dir[1].equals("-a")) {
            Workspace workspace = new Workspace(currentPath);
            filePaths = workspace.listFiles();
        // for adding Individual file
        } else {
            Path file = Paths.get(dir[1]);
            // Checking if the provided file path exist or not
            if (!Files.exists(currentPath.resolve(file))) {
                System.out.println("The " + file.toString() + " does not exist");
                return;
            }
            filePaths.add(file);
        }
        Path mitPath = currentPath.resolve(".mit");
        Database DB = new Database(mitPath.resolve("objects"));
        Workspace wk = new Workspace(currentPath);
        Index index = new Index(mitPath);
        // Add the following file Paths to the Index file
        for (Path path : filePaths) {
            if (!Files.exists(currentPath.resolve(path)) || Files.isDirectory(currentPath.resolve(path))) {
                continue;
            }
            String data = wk.readFile(path);
            try {
                BasicFileAttributes stat = Files.readAttributes(currentPath.resolve(path), BasicFileAttributes.class);
                Blob blob = new Blob(data);
                DB.store(blob);
                index.add(path, blob.getOid(), stat);
                index.write_updates();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
