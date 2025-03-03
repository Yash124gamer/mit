import java.nio.file.Path;
import java.nio.file.Paths;

import commands.MitCommandExecutor;

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
        MitCommandExecutor executor = new MitCommandExecutor(currentPath);
        executor.execute(args[0], args);
    }
    
}