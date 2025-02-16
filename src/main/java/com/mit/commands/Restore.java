package commands;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import utils.Database.Entry;
import utils.Database.Tree;
import utils.FileHandler.File;

public class Restore {
    private Path currentPath;
    private Repository repo;
    private File file_handler;

    public Restore(Path path){
        currentPath = path;
        repo = new Repository(path);
        repo.initialise();
        file_handler = new File(path);
    }
    public void run(String[] args){
        String commit_id = repo.REFS.read_head();
        if (args.length >= 2){
            if (is_valid_hash(args[1]))
                commit_id = args[1];
            else
                System.out.println("Please provide a valid commit ID");
        }
        // Getting all the files from the tree of the given commit 
        List<Entry> commit_Entries = new Tree().parse(repo.DATABASE.readObject(repo.REFS.get_prevoius_tree(commit_id)), "");
        // Getting all the files in the current working area
        List<Path> working_list = repo.WORKSPACE.listFiles();
        for (Entry entry: commit_Entries){
            // If file in working area and commit area are similiar then skip
            if (working_list.contains(entry.getPath())){
                working_list.remove(entry.getPath());
                if ((entry.getOid().equals(repo.WORKSPACE.get_fileHash(entry.getPath())))){
                    continue;
                }
            }
            // This will create a new file if doesn't exist and write or rewrite data in it
            file_handler.writeData(repo.DATABASE.readObject(entry.getOid()), currentPath.resolve(entry.getPath()));
        }
        // Deleting the remaing files
        for (Path path: working_list){
            file_handler.deleteFile(path);
        }
    }
    private boolean is_valid_hash(String hash){
        if (!(hash.length() == 40)){
            return false;
        }
        Path filePath = currentPath.resolve(".mit/objects/"+hash.substring(0, 2)+"/"+hash.substring(2));
        return Files.exists(filePath);
    }
}
