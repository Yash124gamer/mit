package commands;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

import utils.Database.Author;
import utils.Database.Entry;
import utils.Database.Tree;
import utils.FileHandler.Config;

public class Commit {
    private Path currentPath;
    private Repository repo;

    public Commit(Path path){
        currentPath = path;
        repo = new Repository(path);
        repo.initialise();
    }

    public void run(String[] args) {
        if (!Files.exists(currentPath.resolve(".mit"))) {
            System.out.println("not a mit repository (or any of the parent directories)");
            return;
        }
        List<Entry> newEntry = repo.INDEX.load();         // Getting Entries from the Index file
        if (newEntry.size() == 0) {
            System.out.println("Nothing to commit, Please add files to the Staging area to commit");
            return;
        }
        Tree root = new Tree();
        root.buildTree(newEntry);                 // Building Tree from the entries
        root.entries = root.sortEntriesByPathName(root.entries);
        root.Traverse(tree -> repo.DATABASE.store(tree));    // Storing the tree object
        // Checking if same tree doesn't get commited twice
        if (root.getOid().equals(get_prevoius_tree())){
            System.out.println("Changes already commited");
            return;
        }
        Scanner scan = new Scanner(System.in);    // making Scanner object for taking User Input and Commit message
        Config config = new Config(currentPath);
        config.getData();                         // Getting data from the config Files
        if (config.getName().length() < 1 && config.getEmail().length() < 1) {
            config.createEntries();
        }
        config.getData();
        System.out.println("Please Enter the commit Message");
        String message = scan.nextLine();
        scan.close();
        Author author = new Author(config.getName(), config.getEmail());
        String parent = repo.REFS.read_head();
        utils.Database.Commit cm = new utils.Database.Commit(message, parent, root, author);
        repo.DATABASE.store(cm);                               // Storing the Commit object
        repo.REFS.update_head(cm.getOid());
        System.out.println(cm.toString());          // Printing out the commit with message
        if (parent.equals("")) {
            System.out.println("root-commit");
        }
    }
    // Funtion that will return the previous commit's tree object id
    private String get_prevoius_tree(){
        String data = repo.REFS.read_head();
        if (data == null || data.equals("")){
            return "";
        }
        byte[] commitBytes = repo.DATABASE.readObject(data);
        byte[] prevTree = new byte[40];
        System.arraycopy(commitBytes, 6, prevTree, 0, 40);
        return new String(prevTree , StandardCharsets.UTF_8);
    }
}
