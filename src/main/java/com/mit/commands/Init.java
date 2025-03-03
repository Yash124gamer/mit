package commands;

import java.nio.file.Path;
import utils.FileHandler.File;

public class Init implements Command{
    private Path currentPath;

    public Init(Path path){
        currentPath = path;
    }
    public void run(String[] dir){
        File fu = new File(currentPath);
        // Checking if user has specified a directory path for initialising repository
        if (dir.length > 1){
            fu.createDirectories(new String[]{"\\"+dir[1]});
            currentPath = currentPath.resolve("\\"+dir[1]);
        }
        if(fu.dirExist(currentPath.resolve(".mit"))){
            System.out.println("mit repository already initialized in "+currentPath);
            return;
        }
        fu.createDirectories(new String[]{".mit"});
        fu.updateName(".mit");
        fu.createDirectories(new String[]{"objects","refs"});
        System.out.println("Initialized empty mit repository in "+currentPath);
    }
}
