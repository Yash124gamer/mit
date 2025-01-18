import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import utils.Database.Author;
import utils.Database.Blob;
import utils.Database.Commit;
import utils.Database.Tree;
import utils.Database.Entry;
import utils.Database.Database;
import utils.FileHandler.Config;
import utils.FileHandler.File;
import utils.Workspace.Refs;
import utils.Workspace.Workspace;

class App{
    public static void main(String[] args) {
        // Checking if User has given a command or not
        if(args.length < 1){       
            System.out.println("Please Provide a mit command");
            return;
        }
        // Getting the current working directory path
        // Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path currentPath = Paths.get("D:/workspace/first-repo");
        File fu = new File(currentPath);
        String command = args[0];
        switch (command) {
            // Instructions for the Init command
            case "init":
                // Checking if user has specified a directory path for initialising repository
                if (args.length > 1){
                    fu.createDirectories(new String[]{"\\"+args[1]});
                    currentPath = currentPath.resolve("\\"+args[1]);
                }
                if(fu.dirExist(currentPath.resolve(".mit"))){
                    System.out.println("mit repository already initialized in "+currentPath);
                    return;
                }
                fu.createDirectories(new String[]{".mit"});
                fu.updateName(".mit");
                fu.createDirectories(new String[]{"objects","refs"});
                System.out.println("Initialized empty mit repository in "+currentPath);
            break;
            case "commit":
                Path mit_path = currentPath.resolve(".mit");
                Path db_path = mit_path.resolve("objects");
                Refs ref = new Refs(mit_path);
                Database db = new Database(db_path);
                Workspace workspace = new Workspace(Paths.get("D:/workspace/first-repo"));  
                if(!Files.exists(workspace.path.resolve(".mit"))){
                    System.out.println("not a mit repository (or any of the parent directories)");
                    return;
                }
                List<Entry> entries = new ArrayList<>();
                List<Path> paths = workspace.listFiles();
                for (Path path : paths) {
                    if (Files.isRegularFile(currentPath.resolve(path))) {
                        String data = workspace.readFile(path);
                        Blob blob = new Blob(data);
                        db.store(blob);
                        entries.add(new Entry(path, blob.getOid()));
                    } else if (Files.isDirectory(currentPath.resolve(path)) || path.toString().contains("/") && path.isAbsolute()) { // Treat "New folder/as" as a directory.
                        entries.add(new Entry(path, "",true));
                    }
                }
                Tree root = Tree.build(entries);
                root.entries = root.sortEntriesByPathName(root.entries);
                root.Traverse(tree -> db.store(tree));
                Scanner scan = new Scanner(System.in);
                Config config = new Config(currentPath);
                config.getData();
                if(config.getName().length()<1 && config.getEmail().length()<1){
                    System.out.println("Please Enter your Name");
                    String name = scan.nextLine();
                    String email = scan.nextLine();
                    System.out.println("Please Enter your Email");
                    config.createEntries(name, email);
                }
                config.getData();
                System.out.println("Please Enter the commit Message");
                String message = scan.nextLine();
                Author author = new Author(config.getName(),config.getEmail());
                String parent = ref.read_head();
                Commit cm = new Commit(message,parent,root,author);
                db.store(cm);
                ref.update_head(cm.getOid());
                System.out.println(cm.toString());
                if (parent==null){
                    System.out.println("root-commit");
                }
            break;
            // If User provides an unknown command
            default:
                System.out.println("mit "+command+" is not a mit command");
                break;
        }
    }
    
}