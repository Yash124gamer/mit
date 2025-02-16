import java.nio.file.Path;
import java.nio.file.Paths;

import commands.Add;
import commands.Commit;
import commands.Restore;
import commands.Init;
import commands.Logs;
import commands.Revert;
import commands.Status;

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
        String command = args[0];
        switch (command) {
            case "init":
                Init init = new Init(currentPath);
                init.run(args);
            break;
            case "add":
                Add add = new Add(currentPath);
                add.run(args);
            break;
            case "commit":
                Commit commit = new Commit(currentPath);
                commit.run(args);                
            break;
            case "revert":
                Revert revert = new Revert(currentPath);
                revert.run(args);
            break;
            case "status":
                Status status = new Status(currentPath);
                status.run();
            break;
            case "restore":
                Restore restore = new Restore(currentPath);
                restore.run(args);
            break;
            case "logs":
                Logs logs = new Logs(currentPath);
                logs.run();
            break;
            // If User provides an unknown command
            default:
                System.out.println("mit "+command+" is not a mit command");
                break;
        }
    }
    
}