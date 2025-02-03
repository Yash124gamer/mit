package commands;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

import utils.Database.Author;
import utils.Database.Database;
import utils.Database.Entry;
import utils.Database.Tree;
import utils.FileHandler.Config;
import utils.Workspace.Index;
import utils.Workspace.Refs;
import utils.Workspace.Workspace;

public class Commit {
    private Path currentPath;

    public Commit(Path path){
        currentPath = path;
    }

    public void run(String[] args) {
        Path mit_path = currentPath.resolve(".mit");
        Path db_path = mit_path.resolve("objects");
        Refs ref = new Refs(mit_path);
        Database db = new Database(db_path);
        Workspace workspace = new Workspace(currentPath);
        if (!Files.exists(workspace.path.resolve(".mit"))) {
            System.out.println("not a mit repository (or any of the parent directories)");
            return;
        }
        Index in = new Index(mit_path);
        List<Entry> newEntry = in.load();         // Getting Entries from the Index file
        if (newEntry.size() == 0) {
            System.out.println("Nothing to commit, Please add files to the Staging area to commit");
            return;
        }
        Tree root = new Tree();
        root.buildTree(newEntry);                 // Building Tree from the entries
        root.entries = root.sortEntriesByPathName(root.entries);
        root.Traverse(tree -> db.store(tree));    // Storing the tree object
        Scanner scan = new Scanner(System.in);    // making Scanner object for taking User Input and Commit message
        Config config = new Config(currentPath);
        config.getData();                         // Getting data from the config Files
        if (config.getName().length() < 1 && config.getEmail().length() < 1) {
            // Taking User's Details if not present in the Config file
            System.out.println("Please Enter your Name");
            String name = scan.nextLine();
            System.out.println("Please Enter your Email");
            String email = scan.nextLine();
            config.createEntries(name, email);
        }
        config.getData();
        System.out.println("Please Enter the commit Message");
        String message = scan.nextLine();
        scan.close();
        Author author = new Author(config.getName(), config.getEmail());
        String parent = ref.read_head();
        utils.Database.Commit cm = new utils.Database.Commit(message, parent, root, author);
        db.store(cm);                               // Storing the Commit object
        ref.update_head(cm.getOid());
        System.out.println(cm.toString());          // Printing out the commit with message
        if (parent == null) {
            System.out.println("root-commit");
        }
    }
}
